package com.bham.bc.components.environment;

import com.bham.bc.components.triggers.Trigger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Class which can load a map specifically created with a Tiled Map Editor
 *
 * @see <a href="https://www.mapeditor.org/">mapeditor.org</a>
 */
public class MapLoader {
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;

    private final ArrayList<Obstacle> OBSTACLES;
    private final EnumMap<Tileset, Integer> OFFSETS;
    private final HashMap<Integer, int[]> ANIMATIONS;
    private final ArrayList<Trigger> TRIGGERS;
    private final HashMap<Integer, String> TRIGGER_CLASSES;

    /**
     * Constructs Map Loader which loads a map from a JSON file
     * @param resourceName path to resource
     */
    public MapLoader(String resourceName) {
        OBSTACLES = new ArrayList<>();
        OFFSETS = new EnumMap<>(Tileset.class);
        ANIMATIONS = new HashMap<>();
        TRIGGERS = new ArrayList<>();
        TRIGGER_CLASSES = new HashMap<>();

        if (resourceName==null) return;
        try {
            loadMap(resourceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads map with obstacles
     *
     * @param resourceName path to resource
     * @throws Exception
     */
    private void loadMap(String resourceName) throws Exception {
        JSONObject jsonObject;
        InputStream is = MapLoader.class.getResourceAsStream(resourceName);

        assert is != null;
        jsonObject = new JSONObject(new JSONTokener(is));
        initMapProperties(jsonObject);

        // Each group contains multiple layers which make up 1 tileset
        JSONArray layerGroups = jsonObject.getJSONArray("layers");

        // For each group which represents 1 tileset, check its properties
        for(int i = 0; i < layerGroups.length(); i++) {
            JSONObject groupProperties = layerGroups.getJSONObject(i);

            String tilesetName = groupProperties.getString("name");
            JSONArray layers = groupProperties.getJSONArray("layers");

            // For each layer which represents 1 class, construct obstacles and triggers
            for(int j = 0; j < layers.length(); j++) {
                JSONObject layer = layers.getJSONObject(j);

                // If layer has "data", it contains obstacles
                // Otherwise if layer has "objects", it contains triggers
                if (layer.has("data")) {
                    String className = layer.getString("name");
                    JSONArray obstacleArray = layer.getJSONArray("data");

                    OBSTACLES.addAll(convertToObstacles(tilesetName, className, obstacleArray));
                } else if(layer.has("objects")) {
                    String packageName = layer.getString("name");
                    JSONArray triggerArray = layer.getJSONArray("objects");

                    TRIGGERS.addAll(convertToTriggers(packageName, triggerArray));
                }
            }
        }
    }

    /**
     * Checks the attributes about the map from the given JSON object
     *
     * @param jsonObject JSON object to check
     */
    void initMapProperties(JSONObject jsonObject) {
        mapWidth = jsonObject.getInt("width");
        mapHeight = jsonObject.getInt("height");
        tileWidth = jsonObject.getInt("tilewidth");
        tileHeight = jsonObject.getInt("tileheight");

        // Get all the tilesets that were used to build the map
        JSONArray tilesets = jsonObject.getJSONArray("tilesets");

        // Determine from where each tileset starts counting its tiles
        for(int i = 0; i < tilesets.length(); i++) {
            JSONObject tilesetProperties = tilesets.getJSONObject(i);

            String tilesetName = tilesetProperties.getString("name").toUpperCase();
            int offset = tilesetProperties.getInt("firstgid");

            try {
                OFFSETS.put(Tileset.valueOf(tilesetName), offset);
            } catch (IllegalArgumentException e) {
                if(!tilesetName.equals("TRIGGERS")) {
                    e.printStackTrace();
                }
            }

            if(!tilesetProperties.has("tiles")) continue;
            JSONArray tiles = tilesetProperties.getJSONArray("tiles");

            for(int j = 0; j < tiles.length(); j++) {
                // If tile has properties, it is a trigger so we need to map IDs with the class name
                if(tiles.getJSONObject(j).has("properties")) {
                    int tileID = tiles.getJSONObject(j).getInt("id");
                    JSONArray properties = tiles.getJSONObject(j).getJSONArray("properties");
                    for(int k = 0; k < properties.length(); k++) if(properties.getJSONObject(k).get("name").equals("className")) TRIGGER_CLASSES.put(offset + tileID, properties.getJSONObject(k).getString("value"));
                }
                // If tile has animations, add tile IDs which make up the animation to array and map it
                if(tiles.getJSONObject(j).has("animation")) {
                    int tileID = tiles.getJSONObject(j).getInt("id");
                    JSONArray jsonTileSequence = tiles.getJSONObject(j).getJSONArray("animation");
                    int[] tileSequence = new int[jsonTileSequence.length()];
                    for(int k = 0; k < tileSequence.length; k++) tileSequence[k] = jsonTileSequence.getJSONObject(k).getInt("tileid");

                    ANIMATIONS.put(tileID, tileSequence);
                }
            }
        }
    }

    /**
     * Converts JSON object attributes to Obstacles in the map
     *
     * @param tilesetName   name of the tileset used to take the tile image from
     * @param tileName      name of the class used to create an obstacle
     * @param obstacleArray array of obstacles in JSON format
     * @return list of generatable game obstacles
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public ArrayList<Obstacle> convertToObstacles(String tilesetName, String tileName, JSONArray obstacleArray) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Reflect the names of parameters for constructor
        Tileset tileset = Tileset.valueOf(tilesetName);
        int yo = tileset.getOffsetY();
        Class<?> cls = Class.forName("com.bham.bc.components.environment.Obstacle");

        ArrayList<Attribute> attributes = identifyAttributes(tileName);

        ArrayList<Obstacle> obstacleInstances = new ArrayList<>();
        Constructor<?> constructor = cls.getConstructor(int.class, int.class, ArrayList.class, Tileset.class, int[].class);

        // Construct a GenericObstacle for each existing tile in obstacleArray
        for(int i = 0; i < obstacleArray.length(); i++) {
            if(obstacleArray.getInt(i) > 0) {
                int x= (i % mapWidth) * tileWidth;
                int y = (i / mapWidth) * tileHeight - yo;

                int tileID = obstacleArray.getInt(i) - OFFSETS.get(tileset);
                int[] tileIDs = ANIMATIONS.containsKey(tileID) ? ANIMATIONS.get(tileID) : new int[] {tileID};

                obstacleInstances.add((Obstacle) constructor.newInstance(x, y, attributes, tileset, tileIDs));
            }
        }
        return obstacleInstances;
    }

    /**
     * Converts JSON object attributes to Triggers in the map
     *
     * @param packageName    name of the class used to create a trigger
     * @param triggerArray array of triggers in JSON format
     * @throws Exception   if construction of a class fails
     */
    public ArrayList<Trigger> convertToTriggers(String packageName, JSONArray triggerArray) throws Exception {
        ArrayList<Trigger> triggerInstances = new ArrayList<>();

        for(int i = 0; i < triggerArray.length(); i++) {
            JSONObject trigger = triggerArray.getJSONObject(i);
            String className = TRIGGER_CLASSES.get(trigger.getInt("gid"));
            Class<?> cls = Class.forName("com.bham.bc.components.triggers." + packageName + "." + className);
            Constructor<?> constructor = cls.getConstructors()[0];

            ArrayList<Object> params = new ArrayList<>();
            params.add(trigger.getInt("x"));
            params.add(trigger.getInt("y"));

            // Read attributes
            JSONArray attributes = trigger.getJSONArray("properties");

            for (int j = 0; j < attributes.length(); j++) {
                JSONObject attribute = attributes.getJSONObject(j);
                if(attribute.get("name").equals("className")) continue;
                params.add(attribute.getInt("value"));
            }
            triggerInstances.add((Trigger) constructor.newInstance(params.toArray()));
        }
        return triggerInstances;
    }

    /**
     * Identifies all the attributes a tile to be converted to obstacle instance should have
     * @param tileName name of the tile on which the attributes will depend
     * @return a list of attributes the obstacle should have or <i>WALKABLE</i> attribute by default
     */
    private ArrayList<Attribute> identifyAttributes(String tileName) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        switch(tileName) {
            case "Soft":
                attributes.add(Attribute.WALL);
                attributes.add(Attribute.BREAKABLE);
                return attributes;
            case "Hard":
                attributes.add(Attribute.WALL);
                return attributes;
            case "Impassable":
                return attributes;
            case "Covering":
                attributes.add(Attribute.RENDER_TOP);
                attributes.add(Attribute.WALKABLE);
                return attributes;
            case "HomeArea":
                attributes.add(Attribute.HOME_AREA);
                attributes.add(Attribute.WALKABLE);
                return attributes;
            case "HomeCenter":
                attributes.add(Attribute.HOME_CENTER);
                attributes.add(Attribute.HOME_AREA);
                attributes.add(Attribute.WALKABLE);
                return attributes;
            case "EnemySpawnArea":
                attributes.add(Attribute.ENEMY_SPAWN_AREA);
                attributes.add(Attribute.WALKABLE);
                return attributes;
            case "EnemySpawnCenter":
                attributes.add(Attribute.ENEMY_SPAWN_CENTER);
                attributes.add(Attribute.ENEMY_SPAWN_AREA);
                attributes.add(Attribute.WALKABLE);
                return attributes;
            default:
                attributes.add(Attribute.WALKABLE);
                return attributes;
        }
    }

    /**
     * Gets all obstacles
     * @return list of Obstacles
     */
    public ArrayList<Obstacle> getObstacles() {
        return OBSTACLES;
    }

    /**
     * Gets all respawning triggers
     * @return list of Triggers
     */
    public ArrayList<Trigger> getTriggers() {
        return TRIGGERS;
    }

    /**
     * Gets the width of the tile
     * @return width of any tile
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Gets the height of the tile
     * @return height of any tile
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * Gets the amount of tiles in X direction
     * @return number of tiles making up the total width of the map
     */
    public int getNumTilesX() {
        return mapWidth;
    }

    /**
     * Gets the amount of tiles in Y direction
     * @return number of tiles making up the total height of the map
     */
    public int getNumTilesY() {
        return mapHeight;
    }
}

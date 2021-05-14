package com.bham.bd.components.environment;

import com.bham.bd.components.triggers.Trigger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Class which can load a map specifically created with a Tiled Map Editor
 *
 * @see <a href="https://www.mapeditor.org/">mapeditor.org</a>
 */
public class MapLoader {
    /** Number of tiles the loaded map has in x axis */
    private int mapWidth;

    /** Number of tiles the loaded map has in y axis */
    private int mapHeight;

    /** The width of any tile (in pixels) */
    private int tileWidth;

    /** The height of any tile (in pixels) */
    private int tileHeight;
    
    /** {@link Obstacle} list the loaded map has (converted from tiles) */
    private final ArrayList<Obstacle> OBSTACLES;
    
    /** Map of every tileset and its offset used to load the map (each tileset used has a unique offset from where it starts counting its tiles) */
    private final EnumMap<Tileset, Integer> OFFSETS;

    /** Map of every animated tile and the array of positions of tiles that make up each animation */
    private final HashMap<Integer, int[]> ANIMATIONS;

    /** {@link Trigger} list the loaded map has (converted from tile objects) */
    private final ArrayList<Trigger> TRIGGERS;

    /** Map of trigger positions and corresponding class names */
    private final HashMap<Integer, String> TRIGGER_CLASSES;

    /**
     * Constructs Map Loader which loads a map from a JSON file
     * @param pathToMap path to map in JSON format
     */
    public MapLoader(String pathToMap) {
        OBSTACLES = new ArrayList<>();
        OFFSETS = new EnumMap<>(Tileset.class);
        ANIMATIONS = new HashMap<>();
        TRIGGERS = new ArrayList<>();
        TRIGGER_CLASSES = new HashMap<>();

        if (pathToMap == null) {
            System.err.println("The map could not be loaded because the path " + pathToMap + " is null");
            return;
        }

        try {
            loadMap(pathToMap);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads map with obstacles and triggers
     *
     * <p>This method creates a JSON object from a given JSON file and parses any obstacles and triggers it can provide. If the
     * constructors for obstacles or triggers could not be located, they are skipped and the errors are printed out.</p>
     *
     * @param pathToMap      path to map in JSON format
     * @throws JSONException thrown if some JSON property is requested but is not found in the actual file
     */
    private void loadMap(String pathToMap) throws JSONException {
        JSONObject jsonObject = new JSONObject(new JSONTokener(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(pathToMap))));
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

                    try {
                        OBSTACLES.addAll(convertToObstacles(tilesetName, className, obstacleArray));
                    } catch(ClassNotFoundException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else if(layer.has("objects")) {
                    String packageName = layer.getString("name");
                    JSONArray triggerArray = layer.getJSONArray("objects");

                    try {
                        TRIGGERS.addAll(convertToTriggers(packageName, triggerArray));
                    } catch(ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Checks the attributes about the map from the given JSON object
     *
     * <p>Firstly it checks for a unique offset each tileset starts to ident its tile positions and pushes the values to the
     * <i>OFFSET</i> map. If it is a <i>TRIGGER</i> tileset, tiles' positions are mapped to trigger classes, otherwise, if
     * it is a tileset defined in {@link Tileset}, tiles' are checked if they are animated. Otherwise, an error is handled
     * and nothing happens if the tileset is not defined as an enum.</p>
     *
     * @param jsonObject JSON object to check
     */
    void initMapProperties(JSONObject jsonObject) throws JSONException {
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
     * <p>This method goes through JSON obstacle array and builds an {@code Obstacle} object for every tile based on its position and name.
     * If, for some reason, the ID of the tile could not be located within the defined tileset picture in {@link Tileset} enum, the tile
     * is simply skipped and an error is printed. The error is not handled if the constructor of {@link Obstacle} cannot be declared.</p>
     *
     * @param tilesetName   name of the tileset used to take the tile image from
     * @param tileName      name of the class used to create an obstacle
     * @param obstacleArray array of obstacles in JSON format
     * @return list of obstacles to be added to the game map
     *
     * @throws ClassNotFoundException thrown when no constructor method has been found for {@link Obstacle} class
     * @throws NoSuchMethodException  thrown if {@link Obstacle} cannot be located under {@link com.bham.bd.components.environment} package
     */
    public ArrayList<Obstacle> convertToObstacles(String tilesetName, String tileName, JSONArray obstacleArray) throws ClassNotFoundException, NoSuchMethodException {
        // Reflect the names of parameters for constructor
        Tileset tileset = Tileset.valueOf(tilesetName);
        Class<?> cls = Class.forName("com.bham.bd.components.environment.Obstacle");

        ArrayList<Attribute> attributes = identifyAttributes(tileName);

        ArrayList<Obstacle> obstacleInstances = new ArrayList<>();
        Constructor<?> constructor = cls.getConstructor(int.class, int.class, ArrayList.class, Tileset.class, int[].class);

        // Construct an Obstacle for each existing tile in obstacleArray
        for(int i = 0; i < obstacleArray.length(); i++) {
            if(obstacleArray.getInt(i) > 0) {
                int x= (i % mapWidth) * tileWidth;
                int y = (i / mapWidth) * tileHeight;

                int tileID = obstacleArray.getInt(i) - OFFSETS.get(tileset);
                int[] tileIDs = ANIMATIONS.containsKey(tileID) ? ANIMATIONS.get(tileID) : new int[] {tileID};

                try{
                    obstacleInstances.add((Obstacle) constructor.newInstance(x, y, attributes, tileset, tileIDs));
                } catch (InvocationTargetException e) {
                    System.err.println("Invalid tile under " + tileset + " folder");
                    System.err.println("tileID is " + obstacleArray.getInt(i));
                } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return obstacleInstances;
    }

    /**
     * Converts JSON object attributes to Triggers in the map
     *
     * <p>This method goes through JSON trigger array and builds a {@code Trigger} object for every object based on its ID which is
     * mapped to the trigger's class name. If, for some reason, the parameters of a trigger object are not defined correctly, the
     * trigger is simply skipped and an error is printed. The error is not handled if the constructor of a specific trigger cannot
     * be declared.</p>
     *
     * @param packageName  name of the class used to create a trigger
     * @param triggerArray array of triggers in JSON format
     * @return list of triggers to be added to the game map
     *
     * @throws ClassNotFoundException thrown when no constructor method has been found for a specific {@code Trigger} class
     */
    public ArrayList<Trigger> convertToTriggers(String packageName, JSONArray triggerArray) throws ClassNotFoundException {
        ArrayList<Trigger> triggerInstances = new ArrayList<>();

        for(int i = 0; i < triggerArray.length(); i++) {
            JSONObject trigger = triggerArray.getJSONObject(i);
            String className = TRIGGER_CLASSES.get(trigger.getInt("gid"));
            Class<?> cls = Class.forName("com.bham.bd.components.triggers." + packageName + "." + className);
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

            try {
                triggerInstances.add((Trigger) constructor.newInstance(params.toArray()));
            } catch(IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
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

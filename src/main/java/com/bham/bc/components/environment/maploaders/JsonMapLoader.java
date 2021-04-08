package com.bham.bc.components.environment.maploaders;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.triggers.Trigger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;

public class JsonMapLoader extends MapLoader {
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;

    private EnumMap<Tileset, Integer> offsets;
    private HashMap<Integer, int[]> animations;

    /**
     * Constructs JSON Map Loader
     * @param resourceName path to resource
     */
    public JsonMapLoader(String resourceName) {
        super();
        if (resourceName==null) return;
        offsets = new EnumMap<>(Tileset.class);
        animations = new HashMap<>();

        try {
            loadMap(resourceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads map with obstacles and triggers
     *
     * @param resourceName path to resource
     * @throws Exception if resource is not found
     */
    private void loadMap(String resourceName) throws Exception {
        InputStream is = MapLoader.class.getResourceAsStream(resourceName);
        if (is == null) throw new NullPointerException("Cannot find resource file: " + resourceName);

        JSONObject jsonObject = new JSONObject(new JSONTokener(is));
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

                    obstacles.addAll(convertToObstacles(tilesetName, className, obstacleArray));
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
        // Determine animated tiles for each tileset
        for(int i = 0; i < tilesets.length(); i++) {
            JSONObject tilesetProperties = tilesets.getJSONObject(i);

            String tilesetName = tilesetProperties.getString("name").toUpperCase();
            if (tilesetName.equals("TRIGGERS")) continue;
            int offset = tilesetProperties.getInt("firstgid");
            offsets.put(Tileset.valueOf(tilesetName), offset);

            if(!tilesetProperties.has("tiles")) continue;

            JSONArray tiles = tilesetProperties.getJSONArray("tiles");

            for(int j = 0; j < tiles.length(); j++) {
                if(tiles.getJSONObject(j).has("animation")) {
                    int tileID = tiles.getJSONObject(j).getInt("id");
                    JSONArray jsonTileSequence = tiles.getJSONObject(j).getJSONArray("animation");
                    int[] tileSequence = new int[jsonTileSequence.length()];
                    for(int k = 0; k < tileSequence.length; k++) tileSequence[k] = jsonTileSequence.getJSONObject(k).getInt("tileid");

                    animations.put(tileID, tileSequence);
                }
            }
        }
    }

    /**
     * Converts JSON object attributes to Obstacles in the map
     *
     * @param tilesetName   name of the tileset used to take the tile image from
     * @param className     name of the class used to create an obstacle
     * @param obstacleArray array of obstacles in JSON format
     * @return list of generatable game obstacles
     * @throws Exception    if construction of a class fails
     */
    public List<Obstacle> convertToObstacles(String tilesetName, String className, JSONArray obstacleArray) throws Exception {
        // Reflect the names of parameters for constructor
        Tileset tileset = Tileset.valueOf(tilesetName);
        int yo = tileset.getyOffset();
        Class cls = Class.forName("com.bham.bc.components.environment.obstacles."+className);

        List<Obstacle> obstacleInstances = new ArrayList<>();
        Constructor constructor = cls.getConstructor(int.class, int.class, Tileset.class, int[].class);

        // Construct a GenericObstacle for each existing tile in obstacleArray
        for(int i = 0; i < obstacleArray.length(); i++) {
            if(obstacleArray.getInt(i) > 0) {
                int x= (i % mapWidth) * tileWidth;
                int y = (i / mapWidth) * tileHeight - yo;

                int tileID = obstacleArray.getInt(i) - offsets.get(tileset);
                int[] tileIDs = animations.containsKey(tileID) ? animations.get(tileID) : new int[] {tileID};

                obstacleInstances.add((Obstacle) constructor.newInstance(x, y, tileset, tileIDs));
            }
        }
        return obstacleInstances;
    }

    @Override
    public int getTileWidth() {
        return tileWidth;
    }

    @Override
    public int getTileHeight() {
        return tileHeight;
    }

    @Override
    public int getNumTilesX() {
        return mapWidth;
    }

    @Override
    public int getNumTilesY() {
        return mapHeight;
    }
}

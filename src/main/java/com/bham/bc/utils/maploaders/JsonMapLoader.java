package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.TILESET;
import com.bham.bc.entity.triggers.Trigger;
import javafx.embed.swing.JFXPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class JsonMapLoader extends MapLoader {

    int mapWidth = 0;
    int mapHeight = 0;
    int tileWidth = 0;
    int tileHeight = 0;

    private EnumMap<TILESET, Integer> offsets;


    public JsonMapLoader(String resourceName) {
        super();
        try { loadMap(resourceName); }
        catch (Exception e) { e.printStackTrace(); }


    }


    private void loadMap(String resourceName) throws Exception {
        InputStream is = MapLoader.class.getResourceAsStream(resourceName);
        if (is == null) throw new NullPointerException("Cannot find resource file " + resourceName);

        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        //System.out.println(object.toString());
        mapWidth = object.getInt("width");
        mapHeight = object.getInt("height");
        tileWidth = object.getInt("tilewidth");
        tileHeight = object.getInt("tileheight");

        offsets = new EnumMap<>(TILESET.class);
        offsets.put(TILESET.BASIC, 4061);
        offsets.put(TILESET.ASHLANDS, 1);
        offsets.put(TILESET.ATLANTIS, 1681);


        //JSONArray tilesets = object.getJSONArray("tilesets");
        //for(Object tileset: tilesets) {

        //}

        // Each group represents 1 tileset


        JSONArray layerGroups = object.getJSONArray("layers");

        for(int i = 0; i < layerGroups.length(); i++) {
            JSONObject group = layerGroups.getJSONObject(i);
            int offset = 0;

            if(group.getString("name").equals("BASIC")){
                offset = 4061;
            } else if (group.getString("name").equals("ASHLANDS")) {
                offset = 1;
            } else if (group.getString("name").equals("ATLANTIS")) {
                offset = 1681;
            }

            //System.out.println(group.toString());
            String onamae = group.getString("name");
            JSONArray lays = group.getJSONArray("layers");

            for(int j = 0; j < lays.length(); j++) {
                JSONObject lay = lays.getJSONObject(j);

                if (lay.has("data")) {  // If has obstacles
                    String name = lay.getString("name");

                    JSONArray obstacleArray = lay.getJSONArray("data");
                    List<GenericObstacle> obs = createMapObjects(onamae, name, obstacleArray);
                    obstacles.addAll(obs);

                } else if(lay.has("objects")) { // If has properties for trigger
                    createTriggers(lay.getString("name"), lay.getJSONArray("objects"));
                }
            }


        }


        //1 layer represents 1 class type, layer name must be class name
        /*
        JSONArray layers = object.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++)
        {
            //get class name and layout array
            JSONObject layer = layers.getJSONObject(i);

            //for tile layer
            if(layer.has("data")) {
                String name = layer.getString("name");

                JSONArray layout = layer.getJSONArray("data");

                List<GenericObstacle> objs = createMapObjects(name, layout);
                obstacles.addAll(objs);
            } else if (layer.has("objects")) {//for object layer
                createTriggers(layer.getString("name"), layer.getJSONArray("objects"));
            }

        }

         */
    }

    public void createTriggers(String className, JSONArray objs) throws Exception {
        //reflection
        Class cls = Class.forName("com.bham.bc.components.environment.triggers."+className);
        Constructor[] constructors = cls.getConstructors();

        //create 1 object(trigger) in each loop
        for (int i = 0; i < objs.length(); i++) {

            JSONObject obj = objs.getJSONObject(i);

            //parameters for the constructor
            List<Object> params = new ArrayList<Object>();
            params.add(obj.getInt("x"));
            params.add(obj.getInt("y"));

            //read properties
            JSONArray arr = obj.getJSONArray("properties");
            for (int j = 0; j < arr.length(); j++) {
                JSONObject tmp = arr.getJSONObject(j);
                params.add(tmp.getInt("value"));
            }

            //match parameter number with constructors to find the right one
            for (Constructor constructor1 : constructors) {
                if (constructor1.getParameterCount()==params.size()){
                    Object x = constructor1.newInstance(params.toArray());
                    triggerSystem.register((Trigger) x);
                    break;
                }
            }
        }
    }

    public List<GenericObstacle> createMapObjects(String tilesetName, String className, JSONArray layout) throws Exception {
        //reflection
        TILESET tileset = TILESET.valueOf(tilesetName);
        Class cls = Class.forName("com.bham.bc.components.environment.obstacles."+className);
        Constructor constructor = cls.getConstructor(int.class, int.class, TILESET.class, int[].class);


        System.out.println(tileset.toString() + " |||| " + cls.toString() + " ||| ");
        System.out.println(layout.length());

        List<GenericObstacle> objs = new ArrayList<>();
        //get positions
        for (int j = 0; j < layout.length(); j++)
        {
            if(layout.getInt(j)>0){
                int x= (j%mapWidth)*tileWidth;
                int y = (j/mapWidth)*tileHeight;
                int idx = layout.getInt(j) - offsets.get(tileset);
                objs.add((GenericObstacle) constructor.newInstance(x,y, tileset, new int[]{idx}));
            }
        }
        return objs;
    }



    public static void main(String[] args) throws Exception {
        new JFXPanel();
        JsonMapLoader mapLoader = new JsonMapLoader("/test.json");
        List<GenericObstacle> ls = mapLoader.getObstacles();
        for (GenericObstacle l : ls) {
            System.out.println(l);
        }
    }
}

package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.TILESET;
import com.bham.bc.entity.triggers.Trigger;
import javafx.embed.swing.JFXPanel;
import org.json.JSONArray;
import org.json.JSONObject;
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

    public JsonMapLoader(String resourceName){
        super();
        try { loadMap(resourceName); }
        catch (Exception e) { e.printStackTrace(); }

        offsets = new EnumMap<>(TILESET.class);
        offsets.put(TILESET.BASIC, 4061);
        offsets.put(TILESET.ASHLANDS, 1);
        offsets.put(TILESET.ATLANTIS, 1681);
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


        JSONArray tilesets = object.getJSONArray("tilesets");
        for(Object tileset: tilesets) {

        }

        // Each group represents 1 tileset
        JSONArray groups = object.getJSONArray("layers");

        for(int i = 0; i < groups.length(); i++) {
            JSONObject lays = groups.getJSONObject(i);
            int offset = 0;

            if(lays.getString("name").equals("BASIC")){
                offset = 4061;
            } else if (lays.getString("name").equals("ASHLANDS")) {
                offset = 1;
            } else if (lays.getString("name").equals("ATLANTIS")) {
                offset = 1681;
            }


            for(int j = 0; j < lays.length(); j++) {
                //JSONObject lay = lays.getJSONObject(j);
            }


        }


        //1 layer represents 1 class type, layer name must be class name
        JSONArray layers = object.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++)
        {
            //get class name and layout array
            JSONObject layer = layers.getJSONObject(i);

            //for tile layer
            if(layer.has("data")) {
                String name = layer.getString("name");
                System.out.println(name);

                JSONArray layout = layer.getJSONArray("data");

                List<GenericObstacle> objs = createMapObjects(name, layout);
                obstacles.addAll(objs);
            } else if (layer.has("objects")) {//for object layer
                createTriggers(layer.getString("name"), layer.getJSONArray("objects"));
            }

        }
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

    public List<GenericObstacle> createMapObjects(String className, JSONArray layout) throws Exception {
        //reflection
        Class cls = Class.forName("com.bham.bc.components.environment.obstacles."+className);
        Constructor constructor = cls.getConstructor(int.class, int.class, TILESET.class, int[].class);

        List<GenericObstacle> objs = new ArrayList<>();
        //get positions
        for (int j = 0; j < layout.length(); j++)
        {
            if(layout.getInt(j)>0){
                int x= (j%mapWidth)*tileWidth;
                int y = (j/mapWidth)*tileHeight;
                objs.add((GenericObstacle) constructor.newInstance(x,y, TILESET.ASHLANDS, new int[0]));
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

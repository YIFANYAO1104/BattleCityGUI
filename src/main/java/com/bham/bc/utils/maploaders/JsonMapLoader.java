package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.MapObject2D;
import com.bham.bc.components.environment.obstacles.Home;
import com.bham.bc.entity.triggers.Trigger;
import javafx.embed.swing.JFXPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class JsonMapLoader extends MapLoader {

    //private String resourceName = "/test.json";
    int mapWidth = 0;
    int mapHeight = 0;
    int tileWidth = 0;
    int tileHeight = 0;

    public JsonMapLoader(String resourceName){
        super();
        try {
            this.loadMap("/test.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadMap(String resourceName) throws Exception {
        InputStream is = MapLoader.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName);
        }

        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        //System.out.println(object.toString());
        mapWidth = object.getInt("width");
        mapHeight = object.getInt("height");
        tileWidth = object.getInt("tilewidth");
        tileHeight = object.getInt("tileheight");

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

                List<MapObject2D> objs = createMapObjects(name, layout);
                if (name.equals("Home")) {
                    home = (Home) objs.get(0);
                } else {
                    obstacles.addAll(objs);
                }
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

    public List<MapObject2D> createMapObjects(String className, JSONArray layout) throws Exception {
        //reflection
        Class cls = Class.forName("com.bham.bc.components.environment.obstacles."+className);
        Constructor constructor = cls.getConstructor(int.class, int.class);

        List<MapObject2D> objs = new ArrayList<MapObject2D>();
        //get positions
        for (int j = 0; j < layout.length(); j++)
        {
            if(layout.getInt(j)>0){
                int x= (j%mapWidth)*tileWidth;
                int y = (j/mapWidth)*tileHeight;
                objs.add((MapObject2D) constructor.newInstance(x,y));
            }
        }
        return objs;
    }



    public static void main(String[] args) throws Exception {
        new JFXPanel();
        JsonMapLoader mapLoader = new JsonMapLoader("/test.json");
        List<MapObject2D> ls = mapLoader.getObstacles();
        for (MapObject2D l : ls) {
            System.out.println(l);
        }
    }
}

package com.bham.bc.utils;

import com.bham.bc.components.environment.MapObject2D;
import com.bham.bc.components.environment.obstacles.Home;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLoader {



    private List<MapObject2D> obstacles = new ArrayList<MapObject2D>();
    private Home home;
    private TriggerSystem triggerSystem = new TriggerSystem();

    //private String resourceName = "/test.json";
    int mapWidth = 0;
    int mapHeight = 0;
    int tileWidth = 0;
    int tileHeight = 0;

    public void loadMap(String resourceName) throws Exception {
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

        JSONArray layers = object.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++)
        {
            //get class name and layout array
            JSONObject layer = layers.getJSONObject(i);
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
            } else if (layer.has("objects")) {
                createTriggers(layer.getString("name"), layer.getJSONArray("objects"));
            }

        }
    }

    public void createTriggers(String className, JSONArray objs) throws Exception {
        //reflection
        Class cls = Class.forName("com.bham.bc.components.environment.triggers."+className);
        Constructor constructor = cls.getConstructor(int.class, int.class, int.class, int.class);
//        List<MapObject2D> objs = new ArrayList<MapObject2D>();
        //get positions

        Map<String, Integer> properties = new HashMap<String, Integer>();

        for (int i = 0; i < objs.length(); i++) {
            //get objs
            JSONObject obj = objs.getJSONObject(i);

            //read properties
            JSONArray arr = obj.getJSONArray("properties");
            for (int j = 0; j < arr.length(); j++) {
                JSONObject tmp = arr.getJSONObject(j);
                properties.put(tmp.getString("name"),tmp.getInt("value"));
            }

            int arg1 = obj.getInt("x");
            int arg2 = obj.getInt("y");
            int arg3 = properties.get("health");
            int arg4 = properties.get("respawnCoolDown");

            Object x = constructor.newInstance(new Object[] { arg1, arg2, arg3, arg4 });
            triggerSystem.register((Trigger) x);
            properties.clear();
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
        MapLoader mapLoader = new MapLoader();
        mapLoader.loadMap("/test.json");
        List<MapObject2D> ls = mapLoader.getObstacles();
        for (MapObject2D l : ls) {
            System.out.println(l);
        }
    }

    public List<MapObject2D> getObstacles() {
        return obstacles;
    }
    public Home getHome() {
        return home;
    }

    public TriggerSystem getTriggerSystem() {
        return triggerSystem;
    }
}

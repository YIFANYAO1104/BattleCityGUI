package com.bham.bc.utils;

import javafx.embed.swing.JFXPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;

public class MapLoader {

    private String resourceName = "/test.json";
    int mapWidth = 0;
    int mapHeight = 0;
    int tileWidth = 0;
    int tileHeight = 0;

    public void loadMap(){
        InputStream is = MapLoader.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName);
        }

        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        //System.out.println(object.toString());
        int mapWidth = object.getInt("width");
        int mapHeight = object.getInt("height");
        int tileWidth = object.getInt("tilewidth");
        int tileHeight = object.getInt("tileheight");

        JSONArray layers = object.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++)
        {
            //get class name
            JSONObject layer = layers.getJSONObject(i);
            String name = layer.getString("name");
            System.out.println(name);

            //get positions
            JSONArray layout = layer.getJSONArray("data");
            for (int j = 0; j < layout.length(); j++)
            {
                if(layout.getInt(j)>0){
                    int x= (j%mapWidth)*tileWidth;
                    int y = (j/mapWidth)*tileHeight;
                    System.out.println("x="+j%mapWidth);
                    System.out.println("y="+j/mapWidth);
                }
            }
        }
    }

    public void createMapObjects(String className, JSONArray layout) throws Exception {
        new JFXPanel();
        Class cls = Class.forName("com.bham.bc.components.environment.obstacles."+className);
        Constructor constructor = cls.getConstructor(int.class, int.class);

        for (int j = 0; j < layout.length(); j++)
        {
            if(layout.getInt(j)>0){
                int x= (j%mapWidth)*tileWidth;
                int y = (j/mapWidth)*tileHeight;
                Object obj = constructor.newInstance(x,y);
            }
        }
    }



    public static void main(String[] args) throws Exception {
        new JFXPanel();
        Class cls = Class.forName("com.bham.bc.components.environment.obstacles.CommonWall");
        System.out.println(cls);
        Constructor constructor = cls.getConstructor(int.class, int.class);
        Object obj = constructor.newInstance(1,1);
        Object obj2 = constructor.newInstance(2,2);
        System.out.println(obj);
        System.out.println(obj2);
    }
}

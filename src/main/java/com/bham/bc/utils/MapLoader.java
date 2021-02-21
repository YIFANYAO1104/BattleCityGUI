package com.bham.bc.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class MapLoader {

    public static void main(String[] args) {
        String resourceName = "/test.json";
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
            JSONObject layer = layers.getJSONObject(i);
            String name = layer.getString("name");
            System.out.println(name);


            JSONArray layout = layer.getJSONArray("data");
            for (int j = 0; j < layout.length(); j++)
            {
                if(layout.getInt(j)>0){
                    System.out.println("x="+j%mapWidth);
                    System.out.println("y="+j/mapWidth);
                }
            }
        }
    }
}

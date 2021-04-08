package com.bham.bc.components.triggers;

import com.bham.bc.components.environment.MapLoader;
import com.bham.bc.components.triggers.powerups.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TriggerLoader {


    //private TriggerSystem triggerSystem = new TriggerSystem();

    private ArrayList<Trigger> triggers;

    private HashMap<Integer, String> classNameMap= new HashMap<>();

    public TriggerLoader(String resourceName) {
        triggers = new ArrayList<>();
        if (resourceName==null) return;
        try {
            loadMap(resourceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        initTeleportTrigger();
    }

//    public TriggerSystem getTriggerSystem() {
//        return triggerSystem;
//    }

    public ArrayList<Trigger> getTriggers() { return triggers; }


    public void initTriggerIDMap(JSONObject obj){
        JSONArray tilesets = obj.getJSONArray("tilesets");
        for(int i = 0; i < tilesets.length(); i++) {
            JSONObject tileset = tilesets.getJSONObject(i);
            if (tileset.get("name").equals("triggers")){
                int firstgid = tileset.getInt("firstgid");
                JSONArray tiles = tileset.getJSONArray("tiles");
                for(int j = 0; j < tiles.length(); j++) {
                    JSONObject tile = tiles.getJSONObject(j);
                    int id = tile.getInt("id");
                    JSONArray properties = tile.getJSONArray("properties");
                    for(int k = 0; k < properties.length(); k++) {
                        JSONObject property = properties.getJSONObject(k);
                        if (property.get("name").equals("className")){
                            classNameMap.put(firstgid+id,property.getString("value"));
                            System.out.println("id = "+(firstgid+id));
                            System.out.println("value = "+property.getString("value"));
                        }
                    }
                }

            }
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

        //get id-className Map from tileset
        initTriggerIDMap(jsonObject);


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
                if(layer.has("objects")) {
                    String packageName = layer.getString("name");
                    JSONArray triggerArray = layer.getJSONArray("objects");

                    addTriggers(packageName,triggerArray);
                }
            }
        }
    }


    /**
     * Converts JSON object attributes to Triggers in the map
     *
     * @param packageName    name of the class used to create a trigger
     * @param triggerArray array of triggers in JSON format
     * @throws Exception   if construction of a class fails
     */
    public void addTriggers(String packageName, JSONArray triggerArray) throws Exception {
        // Reflect the names of parameters for constructor
        for(int i = 0; i < triggerArray.length(); i++) {
            JSONObject trigger = triggerArray.getJSONObject(i);
            String className = classNameMap.get(trigger.getInt("gid"));
            Class cls = Class.forName("com.bham.bc.components.triggers."+packageName+"."+className);

            Constructor constructor = cls.getConstructors()[0];

            List<Object> params = new ArrayList<>();
            params.add(trigger.getInt("x"));
            params.add(trigger.getInt("y"));
            // Read attributes
            JSONArray attributes = trigger.getJSONArray("properties");

            for (int j = 0; j < attributes.length(); j++) {
                JSONObject attribute = attributes.getJSONObject(j);
                if (attribute.get("name").equals("className")) continue;
                params.add(attribute.getInt("value"));
            }
            System.out.println(params);

            //triggerSystem.register((Trigger) constructor.newInstance(params.toArray()));
            triggers.add((Trigger) constructor.newInstance(params.toArray()));
        }
    }





    /**
     * Initializes all the triggers
     */
    private void initTeleportTrigger() {
//        HealthGiver hg = new HealthGiver(400,400,10,10);
//        HealthGiver hg1 = new HealthGiver(600,400,10,10);
//        WeaponGenerator w = new WeaponGenerator(466, 466, Weapon.ArmourGun, 30,30,30);
        //        triggerSystem.register(w);
//        Immune I = new Immune(325,325,10,10);
//        TripleBullet T = new TripleBullet(350,350,10,10);
//        Freeze F = new Freeze(375,375,5,10);
//        triggerSystem.register(hg);
//        triggerSystem.register(hg1);

//        triggerSystem.register(I);
//        triggerSystem.register(T);
//        triggerSystem.register(F);


//        SpeedTrigger sp = new SpeedTrigger(380,400,20,100);
//        ArmorTrigger at = new ArmorTrigger(500,570,500,100);
//        TrappedTrigger tt = new TrappedTrigger(630,400,100);
//        UntrappedTrigger ut = new UntrappedTrigger(560,460,100);

//        SpeedTrigger sp2 = new SpeedTrigger(350,670,12,100);
//        LandmineTrigger l1 = new LandmineTrigger(470,540,100);
//        SpeedTrigger sp1 = new SpeedTrigger(640,630,3,100);
//        TrappedTrigger tt2 = new TrappedTrigger(370,650,100);
//        LandmineTrigger l2 = new LandmineTrigger(403, 630,100);
//        StateTrigger s1 = new StateTrigger(440,500,100);


//        triggerSystem.register(at);
//        triggerSystem.register(sp);
//        triggerSystem.register(tt);
//        triggerSystem.register(ut);
//        triggerSystem.register(sp1);
//        triggerSystem.register(tt2);
//        triggerSystem.register(l2);
//        triggerSystem.register(l1);
//        triggerSystem.register(s1);
//        triggerSystem.register(sp2);


        TeleportTrigger t1 = new TeleportTrigger(420,560,100);
        TeleportTrigger t2 = new TeleportTrigger(520,455,100);
        TeleportTrigger t3 = new TeleportTrigger(660,660,100);
        TeleportTrigger t4 = new TeleportTrigger(350,370,100);
        t4.setDestination(t3);
        t1.setDestination(t2);
//        triggerSystem.register(t1);
//        triggerSystem.register(t2);
//        triggerSystem.register(t3);
//        triggerSystem.register(t4);
        triggers.add(t1);
        triggers.add(t2);
        triggers.add(t3);
        triggers.add(t4);
    }

//    public static void main(String[] args) {
//        new JFXPanel();
//        new TriggerLoader("/test.json");
//    }
}

package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader {
    protected int mapWidth;
    protected int mapHeight;

    protected List<GenericObstacle> obstacles;
    protected List<Trigger> triggers;

    protected TriggerSystem triggerSystem;

    /**
     * Constructs map loader with compulsory attributes
     */
    public MapLoader() {
        mapWidth = 0;
        mapHeight = 0;
        obstacles = new ArrayList<>();
        triggers = new ArrayList<>();
        triggerSystem = new TriggerSystem();
    }

    /**
     * Gets the width of the map
     * @return integer representing the size of map's width
     */
    public int getMapWidth() { return mapWidth; }

    /**
     * Gets the height of the map
     * @return integer representing the size of map's height
     */
    public int getMapHeight() { return mapHeight; }

    /**
     * Gets all obstacles
     * @return List of Generic Obstacles
     */
    public List<GenericObstacle> getObstacles() { return obstacles; }



    /**
     * Gets all triggers
     * @return List of Triggers
     */
    public List<Trigger> getTriggers() { return triggers; }

    /**
     * Gets trigger layer
     * @return Trigger System
     */
    public TriggerSystem getTriggerSystem() { return triggerSystem; }

    /**
     * registers all the triggers
     */
    public void registerTriggers() { for(Trigger t: triggers) triggerSystem.register(t); }

}

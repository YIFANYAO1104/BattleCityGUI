package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader {

    protected List<GenericObstacle> obstacles;
    protected List<Trigger> triggers;

    protected TriggerSystem triggerSystem;

    /**
     * Constructs map loader with compulsory attributes
     */
    public MapLoader() {
        obstacles = new ArrayList<>();
        triggers = new ArrayList<>();
        triggerSystem = new TriggerSystem();
    }

    /**
     * gets all obstacles
     * @return List of Generic Obstacles
     */
    public List<GenericObstacle> getObstacles() { return obstacles; }

    /**
     * gets all triggers
     * @return List of Triggers
     */
    public List<Trigger> getTriggers() { return triggers; }

    /**
     * gets trigger layer
     * @return Trigger System
     */
    public TriggerSystem getTriggerSystem() { return triggerSystem; }

    /**
     * registers all the triggers
     */
    public void registerTriggers() { for(Trigger t: triggers) triggerSystem.register(t); }

}

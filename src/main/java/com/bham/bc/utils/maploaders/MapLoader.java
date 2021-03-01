package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader {

    protected List<GenericObstacle> obstacles;
    protected TriggerSystem triggerSystem;

    /**
     * Constructs map loader with compulsory attributes
     */
    public MapLoader() {
        obstacles = new ArrayList<>();
        triggerSystem = new TriggerSystem();
    }

    /**
     * gets obstacle layer
     * @return List of Generic Obstacles
     */
    public List<GenericObstacle> getObstacles() { return obstacles; }

    /**
     * gets trigger layer
     * @return Trigger System
     */
    public TriggerSystem getTriggerSystem() { return triggerSystem; }

}

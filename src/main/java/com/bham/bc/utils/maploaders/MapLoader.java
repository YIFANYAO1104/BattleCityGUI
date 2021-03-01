package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader {

    protected List<GenericObstacle> obstacles = new ArrayList<>();
    protected TriggerSystem triggerSystem = new TriggerSystem();



    public List<GenericObstacle> getObstacles() {
        return obstacles;
    }
    public TriggerSystem getTriggerSystem() {
        return triggerSystem;
    }

}

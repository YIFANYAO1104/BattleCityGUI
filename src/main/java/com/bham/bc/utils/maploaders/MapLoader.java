package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.MapObject2D;
import com.bham.bc.components.environment.obstacles.Home;
import com.bham.bc.entity.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader {

    protected List<MapObject2D> obstacles = new ArrayList<MapObject2D>();
    protected Home home;
    protected TriggerSystem triggerSystem = new TriggerSystem();



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

package com.bham.bc.utils.maploaders;

import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.obstacles.Attribute;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.entity.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Gets the width of the tile
     * @return width of any tile
     */
    public abstract int getTileWidth();

    /**
     * Gets the height of the tile
     * @return height of any tile
     */
    public abstract int getTileHeight();

    /**
     * Gets the amount of tiles in X direction
     * @return number of tiles making up the total width of the map
     */
    public abstract int getNumTilesX();

    /**
     * Gets the amount of tiles in Y direction
     * @return number of tiles making up the total height of the map
     */
    public abstract int getNumTilesY();
}

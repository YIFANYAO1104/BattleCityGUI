package com.bham.bc.components.environment.maploaders;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.TriggerSystem;
import java.util.ArrayList;
import java.util.List;

public abstract class MapLoader {

    protected List<Obstacle> obstacles;

    /**
     * Constructs map loader with compulsory attributes
     */
    public MapLoader() {
        obstacles = new ArrayList<>();
    }

    /**
     * Gets all obstacles
     * @return List of Generic Obstacles
     */
    public List<Obstacle> getObstacles() { return obstacles; }

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

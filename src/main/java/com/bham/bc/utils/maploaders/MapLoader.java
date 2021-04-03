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

//    /**
//     * Gets all the obstacles that don't affect any entities in the game
//     * @return a list of type {@link GenericObstacle} containing obstacles that don't interact with other entities
//     */
//    public List<GenericObstacle> getNoninteractiveObstacles() {
//        return obstacles.stream().filter(o -> o.getAttributes().contains(Attribute.PASSABLE)).collect(Collectors.toList());
//    }
//
//    /**
//     * Gets all the obstacles that do affect game entities in some way
//     * @return a list of type {@link GenericObstacle} containing obstacles whose hit-boxes are taken into account when game updates
//     */
//    public List<GenericObstacle> getInteractiveObstacles() {
//        return obstacles.stream().filter(o -> !o.getAttributes().contains(Attribute.PASSABLE)).collect(Collectors.toList());
//    }

    /**
     * Gets all triggers
     * @return List of Triggers
     */
    public List<Trigger> getTriggers() { return triggers; }

//    /**
//     * Gets trigger layer
//     * @return Trigger System
//     */
//    public TriggerSystem getTriggerSystem() { return triggerSystem; }
//
//    /**
//     * Registers all the triggers
//     */
//    public void registerTriggers() { for(Trigger t: triggers) triggerSystem.register(t); }

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

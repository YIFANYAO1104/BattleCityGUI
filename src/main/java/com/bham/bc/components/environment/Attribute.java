package com.bham.bc.components.environment;

/**
 * Represents the attributes an obstacle can have
 *
 * <p>This is required whenever we want to identify certain obstacles from a group of Obstacles.</p>
 */
public enum Attribute {
    WALL,
    WALKABLE,
    BREAKABLE,
    UPDATABLE,
    RENDER_TOP,
    HOME_AREA,
    HOME_CENTER,
    ENEMY_SPAWN_AREA,
    ENEMY_SPAWN_CENTER
}

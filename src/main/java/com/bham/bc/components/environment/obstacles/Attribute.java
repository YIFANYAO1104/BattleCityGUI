package com.bham.bc.components.environment.obstacles;

/**
 * Represents the attributes an obstacle can have. This is required whenever we want
 * to identify certain obstacles from a group of Generic Obstacles.
 */
public enum Attribute {
    PASSABLE,
    BREAKABLE,
    UPDATABLE,
    RENDER_TOP,
    HOME_AREA,
    HOME_CENTER,
    ENEMY_SPAWN_AREA,
    ENEMY_SPAWN_CENTER
}

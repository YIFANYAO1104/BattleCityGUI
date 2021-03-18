package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.DefaultEnemy;

/**
 * Interface defining the required backend services for a mode to properly work
 */
public interface BackendServices {
    /**
     * Adds enemy to the game
     * @param enemy Enemy object to be added
     */
    void addEnemy(DefaultEnemy enemy);

    /**
     * Adds a bullet to the bullet list
     * @param bullet bullet to be added to the bullet list
     */
    void addBullet(Bullet bullet);

    /**
     * Updates all the entities: map, characters and armory elements
     */
    void update();

    /**
     * Clears all objects in the game
     */
    void clear();
}

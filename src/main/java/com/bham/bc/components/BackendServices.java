package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.graph.SparseGraph;

/**
 * Interface defining the required backend services for a mode to properly work
 */
public interface BackendServices {
    /**
     * Adds enemy to the game
     * @param enemy Enemy object to be added
     */
    void addEnemy(Enemy enemy);

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

    void addBombTank(BombTank b);

    SparseGraph getNavigationGraph();

     void testAStar();

     void testDjistra();

    void addExplosiveTrigger(int x, int y);
}

package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.physics.BombTank;
import javafx.scene.shape.Shape;

/**
 * Desc: required backend services for any mode to properly work
 */
public interface BackendServices {

    /**
     * adds enemy to the game
     * @param enemy Enemy object to be added
     */
    void addEnemy(Enemy enemy);

    /**
     * removes enemy from the game
     * @param enemy Enemy object to be removed
     */
    void removeEnemy(Enemy enemy);

    /**
     * Adding bombtank to bomb tank list
     * @param bombTank
     */
    void addBombTank(BombTank bombTank);

    /**
     * Remove bomb tank from bomb tank list
     * @param bombTank
     */
    void removeBombTank(BombTank bombTank);

    Shape getPlayerHitBox();

    /**
     * Adds a bullet to the bullet list
     * @param bullet bullet to be added to the bullet list
     */
    void addBullet(Bullet bullet);

    /**
     * Unregisters the bullet from the game
     * @param bullet bullet to be removed from the bullet list
     */
    void removeBullet(Bullet bullet);
}

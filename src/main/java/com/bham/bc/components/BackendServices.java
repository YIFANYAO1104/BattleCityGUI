package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.physics.BombTank;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public interface BackendServices {
    /** The calling from each other member must implements the centerController Interface*/
    /**
     * Turn Back
     * @param t
     */
    void changToOldDir(MovingEntity t);

    void removeEnemy(Enemy enemy);

    /**
     * Adding bombtank to bomb tank list
     * @param b
     */
    void addBombTank(BombTank b);

    /**
     * Remove bomb tank from bomb tank list
     * @param b
     */
    void removeBombTank(BombTank b);

    int getHomeTankX();

    int getHomeTankY();

    Rectangle getHomeHitBox();

    /**
     * Adding bullets to bullets list
     * @param m
     */
    void addBullet(Bullet m);

    /**
     * Removing bullets to bullets list
     * @param m
     */
    void removeBullet(Bullet m);
}

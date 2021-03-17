package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.graph.SparseGraph;
import javafx.scene.shape.Rectangle;

/**
 * Desc: required backend services for any mode to properly work
 */
public interface BackendServices {
    /**
     * changes direction of any entity to previous one
     * @param movingEntity MovingEntity object to be changed
     */
    void changeToOldDir(MovingEntity movingEntity);

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

    /**
     * removes any obstacle from the map
     * @param obstacle GenericObstacle object to be removed
     */
    void removeObstacle(GenericObstacle obstacle);

    int getPlayerX();

    int getPlayerY();

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

    SparseGraph getGraph();
}

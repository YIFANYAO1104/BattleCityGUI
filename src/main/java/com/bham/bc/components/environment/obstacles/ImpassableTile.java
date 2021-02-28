package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.TILESET;

import static com.bham.bc.components.CenterController.centerController;

/**
 * Desc: Tile that cannot be passed by a character. Only bullets can pass it
 */
public class ImpassableTile extends GenericObstacle {

    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public ImpassableTile(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    public void handleBullet(Bullet b) {
        return;
    }

    @Override
    public void handleTank(Tank t) {
        if(t.isLive() && this.isIntersect(t)){
            centerController.changToOldDir(t);
        }
    }
}

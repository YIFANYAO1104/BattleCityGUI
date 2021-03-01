package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;

import static com.bham.bc.components.CenterController.centerController;

/**
 * Desc: Tile that is unbreakable, nothing can pass through it
 */
public class HardTile extends GenericObstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public HardTile(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
        renderTop = false;
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/Map/metalWall.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) {
        if (b.isLive() && this.isIntersect(b)) {
            b.setLive(false);
            centerController.removeBullet(b);
        }
    }

    @Override
    public void handleCharacter(Tank t) {
        if(t.isLive() && this.isIntersect(t)){
            centerController.changToOldDir(t);
        }
    }
}

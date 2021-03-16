package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Desc: Tile that is unbreakable, nothing can pass through it
 */
public class Hard extends GenericObstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public Hard(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
        renderTop = false;
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/metalWall.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) {
        if (b.exists() && this.intersects(b)) {
            //b.setAlive(false);
            backendServices.removeBullet(b);
        }
    }

    @Override
    public void handleCharacter(Character t) {
        if(t.exists() && this.intersects(t)){
            backendServices.changeToOldDir(t);
        }
    }
}

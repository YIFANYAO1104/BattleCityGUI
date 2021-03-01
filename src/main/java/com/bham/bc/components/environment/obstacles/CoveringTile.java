package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.TILESET;
import javafx.scene.image.Image;

/**
 * Desc: Tile which covers anything that passes through it
 */
public class CoveringTile extends GenericObstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public CoveringTile(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/Map/tree.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) {
        return;
    }

    @Override
    public void handleCharacter(Tank t) {
        return;
    }
}

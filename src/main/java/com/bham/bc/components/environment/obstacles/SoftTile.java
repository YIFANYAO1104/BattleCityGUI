package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.TILESET;
import javafx.scene.image.Image;

import static com.bham.bc.components.CenterController.centerController;

/**
 * Desc: Tile that is breakable, nothing can pass through it until it's destroyed
 */
public class SoftTile extends GenericObstacle {

    // Use Switch statement and check tileID manually to determine how much hp each type of softTile has
    // Alternatively, just set every tile equal to the same hp
    private int hp = 3;

    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public SoftTile(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/Map/CommonWall.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) {
        if (b.isLive() && this.isIntersect(b)) {
            b.setLive(false);
            centerController.removeBullet(b);
            // hp -= b.damage();
            if(--hp <= 0) centerController.removeBullet(b);
            // centerController.removeObstacle(this);
        }
    }

    @Override
    public void handleCharacter(Tank t) {
        if(t.isLive() && this.isIntersect(t)){
            centerController.changToOldDir(t);
        }
    }
}

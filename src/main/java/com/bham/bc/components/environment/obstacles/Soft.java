package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;

import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Desc: Tile that is breakable, nothing can pass through it until it is destroyed
 */
public class Soft extends GenericObstacle {

    // Use Switch statement and check tileID manually to determine how much hp each type of softTile has
    // Alternatively, just set every tile equal to the same hp
    private int hp = 50;

    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public Soft(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
        renderTop = false;
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/softWall.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) {
        if(intersects(b)) {
            hp -= b.getDamage();
            b.destroy();

            if(hp <= 0) {
                exists = false;
                entityManager.removeEntity(this);
            }
        }
    }

    @Override
    public void handleCharacter(Character c) {
        if(intersectsShape(c.getImageHitbox())) c.move(-1, true);
    }
}

package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;

/**
 * Desc: Tile which covers anything that passes through it
 */
public class Covering extends GenericObstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public Covering(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    public EnumSet<ATTRIBUTE> getAttributes() { return EnumSet.of(ATTRIBUTE.RENDER_TOP); }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/tree.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) { }

    @Override
    public void handleCharacter(GameCharacter t) { }

    @Override
    public void interactWith(int ID, int indexOfNode , Rectangle r1) { }
}

package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;


/**
 * Desc: Tile which can be passed by any game entity. It can act as a decoration
 */
public class Passable extends GenericObstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public Passable(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }
    @Override
    public EnumSet<Attribute> getAttributes() { return EnumSet.of(Attribute.PASSABLE); }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/icewall.jpg") };
    }

    @Override
    public void handleBullet(Bullet b) { return; }

    @Override
    public void handleCharacter(GameCharacter t) { return; }

    @Override
    public void interactWith(int ID, int indexOfNode , Rectangle r1) {
        return;
    }
}

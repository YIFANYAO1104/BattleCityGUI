package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.maploaders.Tileset;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;


/**
 * Tile which can be passed by any game entity. It can act as a decoration
 */
public class Passable extends Obstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public Passable(int x, int y, Tileset tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    public EnumSet<Attribute> getAttributes() {
        return EnumSet.of(Attribute.WALKABLE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/DefaultTiles/icewall.jpg") };
    }

    @Override
    public void interactWith(int ID, int indexOfNode , Rectangle r1) {
        return;
    }
}

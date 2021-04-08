package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.maploaders.Tileset;
import javafx.scene.image.Image;

import java.util.EnumSet;

/**
 * Tile which represents the center of the home territory surrounded by tiles of type {@link HomeArea}
 */
public class HomeCenter extends Obstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public HomeCenter(int x, int y, Tileset tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    public EnumSet<Attribute> getAttributes() {
        return EnumSet.of(Attribute.HOME_CENTER, Attribute.HOME_AREA, Attribute.WALKABLE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/DefaultTiles/icewall.jpg") };
    }
}

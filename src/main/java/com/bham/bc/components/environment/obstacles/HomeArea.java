package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;

import java.util.EnumSet;

/**
 * Tile which corresponds to the home territory that is needed to be defended
 */
public class HomeArea extends GenericObstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public HomeArea(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    public EnumSet<Attribute> getAttributes() {
        return EnumSet.of(Attribute.HOME_AREA, Attribute.PASSABLE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/icewall.jpg") };
    }

    @Override
    public void handleBullet(Bullet b) { }

    @Override
    public void handleCharacter(GameCharacter c) { }
}

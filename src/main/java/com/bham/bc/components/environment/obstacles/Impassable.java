package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.maploaders.Tileset;
import javafx.scene.image.Image;

/**
 * Tile that cannot be passed by a character. Only bullets can pass it
 */
public class Impassable extends Obstacle {

    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public Impassable(int x, int y, Tileset tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/river_01.jpg") };
    }

    @Override
    public void handleBullet(Bullet b) { }

    @Override
    public void handleCharacter(GameCharacter c) {
        if(intersects(c)) c.move(-1);
    }
}

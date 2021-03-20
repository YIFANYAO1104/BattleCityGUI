package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.utils.messaging.MessageDispatcher.Dispatch;
import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageTypes.*;

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
        renderTop = false;
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/icewall.jpg") };
    }

    @Override
    public void handleBullet(Bullet b) { return; }

    @Override
    public void handleCharacter(Character t) { return; }

    @Override
    public void interactWith(int ID, int indexOfNode , Rectangle r1) {
        return;
    }
}

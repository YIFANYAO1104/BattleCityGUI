package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.messaging.MessageDispatcher.Dispatch;
import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interactWithPassable;

/**
 * Desc: Tile that is breakable, nothing can pass through it until it is destroyed
 */
public class Soft extends GenericObstacle {

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
    public Soft(int x, int y, TILESET tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
        renderTop = false;
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/tiles/softWall.bmp") };
    }

    @Override
    public void handleBullet(Bullet b) {
        if (b.isAlive() && this.intersects(b)) {
            b.setAlive(false);
            backendServices.removeBullet(b);
//             hp -= b.damage();
            if(--hp <= 0) backendServices.removeObstacle(this);
        }
    }

    @Override
    public void handleCharacter(Character t) {
        if(t.isAlive() && this.intersects(t)){
            backendServices.changeToOldDir(t);
        }
    }

    @Override
    public void interactWith(int ID, int indexOfNode , Rectangle r1) {
        if(this.getHitBox().intersects(r1.getBoundsInLocal()))
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,this.ID(),ID,Msg_interactWithPassable,indexOfNode);
    }
}

package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.utils.maploaders.TILESET;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import static com.bham.bc.utils.messaging.MessageTypes.*;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interactWithPassable;
import java.util.EnumSet;

import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Desc: Tile that is breakable, nothing can pass through it until it is destroyed
 */
public class Soft extends GenericObstacle {

    // Use Switch statement and check tileID manually to determine how much hp each type of softTile has
    // Alternatively, just set every tile equal to the same hp
    private double hp = 50;

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
    }

    @Override
    public EnumSet<ATTRIBUTE> getAttributes() { return EnumSet.of(ATTRIBUTE.BREAKABLE); }

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
                //------------for testing---------------------------------------
                backendServices.testAStar();
                Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,getID(),
                        backendServices.getGraph().getID(),
                        Msg_removeSoft,NO_ADDITIONAL_INFO);
                //------------for testing---------------------------------------
                exists = false;
                entityManager.removeEntity(this);
            }
        }
    }

    @Override
    public void handleCharacter(GameCharacter c) {
        if(intersects(c)) c.move(-1, true);
    }

    @Override
    public void interactWith(int ID, int indexOfNode , Rectangle r1) {
        if(this.getHitBox().intersects(r1.getBoundsInLocal()))
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,this.getID(),ID,Msg_interactWithPassable,indexOfNode);
    }


    @Override
    // TODO
    public void decreaseHP(double hurt) {
        hp = Math.max(hp - hurt, 0.0);
        if(hp <= 0.0) {
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,getID(),
                    backendServices.getGraph().getID(),
                    Msg_removeSoft,NO_ADDITIONAL_INFO);
            exists = false;
            entityManager.removeEntity(this);
        }
    }

}

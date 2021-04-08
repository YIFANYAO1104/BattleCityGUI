package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.Tileset;
import com.bham.bc.entity.ai.navigation.ItemType;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import static com.bham.bc.utils.messaging.MessageTypes.*;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interactWithSoft;
import java.util.EnumSet;

import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Tile that is breakable, nothing can pass through it until it is destroyed
 */
public class Soft extends Obstacle {

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
    public Soft(int x, int y, Tileset tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    public EnumSet<Attribute> getAttributes() {
        return EnumSet.of(Attribute.BREAKABLE, Attribute.WALL);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/DefaultTiles/softWall.bmp") };
    }

    @Override
    public void interactWith(int graphSystemID, int indexOfNode , Rectangle r1) {
        //set this node as high cost node
        if(getHitBox().intersects(r1.getBoundsInLocal())){
            Object[] params = {indexOfNode,this};
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), graphSystemID, Msg_interactWithSoft, params);
        }
    }


    @Override
    public void changeHp(double health) {
        hp += health;
        if(hp <= 0.0) {
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), backendServices.getGraph().getID(), Msg_removeSoft, NO_ADDITIONAL_INFO);
            exists = false;
            entityManager.removeEntity(this);
        }
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SOFT;
    }
}

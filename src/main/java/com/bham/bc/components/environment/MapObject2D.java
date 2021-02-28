package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.utils.messaging.MessageDispatcher.Dispatch;
import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interact;

public abstract class MapObject2D extends BaseGameEntity {
    /**
     * Constructor Of Object,genreating a BaseGameEntity ID
     * @param x
     * @param y
     */
    private boolean toBeRemoved;

    private int width ;
    private int length;

    protected void setToBeRemovedFromMap() {
        toBeRemoved = true;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    public MapObject2D(int x, int y) {
        super(GetNextValidID(),x,y);
        toBeRemoved = false;

    }

    @Override
    public String toString() {
        return "GameMap element";
    }


    /**
     * A method to indicate if the bullet hits the obstacles
     * If bullet hits the wall then we should remove the wall from centerController
     * @param m
     * @return
     */
    abstract public void beHitBy(Bullet m);


    abstract public void collideWith(Tank t);


    public void interactWith(int ID,int indexOfNode ,Rectangle r1) {
        if(this.getHitBox().intersects(r1.getBoundsInLocal()))
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,this.ID(),ID,Msg_interact,indexOfNode);
    }
}

package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.shape.Rectangle;

public abstract class MapObject2D extends BaseGameEntity {
    /**
     * Constructor Of Object,genreating a BaseGameEntity ID
     * @param x
     * @param y
     */
    private boolean toBeRemoved;

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

    abstract public void interactWith(int ID,int indexOfNode, Rectangle r1);
}

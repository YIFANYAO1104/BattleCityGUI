package com.bham.bc.common;

import java.awt.*;

public abstract class MovingEntity extends BaseGameEntity{

    /**
     * the velocity of moving object( speed x in horizontal and speed y in vertical)
     */
    protected int speedX;
    protected int speedY;

    /**
     * the record of the previous position of the moving object
     * It is useful when it moves and intersects sth.
     * In this situation, it will move back.
     */
    protected int oldX, oldY;

    /**
     * the length and width of the moving object
     * It is somewhat wasting spaces, but this could be easier to develop. So we need them
     */
    protected int width;
    protected int length;

    /**
     * the moving direction of the moving object
     */

    protected Direction direction;
    /**
     * the status to check if the object is alive or dead
     */
    protected boolean live;

    /**
     * the constructor of this class, will generate a valid ID using parent class's generating ID method
     */
    /**
     * the constructor of this class, will generate a valid ID using parent class's generating ID method
     * The other attributes of an moving entity will be set
     */
    protected MovingEntity(int speedX,int speedY,
                           int x, int y,
                           int width, int length,
                           Direction direction) {
        super(GetNextValidID(),x,y);
        this.speedX =speedX;
        this.speedY = speedY;

        this.oldX = x;
        this.oldY = y;

        this.width =width;
        this.length = length;

        this.direction = direction;
        live = true;
    }

    protected abstract void move();

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, length);
    }

    public void changToOldDir() {
        x = oldX;
        y = oldY;
    }
}

package com.bham.bc.tank;

import com.bham.bc.common.Direction;
import com.bham.bc.common.MovingEntity;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

abstract public class Tank extends MovingEntity {

    /**
     * the constructor of this class, will generate a valid ID using parent class's generating ID method
     * The other attributes of an moving entity will be set
     *
     * @param speedX
     * @param speedY
     * @param x
     * @param y
     * @param width
     * @param length
     * @param direction
     */
    protected Tank(int speedX, int speedY, int x, int y, int width, int length, Direction direction) {
        super(speedX, speedY, x, y, width, length, direction);
    }

    //Will be used by Triggers------------------------------------------------------------------------
    public Point2D getPosition() {
        return new Point2D(x,y);
    }

    public Point2D getRadius() {
        return new Point2D(width,length);
    }

    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, length);
    }

    abstract public void increaseHealth(int health);
}

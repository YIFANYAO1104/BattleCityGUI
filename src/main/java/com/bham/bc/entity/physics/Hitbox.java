package com.bham.bc.entity.physics;

import javafx.geometry.Point2D;


/**
 * The hitbox to check the interaction
 */
public class Hitbox {
    private Point2D TopLeft;
    private Point2D BottomRight;
    private Point2D Centure;

    public Hitbox(Point2D topLeft1, Point2D bottomRight1) {
        this.TopLeft = topLeft1;
        this.BottomRight = bottomRight1;
        Point2D t1 = topLeft1.add(BottomRight);
        this.Centure = new Point2D(t1.getX() / 2, t1.getY() / 2);
    }

    //Return true if this one is hitted with another one.
    private static Boolean isInteractedWith(Hitbox one, Hitbox another) {
        if(
                (one.getTop()< another.getBottom() && one.getRight() < another.getRight())
                && (one.getBottom() > another.getTop() && one.getRight()>another.getLeft() )
        )
            return true;
        else
            return false;

    }

    /**
     * Detect if this and another hitbox are interacting
     * @param another Hitbox
     * @return Boolean
     */
    public Boolean isInteractedWith(Hitbox another){
        if(isInteractedWith(this,another) || isInteractedWith(another,this))
            return true;
        return false;
    }
    public double getLeft(){
        return this.TopLeft.getX();
    }
    public double getTop(){
        return this.TopLeft.getY();
    }

    public double getRight(){
        return this.BottomRight.getX();
    }
    public double getBottom(){
        return this.BottomRight.getY();
    }


    public Point2D getBottomRight() {
        return BottomRight;
    }

    public Point2D getCenture() {
        return Centure;
    }

    public Point2D getTopLeft() {
        return TopLeft;
    }
}

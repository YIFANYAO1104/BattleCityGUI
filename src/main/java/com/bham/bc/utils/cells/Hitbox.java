package com.bham.bc.utils.cells;

import javafx.geometry.Point2D;



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
    public Boolean isInteractedWith(Hitbox another) {
        return !(
                (another.getTop() > this.getBottom())
                ||(another.getBottom() < this.getTop())
                ||(another.getLeft() > this.getRight())
                ||(another.getRight() < this.getLeft())
                );

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

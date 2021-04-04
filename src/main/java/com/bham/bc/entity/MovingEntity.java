package com.bham.bc.entity;

import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/**
 * Represents any entity that can move in any angle
 */
public abstract class MovingEntity extends BaseGameEntity {

    //the magnitude of velocity should be less than this value
    protected double maxSpeed;
    //could be zero
    protected Point2D velocity;
    //non-zero, normalized vector for direction, must be updated once the velocity was updated
    protected Point2D heading;
    protected boolean exists;

    /**
     * Constructs a single moving entity by default facing up (angle is set to 0) and generates a new valid ID for it
     *
     * @param x top left x coordinate of the entity and its image
     * @param y top left y coordinate of the entity and its image
     * @param maxSpeed value which defines the initial velocity
     */
    //for game character
    protected MovingEntity(double x, double y, double maxSpeed) {
        super(GetNextValidID(), x, y);
        this.maxSpeed = maxSpeed;
        this.velocity = new Point2D(0,0);
        this.heading = new Point2D(0,-1);
        exists = true;
    }

    //for bullet
    protected MovingEntity(double x, double y, double maxSpeed, Point2D heading) {
        super(GetNextValidID(), x, y);
        this.maxSpeed = maxSpeed;
        this.velocity = heading.multiply(maxSpeed);
        this.heading = heading;
        exists = true;
    }

    /**
     * Draws an image on a graphics context
     *
     * <p>The image is drawn at (x, y) rotated by angle pivoted around the point (centerX, centerY).
     * It uses Rotate class form JavaFX which applies rotation using transform matrix.</p>
     *
     * @param gc graphics context the image is to be drawn on
     * @param angle rotation angle
     *
     * @see <a href="https://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas">stackoverflow.com</a>
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Rotate.html">docs.oracle.com</a>
     */
    protected void drawRotatedImage(GraphicsContext gc, Image image, double angle) {
        gc.save();
        Rotate r = new Rotate(angle, x + image.getWidth() / 2, y + image.getHeight() / 2);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, x, y);
        gc.restore();
    }

    /**
     * Gets the current angle
     * @return angle between -180 and 180 degrees the entity s facing
     */
    public double getAngle() {
        return GeometryEnhanced.antiClockWiseAngle(new Point2D(0,-1),heading);
//        return Math.toDegrees(Math.atan2(heading.getY(), heading.getX())) + 90;
    }


    /**
     * Checks if this entity exists
     * @return true if it exists and false otherwise
     */
    public boolean exists() { return exists; }

    /**
     * Defines how the position of the entity is updated on each frame
     */
    public abstract void move();

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public double getMaxForce() {
        return 10;
    }

    public Point2D getHeading() {
        return heading;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

}

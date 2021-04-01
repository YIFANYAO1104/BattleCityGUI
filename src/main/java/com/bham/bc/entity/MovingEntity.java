package com.bham.bc.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/**
 * Represents any entity that can move in any angle
 */
public abstract class MovingEntity extends BaseGameEntity {
    protected double speed;
    protected double angle;
    protected boolean exists;

    /**
     * Constructs a single moving entity by default facing up (angle is set to 0) and generates a new valid ID for it
     *
     * @param x top left x coordinate of the entity and its image
     * @param y top left y coordinate of the entity and its image
     * @param speed value which defines the initial velocity
     */
    protected MovingEntity(double x, double y, double speed) {
        super(GetNextValidID(), x, y);
        this.speed = speed;
        angle = 0;
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
        return angle;
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
}

package com.bham.bc.entity;

import com.bham.bc.entity.physics.Steering;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 * Represents any entity that can move at any angle
 */
public abstract class MovingEntity extends BaseGameEntity {
    public static final double STANDARD_FORCE = 100;
    /**
     * entity's mass
     */
    protected double mass;
    /**
     *  maximum speed value for the entity
     */
    protected double maxSpeed;
    /**
     * max force the entity can stand
     */
    protected double maxForce;
    /**
     * basis vector indicating the direction the entity is facing
     */
    protected Point2D heading;
    /**
     * Point2D object representing a new velocity vector
     */
    protected Point2D velocity;
    protected Point2D acceleration;
    /**
     * Steering Object of entity
     */
    protected Steering steering;

    /**
     * Constructs a single moving entity by default facing up (angle is set to 0), giving it a
     * mass of 1 and generates a new valid ID for it
     *
     * @param x        top left x coordinate of the entity and its image
     * @param y        top left y coordinate of the entity and its image
     * @param maxSpeed value which defines the initial velocity
     */
    protected MovingEntity(double x, double y, double maxSpeed) {
        super(getNextValidID(), x, y);
        this.maxSpeed = maxSpeed;
        mass = 1;
        maxForce = STANDARD_FORCE;
        heading = new Point2D(0,-1);
        velocity = new Point2D(0,0);
        acceleration = new Point2D(0, 0);
        steering = new Steering(this);
    }

    /**
     * Alternate constructor allowing to initialize a custom facing direction with a non-zero velocity
     *
     * @param x        top left x coordinate of the entity and its image
     * @param y        top left y coordinate of the entity and its image
     * @param maxSpeed value which defines the initial velocity
     * @param heading  basis vector indicating the direction the entity is facing
     */
    protected MovingEntity(double x, double y, double maxSpeed, Point2D heading) {
        super(getNextValidID(), x, y);
        this.maxSpeed = maxSpeed;
        this.heading = heading;
        mass = 1;
        maxForce = STANDARD_FORCE;
        velocity = heading.multiply(maxSpeed);
        acceleration = new Point2D(0, 0);
        steering = new Steering(this);
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
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Rotate.html">docs.oracle.com</a>
     */
    protected void drawRotatedImage(GraphicsContext gc, Image image, double angle) {
        gc.save();
        Rotate r = new Rotate(angle, x + image.getWidth() / 2, y + image.getHeight() / 2);
        gc.transform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, x, y);
        gc.restore();
    }

    /**
     * Returns the Steering Object of Entity
     * @return Steering Object of Entity
     */
    public Steering getSteering() {
        return steering;
    }

    /**
     * Calculates the angle the entity is facing
     *
     * <p><b>Note:</b> the basis vector which is used for angle calculation must be (0, -1) as this is the
     * way the entity in the image is facing (upwards).</p>
     *
     * @return angle between 0 and 360 degrees the entity is facing
     */
    public double getAngle() {
        return GeometryEnhanced.antiClockWiseAngle(new Point2D(0,-1), heading);
    }

    /**
     * Returns the mass this entity was assigned
     * @return entity's mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * Returns the maximum speed value this entity can reach
     * @return maximum speed value of this entity
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Returns the maximum force this entity can have (used for acceleration)
     * @return maximum force value the entity can have
     */
    public double getMaxForce() {
        return maxForce;
    }

    /**
     * Returns the normalized vector the entity is facing at
     * @return Point2D object representing a direction vector the entity is facing
     */
    public Point2D getHeading() {
        return heading;
    }

    /**
     * Returns the current velocity
     * @return Point2D object representing a velocity vector the entity has
     */
    public Point2D getVelocity() {
        return velocity;
    }

    /**
     * Sets the maximum speed value for the entity
     * @param maxSpeed max speed the entity can reach
     */
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Sets the maximum force value for the entity
     * @param maxForce max force the entity can stand
     */
    public void setMaxForce(double maxForce) {
        this.maxForce = maxForce;
    }

    /**
     * Sets the current velocity of the entity
     * @param velocity Point2D object representing a new velocity vector
     */
    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    /**
     * Defines how the position of the entity is updated on each frame
     */
    public abstract void move();

    // TODO: remove
    public void renderHitBox(GraphicsContext gc) {
        if(getHitBox() instanceof Rectangle) {
            Rectangle hb = (Rectangle) getHitBox();
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            gc.save();
            Rotate r = new Rotate(getAngle(), x + hb.getWidth() / 2, y + hb.getHeight() / 2);
            gc.transform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.strokeRect(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
            gc.restore();
        } else if(getHitBox() instanceof Circle) {
            Circle hb = (Circle) getHitBox();
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            gc.strokeArc(hb.getCenterX() - hb.getRadius(), hb.getCenterY() - hb.getRadius(), hb.getRadius()*2, hb.getRadius()*2, 0, 360, ArcType.OPEN);
        }
    }
}

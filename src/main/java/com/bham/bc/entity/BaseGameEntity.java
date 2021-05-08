package com.bham.bc.entity;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;

import static com.bham.bc.entity.EntityManager.entityManager;

abstract public class BaseGameEntity {
    /**
     * prepare the index{@link Integer} of next Entity
     */
    private static int nextValidID = 0;

    /**
     * The index {@link Integer} of this Entity
     */
    private int id;

    /**
     * The location in map
     */
    protected double x, y;

    /**
     * The flag of if it is living in game
     */
    protected boolean exists;
    protected Image[] entityImages;


    /**
     * this must be called within each constructor to make sure the ID is set
     * correctly. It verifies that the value passed to the method is greater or
     * equal to the next valid ID, before setting the ID and incrementing the
     * next valid ID
     */
    private void setID(int id) {
        //make sure the val is equal to or greater than the next available ID
        assert (id >= nextValidID) : "<BaseGameEntity::SetID>: invalid ID";

        this.id = id;
        nextValidID = this.id + 1;
    }

    @Override
    protected void finalize() throws Throwable{super.finalize();}


    protected BaseGameEntity(int id, double x, double y) {
        setID(id);
        entityManager.registerEntity(this);
        this.x = x;
        this.y = y;
        exists = true;
    }

    /**
     * @return {@link Integer} Give the index to entity
     */
    public static int getNextValidID() {
        return nextValidID;
    }

    /**
     * Gets the ID of this entity
     *
     * @return ID as an integer this entity was registered with
     */
    public int getID() {
        return id;
    }

    public Point2D getPosition() {
        return new Point2D(x, y);
    }

    public Point2D getCenterPosition() {
        return new Point2D(x + getSize().getX()/2, y + getSize().getY()/2);
    }

    public Point2D getSize() {
        return new Point2D(entityImages[0].getWidth(), entityImages[0].getHeight());
    }

    /**
     * Checks whether the current entity intersects with the given one
     *
     * @param entity BaseGameEntity instance we want to check if the this instance is intersecting with
     * @return true if the hit-boxes of two entities intersect and false otherwise
     */
    public boolean intersects(BaseGameEntity entity) {
        return intersects(entity.getHitBox());
    }

    public boolean intersects(Shape shape) {
        return ((Path)Shape.intersect(this.getHitBox(), shape)).getElements().size() > 0;
    }

    /**
     * Checks if this entity exists
     * @return true if it exists and false otherwise
     */
    public boolean exists() {
        return exists;
    }

    /**
     * Gets the hit-box of the entity
     *
     * <p>Hit-box is a shape with size and location properties. In most cases it is either a circle or a rectangle.</p>
     *
     * @return the shape of the hit-box
     */
    abstract public Shape getHitBox();

    /**
     * Gets the outer radius of the hit-box
     *
     * <p>If it's a circle it gets its radius, if its any other shape, it gets the radius of the outer
     * circle that would be generated around the hit-box.</p>
     *
     * @return size of hit-box radius
     */
    abstract public double getHitBoxRadius();

    /**
     *
     */
    abstract public void update();

    /**
     *
     * @param gc
     */
    abstract public void render(GraphicsContext gc);

    /**
     *
     * @param msg
     * @return
     */
    abstract public boolean handleMessage(Telegram msg);

    /**
     *
     * @return {@link String}
     */
    abstract public String toString();

    // TODO: remove
    public void renderHitBox(GraphicsContext gc) {
        if(getHitBox() instanceof Rectangle) {
            Rectangle hb = (Rectangle) getHitBox();
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            gc.strokeRect(hb.getX(), hb.getY(), hb.getWidth(), hb.getHeight());
        } else if(getHitBox() instanceof Circle) {
            Circle hb = (Circle) getHitBox();
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            gc.strokeArc(hb.getCenterX() - hb.getRadius(), hb.getCenterY() - hb.getRadius(), hb.getRadius()*2, hb.getRadius()*2, 0, 360, ArcType.OPEN);
        }
    }
}

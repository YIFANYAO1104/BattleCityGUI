package com.bham.bc.entity;

import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static com.bham.bc.entity.EntityManager.entityManager;

abstract public class BaseGameEntity {
    private static int nextValidID = 0;
    private int id;

    protected double x, y;
    protected Image[] entityImages;


    /**
     * this must be called within each constructor to make sure the ID is set
     * correctly. It verifies that the value passed to the method is greater or
     * equal to the next valid ID, before setting the ID and incrementing the
     * next valid ID
     */
    private void SetID(int id) {
        //make sure the val is equal to or greater than the next available ID
        assert (id >= nextValidID) : "<BaseGameEntity::SetID>: invalid ID";

        this.id = id;
        nextValidID = this.id + 1;
    }

    @Override
    protected void finalize() throws Throwable{super.finalize();}


    protected BaseGameEntity(int ID, double x, double y) {
        SetID(ID);
        entityManager.registerEntity(this);
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return
     */
    public static int GetNextValidID() { return nextValidID; }

    /**
     * Gets the ID of this entity
     *
     * @return ID as an integer this entity was registered with
     */
    public int getID() { return id; }

    public Point2D getPosition() { return new Point2D(x, y); }

    public Point2D getCenterPosition() {
        double width = getRadius().getX();
        double height = getRadius().getY();

        return new Point2D(x + width/2, y + height/2);
    }

    public Point2D getRadius() { return new Point2D(entityImages[0].getWidth(), entityImages[0].getHeight()); }

    /**
     * Checks whether the current entity intersects with the given one
     *
     * @param entity BaseGameEntity instance we want to check if the this instance is intersecting with
     * @return true if the hit-boxes of two entities intersect and false otherwise
     */
    public boolean intersects(BaseGameEntity entity) { return intersectsShape(entity.getHitBox()); }

    public boolean intersectsShape(Shape shape) {
        return ((Path)Shape.intersect(this.getHitBox(), shape)).getElements().size() > 0;
    }

    /**
     *
     * @return
     */
    abstract public Shape getHitBox();

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
     * @return
     */
    abstract public String toString();


}

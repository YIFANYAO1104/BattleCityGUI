/**
 *  Desc:   base class for a trigger. A trigger is an object that is
 *          activated when an entity moves within its region of influence.
 */
package com.bham.bc.components.triggers;


import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.ExtraInfo;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

abstract public class Trigger extends BaseGameEntity implements ExtraInfo {

    protected boolean exists;
    protected boolean active;
    protected int currentFrame;


    public Trigger(int x, int y) {
        super(GetNextValidID(), x, y);
        exists = true;
        active = true;

        currentFrame = 0;
        entityImages = getDefaultImage();
    }

    /**
     * returns true if the entity given by a position and bounding radius is
     * overlapping the trigger region
     */
    // TODO: remove, no usage of it
    public boolean intersects(Point2D position, Point2D radius) {
        return (new Rectangle(position.getX(), position.getY(), radius.getX(), radius.getY())).intersects(getHitBox().getBoundsInLocal());
    }

    @Override
    public boolean intersects(Shape shape) {
        return shape.intersects(getHitBox().getBoundsInLocal());
    }

    @Override
    public boolean intersects(BaseGameEntity entity) {
        return intersects(entity.getHitBox());
    }

    abstract protected Image[] getDefaultImage();

    /**
     * when this is called the trigger determines if the entity is within the
     * trigger's region of influence. If it is then the trigger will be
     * triggered and the appropriate action will be taken.
     */
    public abstract void handle(BaseGameEntity entity);

    /**
     * called each update-step of the game. This methods updates any internal
     * state the trigger may have
     */

    public boolean active() {
        return active;
    }

    public boolean exists() {
        return exists;
    }


    protected abstract void renderRegion(GraphicsContext gc);

    @Override
    public abstract void update();

    @Override
    public ItemType getItemType() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return "Trigger";
    }

    // TODO: remove
    //child classes use one of these methods to initialize the trigger region
//    protected void addRectangularTriggerRegion(Point2D pos, Point2D radius) {
//        //triggerRegion = new TriggerRegionRectangle(pos, radius);
//        region = new Rectangle(pos.getX(), pos.getY(), radius.getX(), radius.getY());
//    }

//    protected void addRectangularTriggerRegionSurrounded(Point2D imgPos, Point2D imgRadius, Point2D regionRadius) {
//        Point2D topLeft = imgPos.subtract(regionRadius.subtract(imgRadius).multiply(0.5));
//        triggerRegion = new TriggerRegionRectangle(topLeft, regionRadius);
//    }
}
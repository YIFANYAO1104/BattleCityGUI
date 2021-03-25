/**
 *  Desc:   base class for a trigger. A trigger is an object that is
 *          activated when an entity moves within its region of influence.
 */
package com.bham.bc.entity.triggers;


import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.graph.ExtraInfo;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.shape.Shape;

abstract public class Trigger extends BaseGameEntity implements ExtraInfo {

    /**
     * Every trigger owns a trigger region. If an entity comes within this
     * region the trigger is activated
     */
    private TriggerRegion triggerRegion;
    /**
     * if this is true the trigger will be removed from the game
     */
    private boolean toBeRemoved;
    /**
     * it's convenient to be able to deactivate certain types of triggers on an
     * event. Therefore a trigger can only be triggered when this value is true
     * (respawning triggers make good use of this facility)
     */
    private boolean active;

    protected void setToBeRemovedFromGame() {
        toBeRemoved = true;
    }

    protected void setInactive() {
        active = false;
    }

    protected void setActive() {
        active = true;
    }

    /**
     * returns true if the entity given by a position and bounding radius is
     * overlapping the trigger region
     */
    protected boolean rectIsTouchingTrigger(Point2D EntityPos, Point2D EntityRadius) {
        if (triggerRegion != null) {
            return triggerRegion.isTouching(EntityPos, EntityRadius);
        }
        return false;
    }

    //child classes use one of these methods to initialize the trigger region
    protected void addRectangularTriggerRegion(Point2D pos, Point2D radius) {
        triggerRegion = new TriggerRegionRectangle(pos, radius);
    }

    protected void addRectangularTriggerRegionSurrounded(Point2D imgPos, Point2D imgRadius, Point2D regionRadius) {
        Point2D topLeft = imgPos.subtract(regionRadius.subtract(imgRadius).multiply(0.5));
        triggerRegion = new TriggerRegionRectangle(topLeft, regionRadius);
    }

    public Trigger(int id, int x, int y) {
        super(id, x, y);
        toBeRemoved = false;
        active = true;
        triggerRegion = null;
    }

    /**
     * when this is called the trigger determines if the entity is within the
     * trigger's region of influence. If it is then the trigger will be
     * triggered and the appropriate action will be taken.
     */
    public abstract void tryTriggerC(GameCharacter entity);

    public abstract void tryTriggerO(GenericObstacle entity);

    /**
     * called each update-step of the game. This methods updates any internal
     * state the trigger may have
     */
    @Override
    public abstract void update();

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    public boolean isActive() {
        return active;
    }

    public void renderRegion(GraphicsContext gc) {
        triggerRegion.render(gc);
    }



    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(entityImages[0], this.x, this.y);
        renderRegion(gc);
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
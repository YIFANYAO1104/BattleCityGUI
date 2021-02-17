/*
 *  Desc:   base class for a trigger. A trigger is an object that is
 *          activated when an entity moves within its region of influence.
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.common.Triggers;


import com.bham.bc.common.BaseGameEntity;
import javafx.geometry.Point2D;

abstract public class Trigger<entity_type extends BaseGameEntity> extends BaseGameEntity {

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
    protected boolean isTouchingTrigger(Point2D EntityPos, Point2D EntityRadius) {
        if (triggerRegion != null) {
            return triggerRegion.isTouching(EntityPos, EntityRadius);
        }
        return false;
    }

    //child classes use one of these methods to initialize the trigger region
    protected void addRectangularTriggerRegion(Point2D pos, Point2D radius) {
        triggerRegion = new TriggerRegion_Rectangle(pos, radius);
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
    public abstract void tryTrigger(entity_type entity);

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
}
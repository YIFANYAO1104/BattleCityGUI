package com.bham.bd.components.triggers;


import com.bham.bd.entity.BaseGameEntity;
import com.bham.bd.entity.ai.navigation.ItemType;
import com.bham.bd.entity.graph.ExtraInfo;
import com.bham.bd.utils.messaging.Telegram;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

import static com.bham.bd.entity.EntityManager.entityManager;

import java.util.List;

/**
 * Base class for a trigger. A trigger is an object that is activated when an entity moves within its region of influence.
 */

abstract public class Trigger extends BaseGameEntity implements ExtraInfo {

    protected boolean active;
    protected int currentFrame;

    /**
     * Construct the Trigger in a specific position
     * @param x the x coordinates of respawn trigger
     * @param y the x coordinates of respawn trigger
     */
    public Trigger(int x, int y) {
        super(getNextValidID(), x, y);
        active = true;

        currentFrame = 0;
        entityImages = getDefaultImage();
    }

    /**
     * called each update-step of the game. This methods updates any internal
     * state the trigger may have
     */
    public boolean active() {
        return active;
    }

    /**
     * Handles intersection of multiple {@link BaseGameEntity} objects
     * @param entities a list of entities the trigger's region will be checked on
     * @see #handle(BaseGameEntity)
     */
    public void handle(List<BaseGameEntity> entities) {
        entities.forEach(this::handle);
    }

    /**
     * Handles intersection with {@link BaseGameEntity}
     *
     * <p>Determines if the entity is within the trigger's region of influence. If it is then the trigger
     * will be handle the action to be taken</p>
     *
     * @param entity game entity on which the collision will be checked
     */
    public abstract void handle(BaseGameEntity entity);

    protected abstract Image[] getDefaultImage();
    
    /**
     * Removes the obstacle from the entity list
     */
    public void destroy() {
        exists = false;
        entityManager.removeEntity(this);
    }

    /**
     * Handle the intersection
     * @param shape shape of intersecting entity
     * @return true if two objects intersects, otherwise false.
     */
    @Override
    public boolean intersects(Shape shape) {
        return shape.intersects(getHitBox().getBoundsInLocal());
    }
    /**
     * Handle the intersection
     * @param entity the intersecting entity
     * @return true if two objects intersects, otherwise false.
     */
    @Override
    public boolean intersects(BaseGameEntity entity) {
        return intersects(entity.getHitBox());
    }

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
}
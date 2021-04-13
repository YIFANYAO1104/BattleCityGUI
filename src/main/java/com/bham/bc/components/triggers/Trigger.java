package com.bham.bc.components.triggers;


import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.ExtraInfo;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 *  Desc:   base class for a trigger. A trigger is an object that is
 *          activated when an entity moves within its region of influence.
 */
abstract public class Trigger extends BaseGameEntity implements ExtraInfo {

    protected boolean active;
    protected int currentFrame;

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

    @Override
    public boolean intersects(Shape shape) {
        return shape.intersects(getHitBox().getBoundsInLocal());
    }

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
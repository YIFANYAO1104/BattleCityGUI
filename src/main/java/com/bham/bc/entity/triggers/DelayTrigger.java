/*
 * Desc:     defines a trigger that only remains in the game for a specified
 *           number of update steps
 *  @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.entity.triggers;


import com.bham.bc.entity.BaseGameEntity;

abstract public class DelayTrigger<entity_type extends BaseGameEntity> extends Trigger<entity_type> {

    /**
     * the lifetime of this trigger in update-steps
     */
    protected int delayTime;

    public DelayTrigger(int x, int y, int delayTime) {
        super(BaseGameEntity.GetNextValidID(),x,y);
        this.delayTime = delayTime;
        setInactive();
    }

    /**
     * children of this class should always make sure this is called from within
     * their own update method
     */
    @Override
    public void update() {
        //if the lifetime counter expires set this trigger to be removed from
        //the game
        if (--delayTime <= 0) {
            setActive();
        }
    }

}

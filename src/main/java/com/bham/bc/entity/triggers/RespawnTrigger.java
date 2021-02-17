/**
 *  Desc:     base class to create a trigger that is capable of respawning
 *            after a period of inactivity
 */
package com.bham.bc.entity.triggers;


import com.bham.bc.entity.BaseGameEntity;

abstract public class RespawnTrigger<entity_type extends BaseGameEntity> extends Trigger<entity_type> {

    //When a bot comes within this trigger's area of influence it is triggered
    //but then becomes inactive for a specified amount of time. These values
    //control the amount of time required to pass before the trigger becomes 
    //active once more.
    protected int cyclePeriod;
    protected int curToRespawn;

    /**
     * sets the trigger to be inactive for m_iNumUpdatesBetweenRespawns
     * update-steps
     */
    protected void deactivate() {
        setInactive();
        curToRespawn = cyclePeriod;
    }

    public RespawnTrigger(int id, int x, int y) {
        super(id,x,y);
        cyclePeriod = 0;
        curToRespawn = 0;
    }

    /**
     * to be implemented by child classes
     */
    @Override
    abstract public void tryTrigger(entity_type entity);

    /**
     * this is called each game-tick to update the trigger's internal state
     */
    @Override
    public void update() {
        if ((--curToRespawn <= 0) && !isActive()) {
            setActive();
        }
    }

    public void setRespawnDelay(int numTicks) {
        cyclePeriod = numTicks;
    }
}
/**
 *  Desc:     base class to create a trigger that is capable of respawning
 *            after a period of inactivity
 */
package com.bham.bc.entity.triggers;


abstract public class RespawnTrigger extends Trigger {

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
     * this is called each game-tick to update the trigger's internal state
     */
    @Override
    public void update() {
//        System.out.println("respawn  after: "+curToRespawn);
        if ((--curToRespawn <= 0) && !isActive()) {
            setActive();
        }
    }

    public void setRespawnDelay(int numTicks) {
        cyclePeriod = numTicks;
    }
}
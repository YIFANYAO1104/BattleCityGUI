/**
 * Desc: Class to manage a collection of triggers. Triggers may be registered
 * with an instance of this class. The instance then takes care of updating
 * those triggers and of removing them from the system if their lifetime has
 * expired.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.common.Triggers;


import com.bham.bc.tank.Tank;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class TriggerSystem<trigger_type extends Trigger> {

    private ArrayList<trigger_type> m_Triggers = new ArrayList<trigger_type>();

    /**
     * this method iterates through all the triggers present in the system and
     * calls their Update method in order that their internal state can be
     * updated if necessary. It also removes any triggers from the system that
     * have their m_bRemoveFromGame field set to true.
     */
    private void updateTriggers() {
        Iterator<trigger_type> it = m_Triggers.iterator();
        while (it.hasNext()) {
            trigger_type curTrg = it.next();
            if (curTrg.isToBeRemoved()) {
                it.remove();
            } else {
                curTrg.update();
            }
        }
    }

    /**
     * this method iterates through the container of entities passed as a
     * parameter and passes each one to the Try method of each trigger
     * *provided* the entity is alive and provided the entity is ready for a
     * trigger update.
     */
    private void tryTriggers(Tank curEnt) {
        //test each entity against the triggers

        //an entity must be ready for its next trigger update and it must be
        //alive before it is tested against each trigger.
//      if (curEnt.isReadyForTriggerUpdate() && curEnt.isAlive())
        for (Trigger curTrg : m_Triggers) {
            curTrg.tryTrigger(curEnt);
        }
    }

    /**
     * this deletes any current triggers and empties the trigger list
     */
    public void clear() {
        m_Triggers.clear();
    }

    /**
     * This method should be called each update-step of the game. It will first
     * update the internal state odf the triggers and then try each entity
     * against each active trigger to test if any should be triggered.
     */
    public void update(Tank entity) {
        updateTriggers();
        tryTriggers(entity);
    }

    /**
     * this is used to register triggers with the TriggerSystem (the
     * TriggerSystem will take care of tidying up memory used by a trigger)
     */
    public void register(trigger_type trigger) {
        m_Triggers.add(trigger);
    }

    /**
     * some triggers are required to be rendered (like giver-triggers for
     * example)
     */
    public void render(Graphics g) {
        for (trigger_type curTrg : m_Triggers) {
            curTrg.render(g);
        }
    }

    public ArrayList<trigger_type> getTriggers() {
        return m_Triggers;
    }
}
/**
 * Desc: Class to manage a collection of triggers. Triggers may be registered
 * with an instance of this class. The instance then takes care of updating
 * those triggers and of removing them from the system if their lifetime has
 * expired.
 */
package com.bham.bc.components.triggers;


import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.Obstacle;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class TriggerSystem {

    private ArrayList<Trigger> triggers = new ArrayList<>();

    /**
     * this method iterates through all the triggers present in the system and
     * calls their Update method in order that their internal state can be
     * updated if necessary. It also removes any triggers from the system that
     * have their m_bRemoveFromGame field set to true.
     */
    public void update() {
//        Iterator<Trigger> it = triggers.iterator();
//        while (it.hasNext()) {
//            Trigger curTrg = it.next();
//            if (!curTrg.exists()) {
//                it.remove();
//            } else {
//                curTrg.update();
//            }
//        }
        // Does not have a big impact for performance
        triggers.forEach(Trigger::update);
        triggers.removeIf(trigger -> !trigger.exists);
    }

    /**
     * this deletes any current triggers and empties the trigger list
     */
    public void clear() {
        triggers.clear();
    }

    /**
     * This method should be called each update-step of the game. It will first
     * update the internal state odf the triggers and then try each entity
     * against each active trigger to test if any should be triggered.
     */
    public void handleAll(List<GameCharacter> characters, List<Obstacle> obstacles){
        triggers.forEach(trigger -> {
            characters.forEach(trigger::handleCharacter);
            obstacles.forEach(trigger::handleObstacle);
        });
    }

    /**
     * this is used to register triggers with the TriggerSystem (the
     * TriggerSystem will take care of tidying up memory used by a trigger)
     */
    public void register(Trigger trigger) {
        triggers.add(trigger);
    }

    /**
     * some triggers are required to be rendered (like giver-triggers for
     * example)
     */
    public void render(GraphicsContext gc) {
        triggers.forEach(trigger -> trigger.render(gc));
    }

    public ArrayList<Trigger> getTriggers() {
        return triggers;
    }
}
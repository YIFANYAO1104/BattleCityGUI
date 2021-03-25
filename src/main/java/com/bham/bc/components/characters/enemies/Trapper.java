package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.StateMachine;
import javafx.scene.shape.Shape;

/**
 * <h1>Trapper - throws traps everywhere</h1>
 *
 * <p>This type of enemy has 3 main states determined by time and location</p>
 * <ul>
 *     <li><b>Search</b> - in this state the enemy always tries to look for the home
 *     territory. It doesn't care about anything else that's going around it</li>
 *
 *     <li><b>Attack Ally</b> - in this state the enemy stops moving for a moment once a
 *     certain amount of time has passed and places a trap (e.g. a bomb/ mine)</li>
 *
 *     <li><b>Attack Home</b> - in this state enemy stops moving and laying traps, it simply
 *     stands in one spot and tries to take over the home territory</li>
 * </ul>
 */
public class Trapper extends Enemy {
    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x     top left x coordinate of the character
     * @param y     top left y coordinate of the character
     * @param speed value which defines the initial velocity
     * @param hp    health points the enemy should have
     */
    protected Trapper(double x, double y, double speed, double hp) {
        super(x, y, speed, hp);
    }

    @Override
    protected StateMachine createFSM() {
        return null;
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void update() {

    }
}

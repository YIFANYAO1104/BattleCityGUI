package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

import java.util.Arrays;

/**
 * <h1>Trapper - throws traps everywhere</h1>
 *
 * <p>This type of enemy has 3 main states determined by time and location</p>
 * <ul>
 *     <li><b>Search and Lay Trap</b> - in this state the enemy navigates around the map and
 *     lays a trap for the player </li>
 *
 *     <li><b>Retreat</b> - in this state the enemy runs away from Allies
 *     if they get close enough </li>
 *
 *     <li><b>Regenerate</b> - in this the enemy will stop and regenerate it's
 *     health if it below 20% health and is far enough away from the enemy</li>
 * </ul>
 */
public class Trapper extends Enemy {

    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy1"; //TODO: Change this
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    public static final int MAX_HP = 100; //Double the health for the strong enemy

    private final StateMachine stateMachine;
    private IntCondition badHealthCondition;
    private IntCondition closeToAlly;
    private AndCondition regenerateCondition;
    private AndCondition searchAndTrapCondition;
    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x     top left x coordinate of the character
     * @param y     top left y coordinate of the character
     * @param speed value which defines the initial velocity
     * @param hp    health points the enemy should have
     */
    protected Trapper(double x, double y, double speed, double hp) {
        super(x, y, 1, MAX_HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
        this.stateMachine = createFSM();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchAndTrapState = new State(new Action[]{ Action.SEARCHANDTRAP }, null);
        State retreatState = new State(new Action[]{ Action.RETREAT }, null);
        State regenerateState = new State(new Action[]{ Action.REGENERATE }, null);

        // Define all conditions required to change any state
        closeToAlly = new IntCondition(0, 50);
        badHealthCondition = new IntCondition(0, 20);
        regenerateCondition = new AndCondition(new NotCondition(closeToAlly), badHealthCondition);
        searchAndTrapCondition = new AndCondition(new NotCondition(closeToAlly), new NotCondition(badHealthCondition));

        // Define all state transitions that could happen
        Transition searchAndTrapPossibility = new Transition(searchAndTrapState, searchAndTrapCondition);
        Transition retreatPossibility = new Transition(retreatState, closeToAlly);
        Transition regeneratePossibility = new Transition(regenerateState, regenerateCondition);

        // Define how the states can transit from one another
        searchAndTrapState.setTransitions(new Transition[]{ retreatPossibility, regeneratePossibility });
        retreatState.setTransitions(new Transition[]{ searchAndTrapPossibility, regeneratePossibility });
        regenerateState.setTransitions(new Transition[]{ retreatPossibility, searchAndTrapPossibility});

        return new StateMachine(searchAndTrapState);
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void update() {
        // TODO: double distanceToAlly = find distance to nearest ally
        // TODO: closeToAlly.setTestValue((int) distanceToAlly);
        badHealthCondition.setTestValue((int) this.hp);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCHANDTRAP:
                    //TODO: searchAndTrap();
                    break;
                case RETREAT:
                    //TODO: retreat();
                    break;
                case REGENERATE:
                    // TODO: regenerate();
                    break;
            }
        });

    }
}

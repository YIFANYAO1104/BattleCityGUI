package com.bham.bc.components.characters.agents.enemies;

import com.bham.bc.components.characters.Side;
import com.bham.bc.components.characters.agents.Agent;
import com.bham.bc.entity.ai.behavior.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.util.Arrays;

import static com.bham.bc.entity.EntityManager.entityManager;

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
public class Trapper extends Agent {
    // Constant
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/trapper.png";
    public static final int SIZE = 30;

    // Configurable
    public static final double HP = 100;
    public static final double SPEED = 3;

    private final StateMachine stateMachine;
    private IntCondition badHealthCondition;
    private IntCondition closeToAlly;
    private AndCondition regenerateCondition;
    private AndCondition searchAndTrapCondition;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Trapper(double x, double y) {
        super(x, y, SPEED, HP, Side.ENEMY);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        this.stateMachine = createFSM();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchAndTrapState = new State(new Action[]{ Action.SEARCH_HOME }, null);
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
    public void update() {
        // TODO: double distanceToAlly = find distance to nearest ally
        // TODO: closeToAlly.setTestValue((int) distanceToAlly);
        badHealthCondition.setTestValue((int) this.hp);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_HOME:
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

    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }
}

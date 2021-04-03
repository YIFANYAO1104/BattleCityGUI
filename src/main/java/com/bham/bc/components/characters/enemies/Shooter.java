package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Arrays;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * <h1>Shooter - far-end operative</h1>
 *
 * <p>This type of enemy has 4 main states determined by free path condition and its HP</p>
 *
 * <ul>
 *     <li><b>Search</b> - searches for the closest ally if its HP is over 20% and it does not have
 *     "run away" property on. It doesn't care about anything else</li>
 *
 *     <li><b>Attack</b> - shoots at any ally if there are no obstacles in between and if its HP
 *     is over 20%. If the bullets are slow and the target is far, it kinda wastes its energy but
 *     ¯\_(ツ)_/¯</li>
 *
 *     <li><b>Retreat</b> - turns on "run away" property and searches for a "health giver" power-up
 *     or tries to retreat to a safe distance. If there is nowhere to retreat, it turns off "run away"
 *     property and remains in <b>Attack</b> state</li>
 *
 *     <li><b>Regenerate</b> - stops doing everything and regenerates its HP to 80%. Once it hits 80%,
 *     it turns of "run away" property</li>
 * </ul>
 */
public class Shooter extends Enemy {

    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/shooter.png";
    public static final int SIZE = 30;
    public static final double HP = 100;
    public static final double SPEED = 3;

    private final StateMachine stateMachine;
    private FreePathCondition noObstaclesCondition;
    private IntCondition lowHealthCondition;
    private IntCondition highHealthCondition;
    private AndCondition attackCondition;
    private BooleanCondition canRetreatCondition;
    private AndCondition retreatCondition;
    private IntCondition safeDistance;

    /**
     * Constructs an enemy instance with initial speed value set to 1
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Shooter(int x, int y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        stateMachine = createFSM();
    }

    @Override
    protected StateMachine createFSM(){
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.SEARCHALLY }, null);
        State attackState = new State(new Action[]{ Action.ATTACKALLY }, null);
        State retreatState = new State(new Action[]{ Action.RETREAT }, null);
        State regenerateState = new State(new Action[]{ Action.REGENERATE }, null);

        // Define all conditions required to change any state
        lowHealthCondition = new IntCondition(0, (int) (HP * .2));
        highHealthCondition = new IntCondition((int) (HP * .8), 100);
        noObstaclesCondition = new FreePathCondition();
        canRetreatCondition = new BooleanCondition();
        attackCondition = new AndCondition(new NotCondition(lowHealthCondition), noObstaclesCondition);
        retreatCondition = new AndCondition(canRetreatCondition, lowHealthCondition);
        safeDistance = new IntCondition(0, 50); // TODO: Figure out if this is a good distance

        // Define all state transitions that could happen
        Transition searchPossibility = new Transition(searchState, new AndCondition(highHealthCondition, new NotCondition(attackCondition)));
        Transition attackPossibility = new Transition(attackState, attackCondition);
        Transition retreatPossibility = new Transition(retreatState, retreatCondition);
        Transition regeneratePossibility = new Transition(regenerateState, safeDistance);

        // Define how the states can transit from one another
        searchState.setTransitions(new Transition[]{ attackPossibility });
        attackState.setTransitions(new Transition[]{ retreatPossibility, searchPossibility });
        retreatState.setTransitions(new Transition[]{ regeneratePossibility });
        regenerateState.setTransitions(new Transition[]{ searchPossibility });

        return new StateMachine(searchState);
    }

    @Override
    public void update() {
        double distanceToPlayer = getCenterPosition().distance(backendServices.getPlayerCenterPosition());

        safeDistance.setTestValue((int) distanceToPlayer);
        lowHealthCondition.setTestValue((int) hp);
        highHealthCondition.setTestValue((int) hp);
        noObstaclesCondition.setTestValues(getCenterPosition(), backendServices.getPlayerCenterPosition());
        // TODO: canRetreatCondition.setTestValue(SOME FUNCTION);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCHALLY:
                    search(ItemType.ally);
                    break;
                case ATTACKALLY:
                    aim();
                    shoot(0.3);
                    break;
                case RETREAT:
                    // TODO: retreat();
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
    public Shape getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .4);
    }

    @Override
    public String toString() { return "Default Enemy"; }
}

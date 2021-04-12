package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.triggers.effects.RingExplosion;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.util.Arrays;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * <h1>Kamikaze - fearless suicider</h1>
 *
 * <p>This type of enemy has 3 states determined by its distance to ally and free path condition</p>
 * <ul>
 *     <li><b>Search Ally</b> - searches for the closest ally in the game and moves towards it</li>
 *
 *     <li><b>Charge Ally</b> - charges at the nearest ally with increased speed if it is close enough
 *     and if there are no obstacles in a way to stop it</li>
 *
 *     <li><b>Attack Ally</b> - attacks any ally that is very close to it by self-destructing itself
 *     and dealing area damage to anything but allies and obstacles</li>
 * </ul>
 */
public class Kamikaze extends Enemy {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/kamikaze.png";
    public static final int SIZE = 30;
    public static final int HP = 100;
    public static final double SPEED = 3;

    private final StateMachine stateMachine;
    private FreePathCondition noObstacleCondition;
    private IntCondition chargeAllyCondition;
    private IntCondition attackAllyCondition;

    /**
     * Constructs a kamikaze type enemy
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Kamikaze(double x, double y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        //navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
        stateMachine = createFSM();
        navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.SEARCH_ALLY }, null);
        State chargeState = new State(new Action[]{ Action.SEARCH_ALLY }, null);
        State attackState = new State(new Action[]{ Action.ATTACK_ALLY }, null);

        // Set up required entry/exit actions
        searchState.setEntryActions(new Action[]{ Action.SET_SEARCH });
        searchState.setExitActions(new Action[]{ Action.RESET_SEARCH });
        chargeState.setEntryActions(new Action[]{ Action.SET_SPEED });
        chargeState.setExitActions(new Action[]{ Action.RESET_SPEED });

        // Define all conditions required to change any state
        noObstacleCondition = new FreePathCondition(getHitBoxRadius());
        chargeAllyCondition = new IntCondition(0, 150);
        attackAllyCondition = new IntCondition(0, 50);

        // Define all state transitions that could happen
        Transition searchPossibility = new Transition(searchState, new NotCondition(new AndCondition(chargeAllyCondition, noObstacleCondition)));
        Transition chargePossibility = new Transition(chargeState, new AndCondition(chargeAllyCondition, noObstacleCondition));
        Transition attackPossibility = new Transition(attackState, attackAllyCondition);

        // Define how the states can transit from one another
        searchState.setTransitions(new Transition[]{ chargePossibility });
        chargeState.setTransitions(new Transition[]{ attackPossibility, searchPossibility });
        attackState.setTransitions(new Transition[]{ });

        return new StateMachine(searchState);
    }

    @Override
    public void update() {
        double distanceToAlly = getCenterPosition().distance(backendServices.getClosestCenter(getCenterPosition(), ItemType.ALLY));

        attackAllyCondition.setTestValue((int) distanceToAlly);
        chargeAllyCondition.setTestValue((int) distanceToAlly);
        noObstacleCondition.setTestValues(getCenterPosition(), backendServices.getClosestCenter(getCenterPosition(), ItemType.ALLY));

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_ALLY:
                    search(ItemType.ALLY);
                    break;
                case CHARGE_ALLY:
                    aim();
                    break;
                case ATTACK_ALLY:
                    destroy();
                    break;
                case SET_SPEED:
                    setMaxSpeed(SPEED * 2);
                    break;
                case RESET_SPEED:
                    setMaxSpeed(SPEED);
                    break;
                case SET_SEARCH:
                    steering.setDecelerateOn(false);
                    steering.seekOn();
                    break;
                case RESET_SEARCH:
                    steering.seekOff();
                    steering.setDecelerateOn(true);
                    pathEdges.clear();
                    break;
            }
        });
        move();
    }

    @Override
    public void destroy() {
        exists = false;
        entityManager.removeEntity(this);
        Trigger explosion = new RingExplosion(getCenterPosition(), 50, side);
        backendServices.addTrigger(explosion);
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }

    @Override
    public String toString() {
        return "Kamikaze";
    }
}

package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.triggers.effects.RingExplosion;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Arrays;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * <h1>Kamikaze - fearless suicider</h1>
 *
 * <p>This type of enemy has 3 states determined by its distance to ally and free path condition</p>
 * <ul>
 *     <li><b>Search</b> - searches for the closest ally in the game and moves towards it</li>
 *
 *     <li><b>Charge</b> - charges at the nearest ally with increased speed if it is close enough
 *     and if there are no obstacles in a way to stop it</li>
 *
 *     <li><b>Attack</b> - attacks any ally that is very close to it by self-destructing itself and
 *     dealing area damage to anything but enemies</li>
 * </ul>
 */
public class Kamikaze extends Enemy {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/kamikaze.png";
    public static final int SIZE = 30;
    public static final int HP = 100;
    public static final double SPEED = 3;

    private final StateMachine stateMachine;
    private FreePathCondition noObstaclesCondition;
    private IntCondition closeRadiusCondition;
    private AndCondition chargeCondition;
    private IntCondition attackCondition;

    /**
     * Constructs a kamikaze type enemy
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Kamikaze(double x, double y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        stateMachine = createFSM();
        navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.SEARCHALLY }, null);
        State chargeState = new State(new Action[]{ Action.CHARGEALLY }, null);
        State attackState = new State(new Action[]{ Action.ATTACKALLY }, null);

        // Define all conditions required to change any state
        closeRadiusCondition = new IntCondition(0, 100);
        noObstaclesCondition = new FreePathCondition();
        chargeCondition = new AndCondition(closeRadiusCondition, noObstaclesCondition);
        attackCondition = new IntCondition(0, 40);

        // Define all state transitions that could happen
        Transition searchPossibility = new Transition(searchState, new NotCondition(chargeCondition));
        Transition chargePossibility = new Transition(chargeState, chargeCondition);
        Transition attackPossibility = new Transition(attackState, attackCondition);

        // Define how the states can transit from one another
        searchState.setTransitions(new Transition[]{ chargePossibility });
        chargeState.setTransitions(new Transition[]{ attackPossibility, searchPossibility });
        attackState.setTransitions(new Transition[]{ });

        return new StateMachine(searchState);
    }

    @Override
    public void update() {
        double distanceToPlayer = getCenterPosition().distance(backendServices.getPlayerCenterPosition());

        attackCondition.setTestValue((int) distanceToPlayer);
        closeRadiusCondition.setTestValue((int) distanceToPlayer);
        noObstaclesCondition.setTestValues(getCenterPosition(), backendServices.getPlayerCenterPosition());

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCHALLY:
                    search(ItemType.ALLY);
                    break;
                case CHARGEALLY:
                    charge();
                    break;
                case ATTACKALLY:
                    destroy();
                    break;
            }
        });
    }

    @Override
    public void destroy() {
        exists = false;
        entityManager.removeEntity(this);
        Trigger explosion = new RingExplosion(getCenterPosition(), 50, side);
        backendServices.addTrigger(explosion);
    }

    @Override
    public Shape getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }
}

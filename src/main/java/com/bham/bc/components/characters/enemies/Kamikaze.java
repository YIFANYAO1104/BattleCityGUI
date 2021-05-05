package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.components.triggers.effects.RingExplosion;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.util.Arrays;

import static com.bham.bc.components.Controller.services;
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
    public static final String IMAGE_PATH = "img/characters/kamikaze.png";
    public static final int SIZE = 30;
    public static final int HP = 50;
    public static final double SPEED = 4;

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
        try{
        entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        }catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }
        navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
        stateMachine = createFSM();
        steering.seekOn();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.SEARCH_ALLY }, null);
        State chargeState = new State(new Action[]{ Action.CHARGE_ALLY }, null);
        State attackState = new State(new Action[]{ Action.ATTACK_ALLY }, null);

        // Set up required entry/exit actions
        searchState.setEntryActions(new Action[]{ Action.SET_SEARCH });
        searchState.setExitActions(new Action[]{ Action.RESET_SEARCH });
        chargeState.setEntryActions(new Action[]{ Action.SET_SPEED });
        chargeState.setExitActions(new Action[]{ Action.RESET_SPEED });

        // Define all conditions required to change any state
        noObstacleCondition = new FreePathCondition(getHitBoxRadius());
        chargeAllyCondition = new IntCondition(0, 200);
        attackAllyCondition = new IntCondition(0, 40);

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
        double distanceToAlly = getCenterPosition().distance(services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
        attackAllyCondition.setTestValue((int) distanceToAlly);
        chargeAllyCondition.setTestValue((int) distanceToAlly);
        noObstacleCondition.setTestValues(getCenterPosition(), services.getClosestCenter(getCenterPosition(), ItemType.ALLY));

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_ALLY:
                    search(ItemType.ALLY);
                    steering.seekOn();
                    break;
                case CHARGE_ALLY:
                    face(ItemType.ALLY);
                    velocity = heading.multiply(maxSpeed);
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
                    steering.seekOn();
                    break;
                case RESET_SEARCH:
                    steering.seekOff();
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
        Trigger dissolve = new Dissolve(getPosition(), entityImages[0], getAngle());
        services.addTrigger(dissolve);
        services.addTrigger(explosion);
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

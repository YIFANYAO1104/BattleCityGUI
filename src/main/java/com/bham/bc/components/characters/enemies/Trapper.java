package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Arrays;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * <h1>Trapper - throws traps everywhere</h1>
 *
 * <p>This type of enemy has 3 main states determined by time and location</p>
 * <ul>
 *     <li><b>Search Home</b> - navigates to the home base and lays traps for allies</li>
 *
 *     <li><b>Attack Home</b> - takes over the home base</li>
 * </ul>
 */
public class Trapper extends Enemy {
    // Constant
    public static final String IMAGE_PATH = "img/characters/trapper.png";
    public static final int SIZE = 30;

    // Configurable
    public static final double HP = 50;
    public static final double SPEED = 3;

    private final StateMachine stateMachine;
    private IntCondition nearToHomeCondition;

    private int timeTillTrap;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Trapper(double x, double y) {
        super(x, y, SPEED, HP);
        try{
        entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        }catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }
        navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
        timeTillTrap = 100;
        this.stateMachine = createFSM();
        steering.seekOn();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchHomeState = new State(new Action[]{ Action.SEARCH_HOME, Action.LAY_TRAP }, null);
        State attackHomeState = new State(new Action[]{ Action.ATTACK_HOME }, null);

        // Set up entry/exit actions
        searchHomeState.setEntryActions(new Action[]{ Action.SET_SEARCH });
        searchHomeState.setExitActions(new Action[]{ Action.RESET_SEARCH });

        // Define all conditions required to change any state
        nearToHomeCondition = new IntCondition(0, (int) (services.getHomeArea().getRadius() * .8));

        // Define all state transitions that could happen
        Transition searchHomePossibility = new Transition(searchHomeState, new NotCondition(nearToHomeCondition));
        Transition attackHomePossibility = new Transition(attackHomeState, nearToHomeCondition);

        // Define how the states can transit from one another
        searchHomeState.setTransitions(new Transition[]{ attackHomePossibility });
        attackHomeState.setTransitions(new Transition[]{ searchHomePossibility });

        return new StateMachine(searchHomeState);
    }

    @Override
    public void update() {
        double distanceToHome = getCenterPosition().distance(services.getClosestCenter(getCenterPosition(), ItemType.HOME));
        nearToHomeCondition.setTestValue((int) distanceToHome);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_HOME:
                    search(ItemType.HOME);
                    break;
                case LAY_TRAP:
                    if(--timeTillTrap <= 0) {
                        layRandomTrap(50);
                        timeTillTrap = 100;
                    }
                    break;
                case ATTACK_HOME:
                    takeOver();
                    break;
                case SET_SEARCH:
                    steering.setDecelerate(false);
                    steering.seekOn();
                    break;
                case RESET_SEARCH:
                    steering.seekOff();
                    steering.setDecelerate(true);
                    pathEdges.clear();
                    break;
            }
        });
        move();
    }

    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;

        Trigger dissolve = new Dissolve(getPosition(), entityImages[0], getAngle());
        services.addTrigger(dissolve);
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }
}

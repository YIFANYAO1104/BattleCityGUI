package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.util.Arrays;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * <h1>Tank - not a tank but heavy enough</h1>
 *
 * <p>This type of enemy has 3 main states determined by its HP and location</p>
 * <ul>
 *     <li><b>Search</b> - in this state, whenever the enemy has over 20% of HP, it tries to find
 *     the home base. It doesn't care about anything than searching and moving towards it</li>
 *
 *     <li><b>Attack Home</b> - in this state, whenever the enemy has over 20% of HP, it tries to
 *     take over the home base. It doesn't care about anything than damaging the home base</li>
 *
 *     <li><b>Attack Ally</b> - in this state, whenever the enemy has 20% or less HP, it tries to
 *     defend itself by shooting at the closest ally. It doesn't move or damage anything but the
 *     closest ally</li>
 * </ul>
 */
public class Tank extends Enemy {
    // Constant parameters
    public static final String IMAGE_PATH = "img/characters/tank.png";
    public static final int SIZE = 30;

    // Configurable
    public static final double HP = 150;
    public static final double SPEED = 2;

    // Behavior
    private final StateMachine stateMachine;
    private FreePathCondition noObstacleCondition;
    private IntCondition highHealthCondition;
    private IntCondition nearToHomeCondition;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Tank(double x, double y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        stateMachine = createFSM();
        GUN.setRate(1000);
        GUN.setDamageFactor(3);
        steering.seekOn();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchHomeState = new State(new Action[]{ Action.SEARCH_HOME, Action.ATTACK_OBST }, null);
        State attackHomeState = new State(new Action[]{ Action.ATTACK_HOME }, null);
        State attackAllyState = new State(new Action[]{ Action.ATTACK_ALLY }, null);

        // Set up entry/exit actions
        searchHomeState.setEntryActions(new Action[]{ Action.SET_SEARCH, Action.SET_RATE });
        searchHomeState.setExitActions(new Action[]{ Action.RESET_SEARCH, Action.RESET_RATE });

        // Helper conditions to make the code easier to understand
        AndCondition attackHomeCondition;

        // Define all conditions required to change any state
        noObstacleCondition = new FreePathCondition();
        highHealthCondition = new IntCondition((int) (HP * .2), (int) HP);
        nearToHomeCondition = new IntCondition(0, (int) (services.getHomeArea().getRadius() * .8));
        attackHomeCondition = new AndCondition(nearToHomeCondition, highHealthCondition);

        // Define all state transitions that could happen
        Transition searchHomePossibility = new Transition(searchHomeState, new AndCondition(new NotCondition(attackHomeCondition), highHealthCondition));
        Transition attackHomePossibility = new Transition(attackHomeState, attackHomeCondition);
        Transition attackAllyPossibility = new Transition(attackAllyState, new NotCondition(highHealthCondition));

        // Define how the states can transit from one another
        searchHomeState.setTransitions(new Transition[]{ attackHomePossibility, attackAllyPossibility });
        attackHomeState.setTransitions(new Transition[]{ attackAllyPossibility, searchHomePossibility });
        attackAllyState.setTransitions(new Transition[]{ searchHomePossibility });  // Can only happen if it heals up

        return new StateMachine(searchHomeState);
    }

    @Override
    public void update() {
        double distanceToHome = getCenterPosition().distance(services.getClosestCenter(getCenterPosition(), ItemType.HOME));
        highHealthCondition.setTestValue((int) hp);
        nearToHomeCondition.setTestValue((int) distanceToHome);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_HOME:
                    search(ItemType.HOME);
                    break;
                case ATTACK_HOME:
                    takeOver();
                    break;
                case ATTACK_ALLY:
                    noObstacleCondition.setTestValues(getCenterPosition(), services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
                    if(noObstacleCondition.test()) {
                        face(ItemType.ALLY);
                        GUN.shoot();
                    }
                    break;
                case ATTACK_OBST:
                    setMaxSpeed(shootObstacle() ? SPEED * .5 : SPEED);
                    break;
                case SET_RATE:
                    GUN.setDamageFactor(3);
                    break;
                case RESET_RATE:
                    GUN.setDamageFactor(1);
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

    @Override
    public String toString() {
        return "Tank";
    }
}

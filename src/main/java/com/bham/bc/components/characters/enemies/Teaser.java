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
 * <h1>Teaser - annoying attention grabber</h1>
 *
 * <p>This type of enemy has 4 main states determined by its HP and distance to ally or home</p>
 * <ul>
 *     <li><b>Search Ally</b> - searches for the closest ally and moves towards it if it has
 *     over 20% of HP. It doesn't shoot in this state even if anything else attacks it</li>
 *
 *     <li><b>Attack Ally</b> - it shoots at the closest ally if it is close enough and if there
 *     are no obstacles between them. It should also have over 20% of HP. It does not move while
 *     it is shooting</li>
 *
 *     <li><b>Search Home</b> - it searches for home with an increased speed if its HP is 20% or
 *     less</li>
 *
 *     <li><b>Attack Home</b> - it attacks the home base by standing in one spot and taking away
 *     "HP" almost as if it was progressively infecting it. It doesn't shoot or run away</li>
 * </ul>
 */
public class Teaser extends Enemy {
    // Constant
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/teaser.png";
    public static final int SIZE = 30;
    public static final EnemyType TRIBE = EnemyType.TEASER;

    // Configurable
    public static final double HP = 50;
    public static final double SPEED = 5;

    // Behavior
    private final StateMachine stateMachine;
    private FreePathCondition noObstacleCondition;
    private IntCondition highHealthCondition;
    private IntCondition nearToAllyCondition;
    private IntCondition nearToHomeCondition;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Teaser(double x, double y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        stateMachine = createFSM();

        GUN.setRate(1200);
        GUN.setDamageFactor(3);
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchAllyState = new State(new Action[]{ Action.SEARCH_ALLY, Action.ATTACK_OBST }, null);
        State searchHomeState = new State(new Action[]{ Action.SEARCH_HOME, Action.ATTACK_OBST }, null);
        State attackAllyState = new State(new Action[]{ Action.ATTACK_ALLY }, null);
        State attackHomeState = new State(new Action[]{ Action.ATTACK_HOME }, null);

        // Set up entry/exit actions
        searchHomeState.setEntryActions(new Action[]{ Action.SET_SPEED, Action.SET_RATE, Action.SET_SEARCH });
        searchHomeState.setExitActions(new Action[]{ Action.RESET_SPEED, Action.RESET_RATE, Action.RESET_SEARCH });
        searchAllyState.setEntryActions(new Action[]{ Action.SET_RATE, Action.SET_SEARCH });
        searchAllyState.setExitActions(new Action[]{ Action.RESET_RATE, Action.RESET_SEARCH });

        // Helper conditions to make the code easier to understand
        AndCondition attackHomeCondition;
        AndCondition attackAllyCondition;

        // Define all conditions required to change any state
        noObstacleCondition = new FreePathCondition();
        highHealthCondition = new IntCondition((int) (HP * .2), (int) HP);
        nearToAllyCondition = new IntCondition(0, 200);
        nearToHomeCondition = new IntCondition(0, (int) (services.getHomeArea().getRadius() * .8));
        attackAllyCondition = new AndCondition(new AndCondition(noObstacleCondition, nearToAllyCondition), highHealthCondition);
        attackHomeCondition = new AndCondition(nearToHomeCondition, new NotCondition(highHealthCondition));

        // Define all state transitions that could happen
        Transition searchAllyPossibility = new Transition(searchAllyState, new AndCondition(new NotCondition(attackAllyCondition), highHealthCondition));
        Transition searchHomePossibility = new Transition(searchHomeState, new AndCondition(new NotCondition(attackHomeCondition), new NotCondition(highHealthCondition)));
        Transition attackAllyPossibility = new Transition(attackAllyState, attackAllyCondition);
        Transition attackHomePossibility = new Transition(attackHomeState, attackHomeCondition);

        // Define how the states can transit from one another
        searchAllyState.setTransitions(new Transition[]{ attackAllyPossibility, searchHomePossibility });   // In case enemy runs over a trap and its HP becomes low
        searchHomeState.setTransitions(new Transition[]{ attackHomePossibility, searchAllyPossibility });   // In case enemy runs over HP buff and is able to attack
        attackAllyState.setTransitions(new Transition[]{ searchAllyPossibility, searchHomePossibility });
        attackHomeState.setTransitions(new Transition[]{ searchHomePossibility, searchAllyPossibility });

        return new StateMachine(searchAllyState);
    }

    @Override
    public void update() {
        double distanceToAlly = getCenterPosition().distance(services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
        double distanceToHome = getCenterPosition().distance(services.getClosestCenter(getCenterPosition(), ItemType.HOME));

        noObstacleCondition.setTestValues(getCenterPosition(), services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
        highHealthCondition.setTestValue((int) hp);
        nearToAllyCondition.setTestValue((int) distanceToAlly);
        nearToHomeCondition.setTestValue((int) distanceToHome);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_ALLY:
//                    System.out.println("SEARCH_ALLY");
                    search(ItemType.ALLY);
                    break;
                case SEARCH_HOME:
//                    System.out.println("SEARCH_HOME");
                    search(ItemType.HOME);
                    break;
                case ATTACK_ALLY:
//                    System.out.println("ATTACK_ALLY");
                    face(ItemType.ALLY);
                    //shoot(.8);
                    GUN.shoot();
                    break;
                case ATTACK_HOME:
//                    System.out.println("ATTACK_HOME");
                    takeOver();
                    break;
                case ATTACK_OBST:
//                    System.out.println("ATTACK_OBST");
                    setMaxSpeed(shootObstacle() ? SPEED * .5 : SPEED);
                    break;
                case SET_SPEED:
//                    System.out.println("SET_SPEED");
                    setMaxSpeed(SPEED * 3);
                    break;
                case RESET_SPEED:
//                    System.out.println("RESET_SPEED");
                    setMaxSpeed(SPEED);
                    break;
                case SET_RATE:
//                    System.out.println("SET_RATE");
                    GUN.setRate(1000);
                    GUN.setDamageFactor(3);
                    break;
                case RESET_RATE:
//                    System.out.println("RESET_RATE");
                    GUN.setRate(2000);
                    GUN.setDamageFactor(1);
                    break;
                case SET_SEARCH:
//                    System.out.println("SET_SEARCH");
                    steering.setDecelerateOn(false);
                case RESET_SEARCH:
//                    System.out.println("RESET_SEARCH");
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
        entityManager.removeEntity(this);
        exists = false;

        Trigger dissolve = new Dissolve(getPosition(), entityImages[0], getAngle());
        services.addTrigger(dissolve);
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .43);
    }

    @Override
    public String toString() {
        return "Teaser";
    }
}

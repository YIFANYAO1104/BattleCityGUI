package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.edge.GraphEdge;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

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
 *     "go back" property on. If there are obstacles in a way, it shoots them with increased fire
 *     rate.</li>
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

    private FreePathCondition noObstCondition;
    private BooleanCondition goBackCondition;

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
        GUN.setRate(1000);
    }

    @Override
    protected StateMachine createFSM(){
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.SEARCH_ALLY, Action.ATTACK_OBST }, null);
        State attackState = new State(new Action[]{ Action.ATTACK_ALLY }, null);
        State retreatState = new State(new Action[]{ Action.RETREAT, Action.REGENERATE, Action.ATTACK_OBST }, null);

        // Set up necessary entry actions for certain states
        attackState.setEntryActions(new Action[]{ Action.RESET_RATE });
        searchState.setEntryActions(new Action[]{ Action.RESET_SEARCH, Action.SET_RATE });
        retreatState.setEntryActions(new Action[]{ Action.RESET_SEARCH, Action.SET_RATE });

        // Define all conditions required to change any state
        noObstCondition = new FreePathCondition();
        goBackCondition = new BooleanCondition();

        // Define all state transitions that could happen
        Transition searchPossibility = new Transition(searchState, new NotCondition(new OrCondition(goBackCondition, noObstCondition)));
        Transition attackPossibility = new Transition(attackState, new AndCondition(new NotCondition(goBackCondition), noObstCondition));
        Transition retreatPossibility = new Transition(retreatState, goBackCondition);

        // Define how the states can transit from one another
        searchState.setTransitions(new Transition[]{ attackPossibility, retreatPossibility });
        attackState.setTransitions(new Transition[]{ retreatPossibility, searchPossibility });
        retreatState.setTransitions(new Transition[]{ searchPossibility, attackPossibility });

        return new StateMachine(searchState);
    }

    @Override
    public void update() {
        noObstCondition.setTestValues(getCenterPosition(), backendServices.getPlayerCenterPosition());

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_ALLY:
                    search(ItemType.ALLY);
                    goBackCondition.setTestValue(hp <= HP * .2);
                    break;
                case ATTACK_ALLY:
                    face(backendServices.getClosestCenter(getCenterPosition(), ItemType.ALLY));
                    shoot(0.5);
                    goBackCondition.setTestValue(hp <= HP * .2);
                    break;
                case ATTACK_OBST:
                    if(edgeBehavior == GraphEdge.shoot) {
                        face(backendServices.getClosestCenter(getCenterPosition(), ItemType.SOFT));
                        GUN.shoot();
                    }
                    break;
                case RETREAT:
                    search(ItemType.ENEMY_AREA);
                    if(Arrays.stream(backendServices.getEnemyAreas()).anyMatch(this::intersects)) {
                        changeHp(HP);
                        goBackCondition.setTestValue(false);
                    }
                    break;
                case REGENERATE:
                    changeHp(HP * .003);
                    goBackCondition.setTestValue(hp < HP * .8);
                case SET_RATE:
                    GUN.setRate(200);
                    break;
                case RESET_RATE:
                    GUN.setRate(1000);
                    break;
                case RESET_SEARCH:
                    pathEdges.clear();
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
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .4);
    }

    @Override
    public String toString() {
        return "Shooter";
    }
}

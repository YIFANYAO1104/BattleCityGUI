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
 * <h1>Shooter - far-end operative</h1>
 *
 * <p>This type of enemy has 3 main states determined by free path condition and its HP</p>
 *
 * <ul>
 *     <li><b>Search Ally</b> - searches for the closest ally if its HP is over 30% and it does not
 *     have "go back" property on. If there are obstacles in a way, it shoots them with increased
 *     fire rate</li>
 *
 *     <li><b>Attack Ally</b> - shoots at any ally if there are no obstacles in between and if its HP
 *     is over 30%. If the bullets are slow and the target is far, it kinda wastes its energy but
 *     does not matter</li>
 *
 *     <li><b>Retreat</b> - turns on "run away" property and searches for the enemy spawn area to
 *     retreat to. While it retreats, it gradually regenerates its HP and once if it manages to reach
 *     its spawn area, it fully restores its health</li>
 * </ul>
 */
public class Shooter extends Enemy {

    public static final String IMAGE_PATH = "img/characters/shooter.png";
    public static final int SIZE = 30;
    public static final double HP = 50;
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
    public Shooter(double x, double y) {
        super(x, y, SPEED, HP);
        try{
        entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
        }catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }
        stateMachine = createFSM();
        steering.seekOn();

        GUN.setRate(1000);
        GUN.setDamageFactor(3);
    }

    @Override
    protected StateMachine createFSM(){
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.SEARCH_ALLY, Action.ATTACK_OBST }, null);
        State attackState = new State(new Action[]{ Action.ATTACK_ALLY }, null);
        State retreatState = new State(new Action[]{ Action.RETREAT, Action.REGENERATE, Action.ATTACK_OBST }, null);

        // Set up necessary entry actions for certain states
        searchState.setEntryActions(new Action[]{ Action.SET_SEARCH, Action.SET_RATE });
        searchState.setExitActions(new Action[]{ Action.RESET_SEARCH, Action.RESET_RATE });
        retreatState.setEntryActions(new Action[]{ Action.SET_SEARCH, Action.SET_SPEED });
        retreatState.setExitActions(new Action[]{ Action.RESET_SEARCH, Action.RESET_SPEED });

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
        noObstCondition.setTestValues(getCenterPosition(), services.getClosestCenter(getCenterPosition(), ItemType.ALLY));

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCH_ALLY:
                    search(ItemType.ALLY);
                    steering.seekOn();
                    goBackCondition.setTestValue(hp <= HP * .3);
                    break;
                case ATTACK_ALLY:
                    face(ItemType.ALLY);
                    GUN.shoot();
                    goBackCondition.setTestValue(hp <= HP * .3);
                    break;
                case ATTACK_OBST:
                    setMaxSpeed(shootObstacle() ? SPEED * .3 : SPEED);
                    break;
                case RETREAT:
                    search(ItemType.ENEMY_AREA);
                    if(Arrays.stream(services.getEnemyAreas()).anyMatch(this::intersects)) {
                        changeHp(HP);
                        goBackCondition.setTestValue(false);
                    }
                    break;
                case REGENERATE:
                    changeHp(HP * .003);
                    goBackCondition.setTestValue(hp < HP * .8);
                case SET_RATE:
                    GUN.setDamageFactor(3);
                    break;
                case RESET_RATE:
                    GUN.setDamageFactor(1);
                    break;
                case SET_SPEED:
                    setMaxSpeed(SPEED * 2);
                    break;
                case RESET_SPEED:
                    setMaxSpeed(SPEED);
                    break;
                case SET_SEARCH:
                    steering.seekOn();
                    steering.setDecelerate(false);
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
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .4);
    }

    @Override
    public String toString() {
        return "Shooter";
    }
}

package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Arrays;

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
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/tank.png";
    public static final int SIZE = 30;
    public static final int HP = 200;
    public static final double SPEED = 1;


    private final StateMachine stateMachine;
    private IntCondition lowHealthCondition;
    private IntCondition reachedHomeCondition;
    private AndCondition attackHomeCondition;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Tank(double x, double y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        this.stateMachine = createFSM();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchHome = new State(new Action[]{ Action.SEARCHHOME }, null);
        State attackHome = new State(new Action[]{ Action.ATTACKHOME }, null);
        State attackAlly = new State(new Action[]{ Action.ATTACKALLY }, null);

        // Define all conditions required to change any state
        reachedHomeCondition = new IntCondition(0, 50);
        lowHealthCondition = new IntCondition(0, 40);
        attackHomeCondition = new AndCondition(reachedHomeCondition, new NotCondition(lowHealthCondition));

        // Define all state transitions that could happen
        Transition attackHomePossibility = new Transition(attackHome, attackHomeCondition);
        Transition attackAllyPossibility = new Transition(attackAlly, lowHealthCondition);

        // Define how the states can transit from one another
        searchHome.setTransitions(new Transition[]{ attackHomePossibility });
        attackHome.setTransitions(new Transition[]{ attackAllyPossibility });
        attackAlly.setTransitions(new Transition[]{ });

        return new StateMachine(searchHome);
    }

    @Override
    public void update() {
        // TODO: double distanceToHome = find distance to home

        // TODO: closeToHome.setTestValue((int) distanceToHome);
        lowHealthCondition.setTestValue((int) this.hp);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCHHOME:
                    search(ItemType.HOME);
                    break;
                case ATTACKHOME:
                    takeOver();
                    break;
                case ATTACKALLY:
                    aim();
                    shoot(.9);
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
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }
}

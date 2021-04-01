package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.characters.Tribe;
import com.bham.bc.entity.ai.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Arrays;

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
 *     "HP" almost as if it was progressively infecting it. It doesn't shoot or run away.</li>
 * </ul>
 */
public class Teaser extends Enemy {
    // Constant
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/teaser.png";
    public static final int SIZE = 30;
    public static final Tribe TYPE = Tribe.TEASER;

    // Configurable
    public static final double HP = 50;
    public static final double SPEED = 5;

    // Behavior
    private final StateMachine stateMachine;
    private IntCondition badHealthCondition;
    private IntCondition closeToHome;
    private IntCondition closeToAlly;
    private AndCondition attackHomeCondition;
    private AndCondition attackAllyCondition;
    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Teaser(double x, double y) {
        super(x, y, 1, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        this.stateMachine = createFSM();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchAllyState = new State(new Action[]{ Action.SEARCHALLY }, null);
        State attackAllyState = new State(new Action[]{ Action.ATTACKALLY }, null);
        State searchHomeState = new State(new Action[]{ Action.SEARCHHOME }, null);
        State attackHomeState = new State(new Action[]{ Action.ATTACKHOME}, null);

        // Define all conditions required to change any state
        closeToHome = new IntCondition(0, 50);
        closeToAlly = new IntCondition(0, 50);
        badHealthCondition = new IntCondition(0, 10);
        attackHomeCondition = new AndCondition(closeToHome, badHealthCondition);
        attackAllyCondition = new AndCondition(closeToAlly, new NotCondition(badHealthCondition));

        // Define all state transitions that could happen
        Transition attackHomePossibility = new Transition(attackHomeState, attackHomeCondition);
        Transition searchHomePossibility = new Transition(searchHomeState, badHealthCondition);
        Transition attackAllyPossibility = new Transition(attackAllyState, attackAllyCondition);

        // Define how the states can transit from one another
        searchAllyState.setTransitions(new Transition[]{ attackAllyPossibility, searchHomePossibility });
        attackHomeState.setTransitions(new Transition[]{ });
        attackAllyState.setTransitions(new Transition[]{ searchHomePossibility});
        searchHomeState.setTransitions(new Transition[]{ attackHomePossibility });

        return new StateMachine(searchAllyState);
    }

    @Override
    public void update() {
        // TODO: double distanceToHome = find distance to home
        // TODO: closeToHome.setTestValue((int) distanceToHome);
        // TODO: double distanceToAlly = find distance to nearest ally
        // TODO: closeToAlly.setTestValue((int) distanceToAlly);
        badHealthCondition.setTestValue((int) this.hp);

        Action[] actions = stateMachine.update();
        Arrays.stream(actions).forEach(action -> {
            switch(action) {
                case SEARCHALLY:
                    //TODO: searchAlly();
                    break;
                case ATTACKALLY:
                    //TODO: attackAlly();
                    break;
                case SEARCHHOME:
                    // TODO: searchHome();
                    break;
                case ATTACKHOME:
                    // TODO: attackHome();
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
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .43);
    }
}

package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.components.triggers.effects.RingExplosion;
import com.bham.bc.entity.ai.behavior.*;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.util.Arrays;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * <h1>Splitter - revenge tastes better when its double</h1>
 *
 * <p>This type of enemy has 2 states determined by its location to the home base. Upon death, it summons 2 mini copies of
 * itself that attack the player</p>
 *
 * <ul>
 *     <li><b>Search Home</b> - in this state, enemy searches for the home base and doesn't react to th environment</li>
 *     <li><b>Attack Home</b> - in this state, if the enemy has reached the home territory, it steadily takes it over</li>
 * </ul>
 *
 * @see MiniSplitter
 */
public class Splitter extends Enemy {
    // Constant parameters
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/splitter.png";
    public static final int SIZE = 28;

    // Configurable
    public static final double HP = 150;
    public static final double SPEED = 2;

    // Behavior
    private final StateMachine stateMachine;
    private IntCondition nearToHomeCondition;

    /**
     * Constructs a Splitter enemy type
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Splitter(double x, double y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
        stateMachine = createFSM();
        steering.seekOn();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchHomeState = new State(new Action[]{ Action.SEARCH_HOME }, null);
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
        attackHomeState.setTransitions(new Transition[]{ searchHomePossibility });  // Can never happen unless the enemy is pushed outside home radius

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
    protected void destroy() {
        exists = false;
        entityManager.removeEntity(this);

        Trigger explosion = new RingExplosion(getCenterPosition(), 50, side);
        services.addTrigger(explosion);

        Trigger dissolve = new Dissolve(getPosition(), entityImages[0], getAngle());
        services.addTrigger(dissolve);

        services.addCharacter(new MiniSplitter(getCenterPosition().getX()-8, getCenterPosition().getY()-8));
        services.addCharacter(new MiniSplitter(getCenterPosition().getX()+8, getCenterPosition().getY()+8));
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }

    @Override
    public String toString() {
        return "Splitter";
    }

    /**
     * <h1>Mini Splitter - doesn't split but at least it's mini</h1>
     *
     * <p>This type of enemy is a mini copy of {@link Splitter} enemy type. It has 2 states determined by its location to the player</p>
     * <ul>
     *     <li><b>Search Ally</b> - in this state, the mini-copy of the enemy looks for the closest ally and approaches it</li>
     *     <li><b>Attack Ally</b> - in this state, the mini-copy of the enemy explodes itself to deal damage to the ally if it
     *     is close enough to it.</li>
     * </ul>
     */
    public static class MiniSplitter extends Enemy {
        // Constant parameters
        public static final String IMAGE_PATH = "file:src/main/resources/img/characters/splitter.png";
        public static final int SIZE = 15;

        // Configurable
        public static final double HP = 20;
        public static final double SPEED = 5;

        // Behavior
        private final StateMachine stateMachine;
        private IntCondition nearToAllyCondition;

        /**
         * Constructs a MiniSplitter enemy type
         *
         * @param x top left x coordinate of the enemy
         * @param y top left y coordinate of the enemy
         */
        protected MiniSplitter(double x, double y) {
            super(x, y, SPEED, HP);
            navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
            entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
            stateMachine = createFSM();
            steering.seekOn();
        }

        @Override
        protected StateMachine createFSM() {
            // Define possible states the enemy can be in
            State searchAllyState = new State(new Action[]{ Action.SEARCH_ALLY }, null);
            State attackAllyState = new State(new Action[]{ Action.ATTACK_ALLY }, null);

            // Set up entry/exit actions
            searchAllyState.setEntryActions(new Action[]{ Action.SET_SEARCH });
            searchAllyState.setExitActions(new Action[]{ Action.RESET_SEARCH });

            // Define all conditions required to change any state
            nearToAllyCondition = new IntCondition(0, 40);

            // Define all state transitions that could happen
            Transition attackAllyPossibility = new Transition(attackAllyState, nearToAllyCondition);

            // Define how the states can transit from one another
            searchAllyState.setTransitions(new Transition[]{ attackAllyPossibility });
            attackAllyState.setTransitions(new Transition[]{ });

            return new StateMachine(searchAllyState);
        }

        @Override
        public void update() {
            double distanceToAlly = getCenterPosition().distance(services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
            nearToAllyCondition.setTestValue((int) distanceToAlly);

            Action[] actions = stateMachine.update();
            Arrays.stream(actions).forEach(action -> {
                switch (action) {
                    case SEARCH_ALLY:
                        search(ItemType.ALLY);
                        break;
                    case ATTACK_ALLY:
                        destroy();
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
        protected void destroy() {
            exists = false;
            entityManager.removeEntity(this);
            Trigger explosion = new RingExplosion(getCenterPosition(), 50, side);
            services.addTrigger(explosion);
        }

        @Override
        public Circle getHitBox() {
            return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
        }

        @Override
        public String toString() {
            return "Mini Splitter";
        }
    }
}

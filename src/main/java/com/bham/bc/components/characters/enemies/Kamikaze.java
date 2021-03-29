package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.SIDE;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.*;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
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
    // Constant
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/kamikaze.png";
    public static final int SIZE = 30;

    // Configurable
    public static final int MAX_HP = 100;
    public static final double SPEED = 1;

    private int timeout = 100; //temp

    private final StateMachine stateMachine;
    private FreePathCondition noObstaclesCondition;
    private IntCondition closeRadiusCondition;
    private AndCondition chargeCondition;
    private IntCondition attackCondition;

    /**
     * Constructs a kamikaze type enemy
     *
     * @param x top left x coordinate of the character
     * @param y top left y coordinate of the character
     */
    public Kamikaze(double x, double y) {
        super(x, y, SPEED, MAX_HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        stateMachine = createFSM();
    }

    @Override
    public void destroy() {
        exists = false;
        entityManager.removeEntity(this);
        Trigger explosion = new ExplosionTrigger(getCenterPosition(), 50, side);
        backendServices.addTrigger(explosion);
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State searchState = new State(new Action[]{ Action.MOVE }, null);
        State chargeState = new State(new Action[]{ Action.CHARGE }, null);
        State attackState = new State(new Action[]{ Action.AIMANDSHOOT }, null);

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
    public Shape getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
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
                case MOVE:
                    if(--timeout == 0) {
                        navigate(backendServices.getPlayerCenterPosition());
                        timeout = 100;
                    }
                    if(timeout%10 == 0) System.out.println(timeout);

                    moveTowardsTarget();
                    break;
                case CHARGE:
                    charge();
                    break;
                case AIMANDSHOOT:
                    destroy();
                    break;
            }
        });
    }

    /**
     * Represents Kamikaze's explosion effect. This is created as a trigger because it affects player's HP
     */
    private static class ExplosionTrigger extends Trigger {

        public static final int SIZE = 60;
        private int currentFrame;
        private double damage;
        private SIDE side;

        /**
         * Constructs explosion at a given location
         * @param centerPosition x and y coordinates of the trigger image
         * @param damage amount of damage that will be dealt to specific side
         * @param side ALLY or ENEMY side trigger belongs to
         */
        public ExplosionTrigger(Point2D centerPosition, double damage, SIDE side) {
            super(BaseGameEntity.GetNextValidID(), (int) (centerPosition.getX() - SIZE/2), (int) (centerPosition.getY() - SIZE/2));
            this.damage = damage;
            this.side = side;
            currentFrame = 0;
            initImages();
        }

        /**
         * Initializes all the images for the explosion
         */
        private void initImages() {
            String baseUrl = "file:src/main/resources/img/characters/effects/blueRingExplosion";
            entityImages = new Image[19];

            for(int i = 1; i <= 19; i++) {
                String url = baseUrl + i + ".png";
                entityImages[i-1] = new Image(url, SIZE, SIZE, false, false);
            }
        }

        //TODO: Adjust size according to currentFrame
        @Override
        public Shape getHitBox() {
            return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0);
        }

        @Override
        public void render(GraphicsContext gc) {
            gc.drawImage(entityImages[currentFrame++ % 19], x, y);
        }

        @Override
        public void tryTriggerC(GameCharacter character) {
            if(intersects(character) && character.getSide() != side && isActive()) {
                setInactive();
                character.changeHP(-damage);
            }
        }

        @Override
        public void tryTriggerO(GenericObstacle entity) {

        }

        @Override
        public void update() {
            if(currentFrame == 19) setToBeRemovedFromGame();
        }

        @Override
        public boolean handleMessage(Telegram msg) {
            return false;
        }

        @Override
        public String toString() {
            return "Kamikaze's explosion";
        }

        @Override
        public ItemType getItemType() {
            return null;
        }
    }
}

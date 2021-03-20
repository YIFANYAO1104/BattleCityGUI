package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.characters.SIDE;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.*;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import com.bham.bc.components.characters.Character;

import java.util.Arrays;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Represents enemy type which self-destructs near player to deal huge amount of damage
 */
public class Kamikaze extends Enemy {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy3.png";
    public static final int SIZE = 30;
    public static final int MAX_HP = 100;
    public static final double SPEED = 1;

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
        Trigger explosion = new ExplosionTrigger(getCenterPosition().getX(), getCenterPosition().getY(), 50);
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
        attackCondition = new IntCondition(0, 45);

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
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0);
    }

    @Override
    // TODO: remove
    public Shape getLine() { return noObstaclesCondition.getPath(); }

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
                    //move();sd
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
    private static class ExplosionTrigger extends Trigger<Character> {

        public static final int SIZE = 60;
        private int currentFrame;
        private double damage;

        /**
         * Constructs explosion at a given location
         * @param centerX center x coordinate of the trigger image
         * @param centerY center y coordinate of the trigger image
         */
        public ExplosionTrigger(double centerX, double centerY, double damage) {
            super(BaseGameEntity.GetNextValidID(), (int) (centerX - SIZE/2), (int) (centerY - SIZE/2));
            this.damage = damage;
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
        public void tryTrigger(Character character) {
            if(intersects(character) && character.getSide() == SIDE.ALLY && isActive()) {
                setInactive();
                character.addHP(-damage);
            }
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
    }
}

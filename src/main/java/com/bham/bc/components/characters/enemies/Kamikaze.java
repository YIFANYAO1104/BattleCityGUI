package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.*;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Represents enemy type which self-destructs near player to deal huge amount of damage
 */
public class Kamikaze extends Enemy {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy3.png";
    public static final int SIZE = 30;
    public static final int MAX_HP = 100;
    public static final double SPEED = 1;

    //private final StateMachine stateMachine;

    private FreePathCondition freePathCondition;
    private IntCondition chargeRangeCondition;
    private IntCondition attackRangeCondition;

    /**
     * Constructs a kamikaze type enemy
     *
     * @param x     top left x coordinate of the character
     * @param y     top left y coordinate of the character
     */
    public Kamikaze(double x, double y) {
        super(x, y, SPEED, MAX_HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        //stateMachine = createFSM();
        createFSM();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State approachState = new State(new Action[]{ Action.MOVE }, null);
        State attackState = new State(new Action[]{ Action.AIMANDSHOOT }, null);

        // Define all conditions required for to change any state
        chargeRangeCondition = new IntCondition(0, 200);
        freePathCondition = new FreePathCondition();         // Pass this condition as second because it is more expensive
        attackRangeCondition = new IntCondition(0, 10);

        //Transition detectChargeOption = new AndCondition();
        return null;
    }

    @Override
    public Shape getHitBox() { return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0); }

    @Override
    public Shape getLine() { return freePathCondition.getPath(); }

    @Override
    public void update() {
        freePathCondition.setTestValues(getCenterPosition(), backendServices.getPlayerCenterPosition());
        freePathCondition.test();
    }
}

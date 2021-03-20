package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.characters.SIDE;
import com.bham.bc.entity.ai.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Represents enemy type which self-destructs near player to deal huge amount of damage
 */
public class Kamikaze extends Enemy {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/enemy3.png";
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    public static final int MAX_HP = 100;
    public static final double SPEED = 1;

    //private final StateMachine stateMachine;

    private CollideCondition freePathCondition;
    private IntCondition chargeRangeCondition;
    private IntCondition attackRangeCondition;

    private int pid;

    /**
     * Constructs a kamikaze type enemy
     *
     * @param x     top left x coordinate of the character
     * @param y     top left y coordinate of the character
     */
    public Kamikaze(double x, double y, int playerID) {
        super(x, y, SPEED, MAX_HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
        //stateMachine = createFSM();
        pid = playerID;
        createFSM();
    }

    @Override
    protected StateMachine createFSM() {
        // Define possible states the enemy can be in
        State approachState = new State(new Action[]{ Action.MOVE }, null);
        State attackState = new State(new Action[]{ Action.AIMANDSHOOT }, null);

        // Define all conditions required for to change any state
        chargeRangeCondition = new IntCondition(0, 200);
        freePathCondition = new CollideCondition(getID(), pid);         // Pass this condition as second because it is more expensive
        attackRangeCondition = new IntCondition(0, 10);

        //Transition detectChargeOption = new AndCondition();
        return null;
    }

    @Override
    public Shape getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, WIDTH, HEIGHT);
        hitBox.getTransforms().add(new Rotate(angle, x + WIDTH/2,y + HEIGHT/2));

        return hitBox;
    }

    @Override
    public Shape getLine() { return freePathCondition.getLine();}

    @Override
    public void update() {
        freePathCondition.setTestValues(getCenterPosition(), backendServices.getPlayerCenterPosition());
        freePathCondition.test();
    }
}

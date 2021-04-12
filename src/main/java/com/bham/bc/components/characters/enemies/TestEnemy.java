package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.behavior.StateMachine;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

public class TestEnemy extends Enemy {
    // Constant parameters
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/splitter.png";
    public static final int SIZE = 30;

    // Configurable
    public static final double HP = 20;
    public static final double SPEED = 5;
    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x     top left x coordinate of the enemy
     * @param y     top left y coordinate of the enemy
     */
    public TestEnemy(double x, double y) {
        super(x, y, SPEED, HP);
        velocity = new Point2D(-1, 0);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
    }

    @Override
    protected void destroy() {
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public Circle getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }

    @Override
    protected StateMachine createFSM() {
        return null;
    }

    @Override
    public String toString() {
        return "Test Enemy";
    }
}

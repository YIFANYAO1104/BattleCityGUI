package com.bham.bc.components.characters.agents.enemies;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Side;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class EnemyTestDemo extends GameCharacter {
    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/tank.png";
    public static final int SIZE = 30;
    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x     top left x coordinate of the enemy
     * @param y     top left y coordinate of the enemy
     */
    public EnemyTestDemo(double x, double y) {
        super(x, y, 10, 100, Side.ENEMY);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };

    }

    @Override
    protected void destroy() {

    }

    @Override
    public List<Shape> getSmoothingBoxes() {
        return null;
    }

    @Override
    public Circle getHitBox() {
        return  new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .5);
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "TestDemo";
    }


    @Override
    public void move() {

    }
}

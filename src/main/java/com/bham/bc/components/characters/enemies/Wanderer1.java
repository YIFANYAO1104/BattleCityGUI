package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.environment.navigation.impl.PathEdge;
import com.bham.bc.entity.ai.StateMachine;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.LinkedList;

import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.GeometryEnhanced.isZero;


public class Wanderer1 extends Enemy {

    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/shooter.png";
    public static final int SIZE = 30;
    public static final double HP = 100;
    public static final double SPEED = 3;

    private LinkedList<PathEdge> pathEdges;
    private Point2D destination;




    /**
     * Constructs an enemy instance with initial speed value set to 1
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Wanderer1(int x, int y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
    }

    @Override
    protected StateMachine createFSM(){
        return null;
    }

    @Override
    public void update() {
        sb.wanderOn();

        mymove();

    }

    public void mymove() {
        //TODO:Move to steering.calculate
        Point2D force = sb.calculate();
        Point2D acceleration = force.multiply(1./3);
        this.acceleration = acceleration;
        velocity = velocity.add(acceleration);
        if(velocity.magnitude()> maxSpeed){
            velocity = velocity.normalize().multiply(maxSpeed);
        }
        if (!isZero(velocity)) {
            heading = velocity.normalize();
        }

        x += velocity.getX();
        y += velocity.getY();
    }

    @Override
    public void destroy() {
        entityManager.removeEntity(this);
        exists = false;
    }

    @Override
    public Shape getHitBox() {
        return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE * .4);
    }

    @Override
    public String toString() { return "Default Enemy"; }

    @Override
    public void render(GraphicsContext gc) {
        drawRotatedImage(gc, entityImages[0], getAntiAngleY());

        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+velocity.getX()*10, y+velocity.getY()*10);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+acceleration.getX(),x+acceleration.getY() );


    }
}

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


public class Wanderer extends Enemy {

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
    public Wanderer(int x, int y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
    }

    @Override
    protected StateMachine createFSM(){
        return null;
    }

    @Override
    public void update() {
        System.out.println("curPos"+getPosition());

        //arrive
        System.out.println("---------------------------------------------------");
        System.out.println("seek"+destination);
        Point2D force = sb.wander_improved();
        System.out.println("force" + force);
        Point2D acceleration = force.multiply(1./3);
        this.acceleration = acceleration;
        System.out.println("v" + velocity);
        velocity = velocity.add(acceleration);
        System.out.println("---------------------------------------------------");
//            System.out.println("EnemyV"+velocity);
//
//            System.out.println("EnemyA" + acceleration);
        //Truncate
        if(velocity.magnitude()>speed){
            velocity = velocity.normalize().multiply(speed);
        }
        if (!isZero(velocity)) {
            heading = velocity.normalize();
        }

        move();

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
        drawRotatedImage(gc, entityImages[0], getAngle());

        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+velocity.getX()*10, y+velocity.getY()*10);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+acceleration.getX(),x+acceleration.getY() );


    }
}

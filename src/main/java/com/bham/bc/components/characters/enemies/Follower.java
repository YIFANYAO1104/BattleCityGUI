package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.environment.navigation.impl.PathEdge;
import com.bham.bc.entity.ai.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.LinkedList;

import static com.bham.bc.entity.EntityManager.entityManager;


public class Follower extends Enemy {

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
    public Follower(int x, int y) {
        super(x, y, SPEED, HP);
        entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
        navigationService.createRequest(new Point2D(16*32, 16*32));
        navigationService.peekRequestStatus();
//        pathEdges = navigationService.getPath();
        pathEdges = new LinkedList<>();
        pathEdges.add(new PathEdge(new Point2D(16*26, 16*26),new Point2D(20*26, 16*26)));
        pathEdges.add(new PathEdge(new Point2D(20*26, 16*26),new Point2D(16*26, 18*26)));
        pathEdges.add(new PathEdge(new Point2D(16*26, 18*26),new Point2D(17 *26, 19*26)));
        pathEdges.add(new PathEdge(new Point2D(17 *26, 19*26),new Point2D(16*26, 20*26)));
        pathEdges.add(new PathEdge(new Point2D(16*26, 20*26),new Point2D(16*26, 21*26)));
        destination = pathEdges.removeFirst().getDestination();
    }

    @Override
    protected StateMachine createFSM(){
        return null;
    }

    @Override
    public void update() {
//        System.out.println("curPos"+getPosition());
        if(intersectsShape(new Circle(destination.getX(), destination.getY(), 1))) {
            if(!pathEdges.isEmpty()) {
                System.out.println("new des");
                destination = pathEdges.removeFirst().getDestination();
//                face(destination);
//                move();
            }
        }

        if (pathEdges.isEmpty()) { //last element
            //arrive
//            System.out.println("---------------------------------------------------");
            Point2D force = sb.arrive_improved(destination);
            System.out.println("force="+force);
            Point2D acceleration = force.multiply(1./3);
            this.acceleration = acceleration;
            velocity = velocity.add(acceleration);
//            System.out.println("---------------------------------------------------");
        } else {
            //seek
//            System.out.println("---------------------------------------------------");
//            System.out.println("seek"+destination);
            Point2D force = sb.seek(destination);
            System.out.println("force="+force);
            Point2D acceleration = force.multiply(1./3);
            this.acceleration = acceleration;
            velocity = velocity.add(acceleration);
//            System.out.println("---------------------------------------------------");

        }
        if(velocity.magnitude()>speed){
            velocity = velocity.normalize().multiply(speed);
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
        for (PathEdge graphEdge : pathEdges) {
            Point2D n1 = graphEdge.getSource();
            Point2D n2 = graphEdge.getDestination();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2.0);
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
        }
        drawRotatedImage(gc, entityImages[0], getAntiAngleY());

        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+velocity.getX()*10, y+velocity.getY()*10);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+acceleration.getX()*10,x+acceleration.getY()*10 );


    }
}

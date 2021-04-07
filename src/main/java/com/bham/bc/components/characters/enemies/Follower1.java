package com.bham.bc.components.characters.enemies;

import com.bham.bc.entity.ai.navigation.impl.PathEdge;
import com.bham.bc.entity.ai.behavior.StateMachine;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.LinkedList;

import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.GeometryEnhanced.isZero;


public class Follower1 extends Enemy {

    public static final String IMAGE_PATH = "file:src/main/resources/img/characters/shooter.png";
    public static final int SIZE = 30;
    public static final double HP = 100;
    public static final double SPEED = 3;

    private LinkedList<PathEdge> pathEdges;
    private Point2D destination;


    boolean isLast = false;
    boolean exit = false;


    /**
     * Constructs an enemy instance with initial speed value set to 1
     *
     * @param x top left x coordinate of the enemy
     * @param y top left y coordinate of the enemy
     */
    public Follower1(int x, int y) {
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
        steering.setTarget(destination);
    }

    @Override
    protected StateMachine createFSM(){
        return null;
    }

    @Override
    public void update() {
        if(intersects(new Circle(destination.getX(), destination.getY(), 1))) {
            if(!pathEdges.isEmpty()) {
                System.out.println("new des");
                destination = pathEdges.removeFirst().getDestination();
                steering.setTarget(destination);
            } else {
                steering.arriveOff();
//                velocity = new Point2D(0,0);
                mymove();
                return;
            }
        }

        if (pathEdges.isEmpty()) { //last element
            //arrive
            steering.arriveOn();
        } else {
            //seek
            steering.seekOn();
        }

        mymove();
    }


    public void mymove() {
        //TODO:Move to steering.calculate
        Point2D force = steering.calculate();
        System.out.println("force="+force);
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
        for (PathEdge graphEdge : pathEdges) {
            Point2D n1 = graphEdge.getSource();
            Point2D n2 = graphEdge.getDestination();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2.0);
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
        }
        drawRotatedImage(gc, entityImages[0], getAngle());

        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+velocity.getX()*10, y+velocity.getY()*10);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);

        gc.strokeLine(x, y, x+acceleration.getX()*10,x+acceleration.getY()*10 );


    }
}

package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.Gun;
import com.bham.bc.components.characters.Side;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.NavigationService;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.ai.navigation.PathEdge;
import com.bham.bc.entity.ai.navigation.impl.PathPlanner;
import com.bham.bc.entity.ai.behavior.StateMachine;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.utils.GeometryEnhanced;
import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.LinkedList;
import java.util.List;

import static com.bham.bc.components.CenterController.services;
import static com.bham.bc.utils.GeometryEnhanced.isZero;

/**
 * <h1>Generic enemy bot</h1>
 *
 * <p>This class defines all the common behavior an enemy character can show. All the method calls are
 * determined by the state machine each enemy has.</p>
 */
public abstract class Enemy extends GameCharacter {

    protected final Gun GUN;
    protected NavigationService navigationService;
    protected LinkedList<PathEdge> pathEdges;

    private Point2D destination;
    private int timeTillSearch;
    private int edgeBehavior;
    private boolean isAiming;

    /**
     * Constructs a character instance with directionSet initialized to empty
     *
     * @param x     top left x coordinate of the enemy
     * @param y     top left y coordinate of the enemy
     * @param speed value which defines the initial velocity
     * @param hp    health points the enemy should have
     */
    protected Enemy(double x, double y, double speed, double hp) {
        super(x, y, speed, hp, Side.ENEMY);
        navigationService = new PathPlanner(this, services.getGraph());
        pathEdges = new LinkedList<>();
        destination = new Point2D(0, 0);
        timeTillSearch = 20;
        edgeBehavior = GraphEdge.normal;
        GUN = new Gun(this, BulletType.DEFAULT,null);
        isAiming = false;
    }

    /**
     * Finds path to a specified item type if it has not already been found
     * @param itemType type of item that should be navigated
     */
    protected void navigate(ItemType itemType) {
        // If we don't have any points to follow, we need to navigate (it may return 0 edges if it's close)
        // Otherwise, if we have points to follow, we still need to update the search if the target is dynamic
        if(pathEdges.isEmpty()) {
            if (navigationService.peekRequestStatus() != SearchStatus.search_incomplete) {
                switch (itemType) {
                    case HEALTH:
                        navigationService.createRequest(ItemType.HEALTH);
                        break;
                    case HOME:
                        navigationService.createRequest(services.getClosestCenter(getCenterPosition(), ItemType.HOME));
                        break;
                    case ENEMY_AREA:
                        navigationService.createRequest(services.getClosestCenter(getCenterPosition(), ItemType.ENEMY_AREA));
                        break;
                    case ALLY:
                        navigationService.createRequest(services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
                        break;
                }
            }
        } else if(itemType == ItemType.ALLY && (--timeTillSearch <= 0)) {
            navigationService.createRequest(services.getClosestCenter(getCenterPosition(), ItemType.ALLY));
        }

        //-----test
        if(itemType == ItemType.ALLY && timeTillSearch % 5 == 0) System.out.println("Time left till new navigation request for ally: " + timeTillSearch);
        //---------

        // Due to checks on each frame of whether the search is complete or not we always need get the list of points if it is empty
        // If the search status is completed, we fill our list with points to follow (if it is empty or we need to update the search for dynamic target)
        if((pathEdges.isEmpty() || (itemType == ItemType.ALLY && timeTillSearch <= 0)) && navigationService.peekRequestStatus() == SearchStatus.target_found) {
            pathEdges = navigationService.getPath();

            // If the target is very close we might have no path edges to follow
            // Otherwise if we have path edges, do not remove the last edge to keep the list not empty for proper search() functionality
            destination = pathEdges.isEmpty() ? getCenterPosition() : pathEdges.getFirst().getDestination();
            steering.setTarget(destination);

            timeTillSearch = 20;
            navigationService.resetTaskStatus();
        }
    }

    /**
     * Navigates to a specific item type (if needed) and moves towards that direction
     * @param itemType type of item that should be searched
     */
    protected void search(ItemType itemType) {
        navigate(itemType);

        // Checks if the enemy intersects the point in the list of path edges and gets the next target point if so
        if(intersects(new Circle(destination.getX(), destination.getY(), 3))) {
            if(!pathEdges.isEmpty()) {
                PathEdge nextEdge = pathEdges.removeFirst();
                edgeBehavior = nextEdge.getBehavior();
                destination = nextEdge.getDestination();
                steering.setTarget(destination);
            }
        }

        // If the list of pathEdges is not empty we need to move forward
//        if(!pathEdges.isEmpty()) {
//            //steering.seekOn();
//            steering.arriveOn();
//            move();
//        }
        steering.seekOn();
    }

    /**
     * Changes angle to align with a specified point
     *
     * @param toward position to face
     */
    protected void face(Point2D toward) {
        Point2D direction = toward.subtract(getCenterPosition());
        if (!GeometryEnhanced.isZero(direction)){
            heading = direction.normalize();
        }
        isAiming = true;
    }

    /**
     * Changes angle to align with a specified closest item type
     *
     * <p><b>Note:</b> if some ally target was followed and found but there is another ally standing closer but behind an
     * obstacle, this method will make the enemy aim at that ally. So free path condition must be checked. Alternatively,
     * in <i>getClosestCenter()</i> only those distances could be filtered if they don't intersect any obstacles.</p>
     *
     * @param itemType object to face
     */
    protected void face(ItemType itemType) {
        face(services.getClosestCenter(getCenterPosition(), itemType));
    }

    /**
     * Shoots bullet(-s) straight or at specified angles with a random probability
     * @param threshold value between 0 and 1 above which the shoot() method would be run
     * @param angles    degrees at which multiple bullets should be fired
     */
    protected void shoot(double threshold, double ...angles) {
        if(Math.random() > threshold) GUN.shoot(angles.length == 0 ? new double[]{0} : angles);
    }

    /**
     * Shoots a bullet at an obstacle from a distance of at max 5 nodes to the obstacle
     * @return true if it is needed to shoot to at an obstacle and false otherwise
     */
    protected boolean shootObstacle() {
        int numNodesDistance = 4;
        boolean canShoot = edgeBehavior == GraphEdge.shoot || pathEdges.stream().limit(numNodesDistance).anyMatch(edge -> edge.getBehavior() == GraphEdge.shoot);

        if(canShoot) {
            face(ItemType.SOFT);
            GUN.shoot();
        }
        return canShoot;
    }

    /**
     * In this behaviour the AI will try to take over the home base
     */
    protected void takeOver() {
        services.occupyHome(this);
    }

    /**
     * Abstract method which all child classes must fill with their own unique Finite State Machine
     * @return the StateMachine for that specific enemy
     */
    protected abstract StateMachine createFSM();

    @Override
    public List<Shape> getSmoothingBoxes(){
        return navigationService.getSmoothingBoxes();
    }

    @Override
    public void render(GraphicsContext gc) {
//        if (navigationService!=null) navigationService.render(gc);
        drawRotatedImage(gc, entityImages[0], getAngle());

//        gc.setStroke(Color.GOLD);
//        gc.setLineWidth(2.0);
//
//        gc.strokeLine(x, y, x+velocity.getX()*10, y+velocity.getY()*10);
//
//        gc.setStroke(Color.WHITE);
//        gc.setLineWidth(2.0);
//
//        gc.strokeLine(x, y, x+acceleration.getX()*10,x+acceleration.getY()*10 );
//
//        gc.setFill(Color.WHITE);
//        gc.fillRoundRect(destination.getX(),destination.getY(),4,4,1,1);
    }

    @Override
    public void move() {
        Point2D force = steering.calculate();
        Point2D acceleration = force.multiply(1. / mass);
        //debug
        this.acceleration = acceleration;

        velocity = velocity.add(acceleration);
        if (velocity.magnitude() > maxSpeed) {
            velocity = velocity.normalize().multiply(maxSpeed);
        }

        if (!isZero(velocity) && !isAiming) {
            heading = velocity.normalize();
        }

        x += velocity.getX();
        y += velocity.getY();

        isAiming = false;
    }

    @Override
    public String toString() {
        return "Enemy";
    }
}

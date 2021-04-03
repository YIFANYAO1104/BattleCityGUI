package com.bham.bc.entity.ai.behavior;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * <p>Condition for checking whether a straight path (represented as a rectangle) between 2 points
 * is free. It tests whether the path intersects any obstacles in the game map but it doesn't
 * check for any character or bullet collision.</p>
 */
public class FreePathCondition implements Condition {
    private Rectangle path;

    /**
     * Constructs a condition by representing its path property as a rectangle
     * <br>TODO: crate a constructor which takes 2 points/ check if testing on empty rectangle doesn't throw an error
     */
    public FreePathCondition() { path = new Rectangle(); }

    /**
     * Gets the path for rendering
     * <br>TODO: remove this
     * @return Rectangle as a path
     */
    public Shape getPath() {
        return path;
    }

    /**
     * Sets 2 points as testing values for the <i>test()</i> method. The path will be checked between them.
     *
     * <p>This method updates this condition's <b>path</b> property. It calculates the angle and the distance
     * between them, creates a rectangle of the same length as the distance and rotates it appropriately to
     * connect the 2 points. The height of the rectangle is 1 so path's width is not considered, however this
     * is enough to satisfy the behavior of the AI character.</p>
     *
     * <br>TODO: Optimise this by putting all the calculations in the test method. So that they are done only during testing.
     *
     * @param pt1 coordinate of the first entity
     * @param pt2 coordinate of the second entity
     */
    public void setTestValues(Point2D pt1, Point2D pt2) {
        double angle = Math.atan2(pt2.getY() - pt1.getY(), pt2.getX() - pt1.getX());
        Rectangle hitbox = new Rectangle(pt1.getX(), pt1.getY(), pt1.distance(pt2), 1);
        hitbox.getTransforms().add(new Rotate(Math.toDegrees(angle), pt1.getX(), pt1.getY()));

        path = hitbox;
    }

    /**
     * Tests for the path to not contain any obstacles in its area
     * @return true if there are no obstacles in a way and false otherwise
     */
    @Override
    public boolean test() { return !backendServices.intersectsObstacles(path); }
}

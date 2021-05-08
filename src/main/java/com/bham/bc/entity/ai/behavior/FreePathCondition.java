package com.bham.bc.entity.ai.behavior;

import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import static com.bham.bc.components.Controller.services;

/**
 * <p>Condition for checking whether a straight path (represented as a rectangle) between 2 points
 * is free. It tests whether the path intersects any obstacles in the game map but it doesn't
 * check for any character or bullet collision.</p>
 */
public class FreePathCondition implements Condition {
    /**
     * source point a path starts from
     */
    private Point2D start;
    /**
     * destination point a path ends at
     */
    private Point2D end;
    /**
     * radius of an entity that has to fit through the path (i.e., half of the width of the path)
     */
    private double radius;

    /**
     * Constructs a condition with placeholder points and a radius of 1 representing the path as a line
     */
    public FreePathCondition() {
        start = new Point2D(0, 0);
        end = new Point2D(0, 0);
        radius = 1;
    }

    /**
     * Constructs a condition with placeholder points and a given radius
     */
    public FreePathCondition(double radius) {
        start = new Point2D(0, 0);
        end = new Point2D(0, 0);
        this.radius = radius;
    }

    /**
     * Constructs a condition with 2 provided points and setting radius to 1 (as if it was a line)
     * @param start source point a path starts from
     * @param end   destination point a path ends at
     */
    public FreePathCondition(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
        radius = 1;
    }

    /**
     * Constructs a condition with 2 provided points and a given radius
     * @param start  source point a path starts from
     * @param end    destination point a path ends at
     * @param radius radius of an entity that has to fit through the path (i.e., half of the width of the path)
     */
    public FreePathCondition(Point2D start, Point2D end, double radius) {
        this.start = start;
        this.end = end;
        this.radius = radius;
    }

    /**
     * Sets 2 points as testing values for the <i>getPath()</i> method. The path will be checked between them.
     *
     * @param start source point a path starts from
     * @param end   destination point a path ends at
     */
    public void setTestValues(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Sets 2 points as testing values for the <i>getPath()</i> method. The path will be checked between them.
     *
     * @param start  source point a path starts from
     * @param end    destination point a path ends at
     * @param radius radius of an entity that has to fit through the path (i.e., half of the width of the path)
     */
    public void setTestValues(Point2D start, Point2D end, double radius) {
        this.start = start;
        this.end = end;
        this.radius = radius;
    }

    /**
     * Gets the path from start to end point
     *
     * <p>It calculates the angle and the distance between them, creates a rectangle of the same length as the
     * distance and rotates it appropriately to connect the 2 points. The height of the rectangle is defined by
     * the radius property, i.e., the height is twice the radius size.</p>
     *
     * @return a properly rotated Rectangle object connecting start and end points
     */
    public Rectangle getPath() {
//        double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
//        Rectangle path = new Rectangle(start.getX(), start.getY(), start.distance(end), radius * 2);
//        path.getTransforms().add(new Rotate(Math.toDegrees(angle), start.getX(), start.getY()));
        double rayCastWidth = radius*2;

        double angle = GeometryEnhanced.clockWiseAngle(end.subtract(start),new Point2D(0,1));
        double dis = start.distance(end);

        Point2D center = start.midpoint(end);
        Point2D topLeft = center.subtract(rayCastWidth*0.5,dis/2);
        Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), rayCastWidth, dis);
        hitBox.getTransforms().add(new Rotate(angle, center.getX(),center.getY()));

        return hitBox;
    }

    /**
     * Renders the calculated path red if there are obstacles in it and green otherwise
     * @param gc graphics context on which the path will be drawn
     */
    public void render(GraphicsContext gc) {
        gc.setStroke(test() ? Color.GREEN : Color.RED);
        gc.setLineWidth(1);
        gc.strokeRect(getPath().getX(), getPath().getY(), getPath().getWidth(), getPath().getHeight());
    }

    /**
     * Tests for the path to not contain any obstacles in its area
     * @return true if there are no obstacles in a way and false otherwise
     */
    @Override
    public boolean test() {
        return !services.intersectsObstacles(getPath());
    }
}

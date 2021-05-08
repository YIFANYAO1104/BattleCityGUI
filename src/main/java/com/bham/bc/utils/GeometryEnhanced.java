package com.bham.bc.utils;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import static com.bham.bc.utils.RandomEnhanced.rand;

/**
 * a util class based on Point2D
 */
public class GeometryEnhanced {

    /**
     * epsilon, when a numember is smaller than this, it's zero
     */
    static public final double eps = 1E-8;

    /**
     * check if a vector is zero vector
     * @param p the vector to be checked
     * @return true if it is a zero vector
     */
    static public boolean isZero(Point2D p) {
        return p.dotProduct(p) < eps;
    }

//    //Ensure the magnitude of a vector is less than speed
//    static public Point2D truncate(Point2D velocity, double speed){
//        if(velocity.magnitude()>speed){
//            return velocity.normalize().multiply(speed);
//        }
//        return velocity;
//    }

    /**
     * Rotates a given point (vector) by some angle
     * @param point a vector to be rotated
     * @param angle angle in radians by which the point is rotated
     * @return a rotated Point2D object
     */
    public static Point2D rotate(Point2D point, double angle) {
        double x = point.getX() * Math.cos(Math.toRadians(angle)) - point.getY() * Math.sin(Math.toRadians(angle));
        double y = point.getX() * Math.sin(Math.toRadians(angle)) + point.getY() * Math.cos(Math.toRadians(angle));

        return new Point2D(x, y);
    }

    /**
     * Rotates a given point (vector) by some angle
     * @param center the center position
     * @param p the point going to be rotated
     * @param antiDegrees the anti-clock wise degree
     * @return a rotated Point2D object
     */
    static public Point2D rotate(Point2D center, Point2D p, double antiDegrees){
        Point2D x = p.subtract(center);
        //rotate linear transformation
        Point2D Tx = new Point2D(Math.cos(Math.toRadians(antiDegrees)),Math.sin(Math.toRadians(antiDegrees)));
        Point2D Ty = new Point2D(-1*Math.sin(Math.toRadians(antiDegrees)),Math.cos(Math.toRadians(antiDegrees)));
        Point2D b = Tx.multiply(x.getX()).add(Ty.multiply(x.getY()));
        return b.add(center);
    }

    /**
     * calculates an angle between 2 vectors
     * @param a vector1
     * @param b vector2
     * @return the clock-wise angle from vector1 to vector2, value from 0 to 360
     */
    static public double clockWiseAngle(Point2D a, Point2D b){
        Point3D c = a.crossProduct(b);
        if (c.getZ() < 0) { //b is on the right of a
            return a.angle(b);
        }
        return 360-a.angle(b);
    }

    /**
     * calculates an angle between 2 vectors
     * @param a vector1
     * @param b vector2
     * @return the anti clock-wise angle from vector1 to vector2, value from 0 to 360
     */
    static public double antiClockWiseAngle(Point2D a, Point2D b){
        Point3D c = a.crossProduct(b);
        if (c.getZ() > 0) { //b is on the right of a
            return a.angle(b);
        }
        return 360-a.angle(b);
    }


    /**
     * transfer a global position to local position
     * @param globalPos the position in global space
     * @param agentDirection the x axis of local space, non-zero vector
     * @param agentPos the origin of local space
     * @return global position
     */
    public static Point2D globalToLocal(Point2D globalPos,
                                        Point2D agentDirection,
                                        Point2D agentPos) {
        assert (!isZero(agentDirection)) : "<GeometryEnhanced::globalToLocal>: Zero Direction Vector";
        Point2D ex = agentDirection.normalize();
        Point2D ey = GeometryEnhanced.perp(ex);
        double a = ex.getX(),b=ey.getX(),
                c=ex.getY(),d=ey.getY();

        double oneOverDeterminant = 1./(a*d-b*c);
        Point2D inverse1 = new Point2D(d,-c).multiply(oneOverDeterminant);
        Point2D inverse2 = new Point2D(-b,a).multiply(oneOverDeterminant);
        Point2D bV = globalPos.subtract(agentPos);
        return inverse1.multiply(bV.getX()).add(inverse2.multiply(bV.getY()));
    }


    /**
     * transfer a local position to global position
     * @param localPos the position in local space
     * @param agentDirection the x axis of local space, non-zero vector
     * @param agentPos the origin of local space
     * @return global position
     */
    public static Point2D localToGlobal(Point2D localPos,
                                             Point2D agentDirection,
                                             Point2D agentPos) {
        assert (!isZero(agentDirection)) : "<GeometryEnhanced::localToGlobal>: Zero Direction Vector";
        Point2D ex = agentDirection.normalize();
        Point2D ey = GeometryEnhanced.perp(ex);
        return ex.multiply(localPos.getX()).add(ey.multiply(localPos.getY())).add(agentPos);
    }

    /**
     * calculates an orthogonality vector
     * @param p a non-zero vector
     * @return an orthogonality vector to p
     */
    static public Point2D perp(Point2D p) {
        return new Point2D(-p.getY(), p.getX());
    }

    /**
     * generate a random position in a circle
     * @param centerPos the center of a circle
     * @param radius the radius of a circle
     * @return a position in the circle
     */
    public static Point2D randomPointInCircle(Point2D centerPos, double radius) {
        Double theta = 2*Math.PI*rand.nextDouble();
        Double r = Math.sqrt(rand.nextDouble())*radius;
        Point2D target = new Point2D(Math.cos(theta),Math.sin(theta))
                .multiply(r)
                .add(centerPos);
        return target;
    }

    /**
     * generate a random position in a circle
     * @param circle the area which bounds the point
     * @return a position in the circle
     */
    public static Point2D randomPointInCircle(Circle circle) {
        return randomPointInCircle(new Point2D(circle.getCenterX(), circle.getCenterY()), circle.getRadius());
    }

    /**
     * ensure that a position is within a range
     * @param pos the position to be bounded
     * @param size the width and height of an agent
     * @param spaceWidth the width of bounding space
     * @param spaceHeight the height of bounding space
     * @return a bounded position
     */
    public static Point2D bound(Point2D pos,Point2D size, int spaceWidth, int spaceHeight) {
        double x = pos.getX();
        double y = pos.getY();
        if (x+size.getX() > spaceWidth) {
            x = spaceWidth-size.getX();
        }

        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }

        if (y+size.getY() > spaceHeight) {
            y = spaceHeight-size.getY();
        }
        return new Point2D(x,y);
    }

    /**
     * for debug, render the hix box of an entity
     * @param gc the panel to paint
     * @param box the hit box going to be rendered
     */
    public static void renderHitBox(GraphicsContext gc, Shape box) {
        if (box instanceof Rectangle){
            Rectangle rect = (Rectangle)box;
            Point2D p1 = new Point2D(rect.getX(), rect.getY());
            Point2D p2 = new Point2D(rect.getX() + rect.getWidth(), rect.getY());
            Point2D p3 = new Point2D(rect.getX(), rect.getY() + rect.getHeight());
            Point2D p4 = new Point2D(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());

            p1 = box.localToParent(p1);
            p2 = box.localToParent(p2);
            p3 = box.localToParent(p3);
            p4 = box.localToParent(p4);


            gc.setStroke(Color.RED);
            gc.setLineWidth(1.0);
            gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            gc.strokeLine(p1.getX(), p1.getY(), p3.getX(), p3.getY());
            gc.strokeLine(p4.getX(), p4.getY(), p2.getX(), p2.getY());
            gc.strokeLine(p4.getX(), p4.getY(), p3.getX(), p3.getY());
        } else if (box instanceof Circle) {
            Circle hb = (Circle) box;
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            gc.strokeArc(hb.getCenterX() - hb.getRadius(), hb.getCenterY() - hb.getRadius(), hb.getRadius()*2, hb.getRadius()*2, 0, 360, ArcType.OPEN);
        }
        else {
            System.out.println("not supported hitbox type");
        }
    }

    /**
     * for debug, render the bound of a hit box
     * @param gc the panel to paint
     * @param box the hit box
     */
    public static void renderBounds(GraphicsContext gc, Shape box) {
        Bounds b = box.getBoundsInParent();


        Point2D p1 = new Point2D(b.getMinX(), b.getMinY());
        Point2D p2 = new Point2D(b.getMinX() + b.getWidth(), b.getMinY());
        Point2D p3 = new Point2D(b.getMinX(), b.getMinY() + b.getHeight());
        Point2D p4 = new Point2D(b.getMinX() + b.getWidth(), b.getMinY() + b.getHeight());

        gc.setStroke(Color.RED);
        gc.setLineWidth(1.0);
        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        gc.strokeLine(p1.getX(), p1.getY(), p3.getX(), p3.getY());
        gc.strokeLine(p4.getX(), p4.getY(), p2.getX(), p2.getY());
        gc.strokeLine(p4.getX(), p4.getY(), p3.getX(), p3.getY());
    }
}

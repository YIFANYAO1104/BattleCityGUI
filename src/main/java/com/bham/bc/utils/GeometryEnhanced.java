package com.bham.bc.utils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.Random;

public class GeometryEnhanced {

    static public final double eps = 1E-8;

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

    // rotate a point around center point, anti-clockwise angle 15ms in test
    static public Point2D rotate(Point2D center, Point2D p, double antiDegrees){
        Point2D x = p.subtract(center);
        //rotate linear transformation
        Point2D Tx = new Point2D(Math.cos(Math.toRadians(antiDegrees)),Math.sin(Math.toRadians(antiDegrees)));
        Point2D Ty = new Point2D(-1*Math.sin(Math.toRadians(antiDegrees)),Math.cos(Math.toRadians(antiDegrees)));
        Point2D b = Tx.multiply(x.getX()).add(Ty.multiply(x.getY()));
        return b.add(center);
    }

    //return an angle between [0,360]
    //rotate a with return value in clock wise order will match b
    static public double clockWiseAngle(Point2D a, Point2D b){
        Point3D c = a.crossProduct(b);
        if (c.getZ() < 0) { //b is on the right of a
            return a.angle(b);
        }
        return 360-a.angle(b);
    }

    //return an angle between [0,360]
    static public double antiClockWiseAngle(Point2D a, Point2D b){
        Point3D c = a.crossProduct(b);
        if (c.getZ() > 0) { //b is on the right of a
            return a.angle(b);
        }
        return 360-a.angle(b);
    }


    //transfer a local position to global position
    //return global position
    //agentDirection will be normalized
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


    //transfer a local position to global position
    //return global position
    //agentDirection will be normalized
    public static Point2D localToGlobal(Point2D localPos,
                                             Point2D agentDirection,
                                             Point2D agentPos) {
        assert (!isZero(agentDirection)) : "<GeometryEnhanced::localToGlobal>: Zero Direction Vector";
        Point2D ex = agentDirection.normalize();
        Point2D ey = GeometryEnhanced.perp(ex);
        return ex.multiply(localPos.getX()).add(ey.multiply(localPos.getY())).add(agentPos);
    }

    static public Point2D perp(Point2D p) {
        return new Point2D(-p.getY(), p.getX());
    }


    static private Random rand = new Random();


    public static double randDouble(double rangeMin, double rangeMax){
        return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
    }

    //https://stackoverflow.com/questions/5837572/generate-a-random-point-within-a-circle-uniformly
    public static Point2D randomPointInCircle(Point2D centerPos, double radius) {
        Double theta = 2*Math.PI*rand.nextDouble();
        Double r = Math.sqrt(rand.nextDouble())*radius;
        Point2D target = new Point2D(Math.cos(theta),Math.sin(theta))
                .multiply(r)
                .add(centerPos);
        return target;
    }

    public static Point2D randomPointInCircle(Circle circle) {
        return randomPointInCircle(new Point2D(circle.getCenterX(), circle.getCenterY()), circle.getRadius());
    }

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
     * @return true if the target Pos is in the vision
     */
    public static boolean isInVision(Point2D curPos,
                                               Point2D curHeading,
                                               Point2D testPos,
                                               double visionAngle) {
        Point2D toTarget = testPos.subtract(curPos).normalize();
        return curHeading.dotProduct(toTarget) >= Math.cos(visionAngle / 2.0);
    }

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


    //rotate a point around center point, anti-clockwise angle, 31ms in test
//    static public Point2D rotate(Point2D center, Point2D p, double antiDegrees){
//        Rotate r = new Rotate();
//        r.setPivotX(center.getX());
//        r.setPivotY(center.getY());
//        r.setAngle(antiDegrees);
//        return r.transform(p);
//    }
}

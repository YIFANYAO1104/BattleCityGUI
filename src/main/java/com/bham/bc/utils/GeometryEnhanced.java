package com.bham.bc.utils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

public class GeometryEnhanced {

    static public final double eps = 1E-8;

    static public boolean isZero(Point2D p) {
        return p.dotProduct(p) < eps;
    }

    //rotate a point around center point, anti-clockwise angle
    static public Point2D rotate(Point2D center, Point2D p, double antiDegrees){
        Rotate r = new Rotate();
        r.setPivotX(center.getX());
        r.setPivotY(center.getY());
        r.setAngle(antiDegrees);
        return r.transform(p);
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


//    public static Point2D PointToWorldSpace(Point2D point,
//                                             Point2D AgentHeading,
//                                             Point2D AgentSide,
//                                             Point2D AgentPosition) {
//        //make a copy of the point
//        Point2D TransPoint = new Point2D(point);
//
//        //create a transformation matrix
//        C2DMatrix matTransform = new C2DMatrix();
//
//        //rotate
//        matTransform.Rotate(AgentHeading, AgentSide);
//
//        //and translate
//        matTransform.Translate(AgentPosition.x, AgentPosition.y);
//
//        //now transform the vertices
//        matTransform.TransformPoint2Ds(TransPoint);
//
//        return TransPoint;
//    }
}

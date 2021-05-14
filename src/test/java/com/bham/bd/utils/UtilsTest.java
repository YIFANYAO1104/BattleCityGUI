package com.bham.bd.utils;

import javafx.geometry.Point2D;
import org.junit.Test;
import javafx.scene.transform.*;

import static com.bham.bd.utils.GeometryEnhanced.*;
import static org.junit.Assert.assertTrue;


public class UtilsTest {
    @Test
    public void angleExperiment1(){
        Point2D p1 = new Point2D(1,1);
        Point2D p2 = new Point2D(-1,1);
        Point2D p3 = new Point2D(-1,-1);
        Point2D p4 = new Point2D(1,-1);


        Point2D x = new Point2D(1,0);
        System.out.println(p1.angle(x));
        System.out.println(clockWiseAngle(p1,x));
        System.out.println(p2.angle(x));
        System.out.println(clockWiseAngle(p2,x));
        System.out.println(p3.angle(x));
        System.out.println(clockWiseAngle(p3,x));
        System.out.println(p4.angle(x));
        System.out.println(clockWiseAngle(p4,x));
    }

    @Test
    public void angleExperiment2(){
        Point2D p1 = new Point2D(1,1);
        Point2D p2 = new Point2D(-1,1);
        Point2D p3 = new Point2D(-1,-1);
        Point2D p4 = new Point2D(1,-1);

        Point2D x = new Point2D(1,0);
        System.out.println(p1.angle(x));
        System.out.println(antiClockWiseAngle(p1,x));
        System.out.println(p2.angle(x));
        System.out.println(antiClockWiseAngle(p2,x));
        System.out.println(p3.angle(x));
        System.out.println(antiClockWiseAngle(p3,x));
        System.out.println(p4.angle(x));
        System.out.println(antiClockWiseAngle(p4,x));
    }

    @Test
    public void rotateTest1(){
        Point2D p1 = new Point2D(3,3);
        Point2D p2 = new Point2D(6,0);
        Rotate r = new Rotate();
        r.setPivotX(p1.getX());
        r.setPivotY(p1.getY());
        r.setAngle(90);
        Point2D target = r.transform(p2);
        assertTrue(isZero(target.subtract(new Point2D(6,6))));
    }

    @Test
    public void rotateTest2(){
        Point2D p1 = new Point2D(3,3);
        Point2D p2 = new Point2D(6,0);
        Point2D target = GeometryEnhanced.rotate(p1,p2,90);
        assertTrue(isZero(target.subtract(new Point2D(6,6))));
    }

    @Test
    public void localToGlobalTest(){
        Point2D localPos = new Point2D(1,1);
        Point2D direction = new Point2D(8./Math.sqrt(2),8./Math.sqrt(2));
        Point2D agentPos = new Point2D(3,3);
        Point2D globalPos = GeometryEnhanced.localToGlobal(
                localPos,
                direction,
                agentPos
        );
        assertTrue(GeometryEnhanced.isZero(globalPos.subtract(new Point2D(3,3+Math.sqrt(2)))));
    }

    @Test
    public void globalToLocalTest(){
        Point2D globalPos = new Point2D(3,3+Math.sqrt(2));
        Point2D direction = new Point2D(8./Math.sqrt(2),8./Math.sqrt(2));
        Point2D agentPos = new Point2D(3,3);
        Point2D localPos = GeometryEnhanced.globalToLocal(
                globalPos,
                direction,
                agentPos
        );
        System.out.println(localPos);
        System.out.println(localPos.subtract(new Point2D(1,1)));
        assertTrue(GeometryEnhanced.isZero(localPos.subtract(new Point2D(1,1))));
    }

    @Test
    public void randomPointTest(){
        int windowHeight = 800;
        int windowWidth = 800;
        double radius = 300;
        Point2D center = new Point2D(windowWidth/2,windowHeight/2);

        for (int i = 0;i<1000;i++){
            Point2D p = GeometryEnhanced.randomPointInCircle(center,radius);
            assertTrue(p.distance(center)<=radius);
        }
    }
}

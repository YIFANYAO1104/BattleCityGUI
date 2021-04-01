package com.bham.bc;

import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.junit.Test;
import javafx.scene.transform.*;

import static com.bham.bc.utils.GeometryEnhanced.antiClockWiseAngle;
import static com.bham.bc.utils.GeometryEnhanced.clockWiseAngle;
import static org.junit.Assert.assertTrue;


public class TransformTest {
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
    public void angleExperiment3(){
        Point2D p1 = new Point2D(3,3);
        Point2D p2 = new Point2D(6,0);
        Rotate r = new Rotate();
        r.setPivotX(p1.getX());
        r.setPivotY(p1.getY());
        r.setAngle(90);
        System.out.println(r.transform(p2));
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




}

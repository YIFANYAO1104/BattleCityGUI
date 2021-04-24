package com.bham.bc;

import com.bham.bc.components.Controller;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.physics.Steering;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.bham.bc.utils.GeometryEnhanced.isZero;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class SteeringTest {

    private Object[] normalCaseParams()
    {
        new JFXPanel();
        Controller.setMode(MapType.Map1);
        Object[][] typeListList = { //
                { new Point2D(0,0) , new Point2D(1,0) },
                { new Point2D(0,0) , new Point2D(0,1) },
                { new Point2D(0,0) , new Point2D(-1,0)},
                { new Point2D(0,0) , new Point2D(0,-1)},
                { new Point2D(1,0)  , new Point2D(0,0)},
                { new Point2D(0,1)  , new Point2D(0,0)},
                { new Point2D(-1,0) , new Point2D(0,0)},
                { new Point2D(0,-1) , new Point2D(0,0)},
        };
        return typeListList;
    }


    @Test
    @Parameters(method = "normalCaseParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void normalCase(Point2D centerPos,Point2D seekPos){
        Player p = new Player(0,0);
        //top left position
        Point2D pos = centerPos.subtract(p.getSize().multiply(0.5));
        p = new Player(pos.getX(),pos.getY());

        Steering sb = new Steering(p);
        sb.setTarget(seekPos);
        sb.seekOn();
        Point2D force = sb.calculate();
        //check magnitude
        assertEquals(p.getMaxSpeed(), force.magnitude(),1E-8);

        //check direction
        Point2D nforce = force.normalize();
        Point2D direction = seekPos.subtract(centerPos).normalize();
        assertTrue(isZero(nforce.subtract(direction)));
    }


    private Object[] maxSpeedParams()
    {
        new JFXPanel();
        Controller.setMode(MapType.Map1);
        Object[][] typeListList = { //
                { new Point2D(0,0) ,  new Point2D(5,0 )  ,  1 , 1000 },
                { new Point2D(0,0) ,  new Point2D(0,5 )  ,  2 , 1000 },
                { new Point2D(0,0) ,  new Point2D(-5,0) ,  3 , 1000 },
                { new Point2D(0,0) ,  new Point2D(0,-5) ,  4 , 1000 },
                { new Point2D(5,0)  , new Point2D(0,0)  ,  5 , 1000 },
                { new Point2D(0,5)  , new Point2D(0,0)  ,  6 , 1000 },
                { new Point2D(-5,0) , new Point2D(0,0)  ,  7 , 1000 },
                { new Point2D(0,-5) , new Point2D(0,0)  ,  8 , 1000 },
        };
        return typeListList;
    }

    @Test
    @Parameters(method = "maxSpeedParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void maxSpeedTest(Point2D centerPos,Point2D seekPos,double maxSpeed, double maxForce){
        Player p = new Player(0,0);
        Point2D pos = centerPos.subtract(p.getSize().multiply(0.5));
        p = new Player(pos.getX(),pos.getY());

        p.setMaxSpeed(maxSpeed);
        p.setMaxForce(maxForce);
        Steering sb = new Steering(p);
        sb.setTarget(seekPos);
        sb.seekOn();
        Point2D force = sb.calculate();
        //check magnitude
        assertTrue(p.getMaxSpeed() >= force.magnitude());
    }


    private Object[] maxForceParams()
    {
        new JFXPanel();
        Controller.setMode(MapType.Map1);
        Object[][] typeListList = { //
                { new Point2D(0,0) ,  new Point2D(5,0 )  , 1000 , 1},
                { new Point2D(0,0) ,  new Point2D(0,5 )  , 1000 , 2},
                { new Point2D(0,0) ,  new Point2D(-5,0) ,  1000 , 3},
                { new Point2D(0,0) ,  new Point2D(0,-5) ,  1000 , 4},
                { new Point2D(5,0)  , new Point2D(0,0)  ,  1000 , 5},
                { new Point2D(0,5)  , new Point2D(0,0)  ,  1000 , 6},
                { new Point2D(-5,0) , new Point2D(0,0)  ,  1000 , 7},
                { new Point2D(0,-5) , new Point2D(0,0)  ,  1000 , 8},
        };
        return typeListList;
    }

    @Test
    @Parameters(method = "maxForceParams")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void maxForceTest(Point2D centerPos,Point2D seekPos,double maxSpeed, double maxForce){
        Player p = new Player(0,0);
        Point2D pos = centerPos.subtract(p.getSize().multiply(0.5));
        p = new Player(pos.getX(),pos.getY());

        p.setMaxSpeed(maxSpeed);
        p.setMaxForce(maxForce);
        Steering sb = new Steering(p);
        sb.setTarget(seekPos);
        sb.seekOn();
        Point2D force = sb.calculate();
        //check magnitude
        assertTrue(p.getMaxSpeed() >= force.magnitude());
    }
}

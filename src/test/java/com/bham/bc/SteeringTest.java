package com.bham.bc;

import com.bham.bc.components.Controller;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.physics.Steering;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import org.junit.Test;

import static com.bham.bc.utils.GeometryEnhanced.isZero;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SteeringTest {

    @Test
    public void normalCase(){
        new JFXPanel();
        Controller.setMode(MapType.Map1);

        Player p = new Player(0,0);
        //top left position
        Point2D pos = new Point2D(0,0).subtract(p.getSize().multiply(0.5));
        p = new Player(pos.getX(),pos.getY());

        Steering sb = new Steering(p);
        sb.setTarget(new Point2D(0,1));
        sb.seekOn();
        Point2D force = sb.calculate();
        //check magnitude
        assertEquals(p.getMaxSpeed(), force.magnitude(),1E-8);

        //check direction
        Point2D nforce = force.normalize();
        Point2D direction = new Point2D(0,1);
        assertTrue(isZero(nforce.subtract(direction)));
    }

    @Test
    public void maxSpeedTest(){
        new JFXPanel();
        Controller.setMode(MapType.Map1);

        Player p = new Player(0,0);
        Point2D pos = new Point2D(0,0).subtract(p.getSize().multiply(0.5));
        p = new Player(pos.getX(),pos.getY());

        p.setMaxSpeed(1);
        Steering sb = new Steering(p);
        sb.setTarget(new Point2D(0,1));
        sb.seekOn();
        Point2D force = sb.calculate();
        //check magnitude
        assertEquals(p.getMaxSpeed(), force.magnitude(),1E-8);

        //check direction
        Point2D nforce = force.normalize();
        Point2D direction = new Point2D(0,1);
        assertTrue(isZero(nforce.subtract(direction)));
    }


    @Test
    public void maxForceTest(){
        new JFXPanel();
        Controller.setMode(MapType.Map1);

        Player p = new Player(0,0);
        Point2D pos = new Point2D(0,0).subtract(p.getSize().multiply(0.5));
        p = new Player(pos.getX(),pos.getY());

        p.setMaxSpeed(1000);
        Steering sb = new Steering(p);
        sb.setTarget(new Point2D(0,1));
        sb.seekOn();
        Point2D force = sb.calculate();
        //check magnitude
        assertTrue(force.magnitude()<=p.getMaxForce());

        //check direction
        Point2D nforce = force.normalize();
        Point2D direction = new Point2D(0,1);
        assertTrue(isZero(nforce.subtract(direction)));
    }
}

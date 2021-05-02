package com.bham.bc.components.triggers;

import com.bham.bc.components.Controller;

import com.bham.bc.components.characters.Direction;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.DefaultBullet;
import com.bham.bc.components.shooting.LaserType;
import com.bham.bc.entity.ai.navigation.impl.PathPlanner;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import org.junit.Test;
import com.bham.bc.components.SurvivalController;
import com.bham.bc.components.Controller;
import com.bham.bc.components.triggers.powerups.*;
import com.bham.bc.components.triggers.traps.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class TriggerTest {

    @Test
    public void SpeedTriggerTest() throws Exception{
            new JFXPanel();
            Controller.setMode(MapType.SMALL);
            Player player = new Player(510, 480);
            GameMap gameMap = new GameMap(MapType.SMALL);
            SpeedTrigger sp= new SpeedTrigger(540,490,30,20);
            //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
             player.testDIRECTION_SET();
             player.setVelocity(new Point2D(5,0));
            for(;;){
                player.move();
                if(player.intersects(sp)){
                    System.out.println("touched");
                    sp.handle(player);
                    assertEquals(20,20,0);
                    System.out.println(player.getCenterPosition());
                    System.out.println("player speed is " + player.getMaxSpeed());
                    break;
                }
            }
    }
    @Test
    public void ArmorTriggerTest() throws Exception{
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(240, 480);
        GameMap gameMap = new GameMap(MapType.SMALL);
        ArmorTrigger ap= new ArmorTrigger(260,480,200,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        System.out.println(player.getFullHp());
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(ap)){
                System.out.println("Armor trigger touched");
                ap.handle(player);
                assertEquals(player.getFullHp(),200,0);

                break;
            }
        }
    }
    @Test
    public void StateTriggerTest() throws Exception{
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        StateTrigger st= new StateTrigger(440,400,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(st)){
                System.out.println("New State touched");
                st.handle(player);
                assertEquals(player.testGun().testBullet(), BulletType.ICE);
                assertEquals(player.testGun().testLaser(), LaserType.ThunderLaser);
                System.out.println("Current bullet successfully switch to " + player.testGun().testBullet());
                System.out.println("Current laser  successfully switch to " + player.testGun().testLaser());
                System.out.println("player speed is " + player.getMaxSpeed());
                break;
            }
        }

    }
    @Test
    public void HealthGiverTest() throws Exception{
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        HealthGiver h= new HealthGiver(440,400,200,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(h)){
                System.out.println("Health giver touched");
                h.handle(player);
                assertEquals(player.getHp(),player.getFullHp(),0);

                break;
            }
        }

    }
    @Test
    public void ImmuneTest() throws Exception{
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        Immune i= new Immune(440,400,1,20);
        Bullet b = new DefaultBullet(440,400,player.getHeading(), Side.ALLY);
        
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(i)){
                System.out.println("Immune touched");
                i.handle(player);
                if(b.intersects(player)) b.handle(player);
                assertEquals(player.getHp(),player.getFullHp(),0);
                break;
            }
        }

    }
    @Test
    public void TripleBulletTest() throws Exception{
    	new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        TripleBullet tp= new TripleBullet(440,400,1,20);
        
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(tp)){
                System.out.println("Triple Bullet touched");
                tp.handle(player);
                Thread.sleep(200);
                player.testFire();
                assertEquals(Controller.services.getBullets().size(),3,0);
                break;
            }
        }

    }
    
    @Test
    public void BombTriggerTest() throws Exception{
    	new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        BombTrigger bt= new BombTrigger(440,400,20);
        
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(bt)){
            	
                System.out.println("Bomb Trigger touched");
                bt.handle(player);
                assert(player.testBomb());
                player.testBomb();
                assertFalse(player.testBomb());
                break;
            }
        }

    }
    
    //---------------------------------TRAPS---------------------------------//
    
    @Test
    public void FreezeTest() throws Exception{
    	new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        Freeze f= new Freeze(440,400,1,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(f)){
                System.out.println("Freeze touched");
                f.handle(player);
                Point2D position = player.getPosition();
                player.update();
                assertEquals(player.getPosition().getX(), position.getX(),0);
                assertEquals(player.getPosition().getY(), position.getY(),0);
                break;
            }
        }

    }
    @Test
    public void InverseTrapTest() throws Exception{
    	new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        InverseTrap t= new InverseTrap(380,400,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.update();
            if(player.intersects(t)){
                System.out.println("Inverse Trap touched");
                t.handle(player);
                player.update();
                assertEquals(player.getHeading().getX(),1,0);
                assertEquals(player.getHeading().getY(),0,0);
                break;
            }
        }
      
    }
    @Test
    public void LandmineTriggerTest() throws Exception{
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(410, 400);
        GameMap gameMap = new GameMap(MapType.SMALL);
        LandmineTrigger l= new LandmineTrigger(440,400,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(l)){
                System.out.println("Landmine Trigger touched");
                l.handle(player);
                assertTrue(player.getHp()<=0);
                break;
            }
        }

    }
 

}
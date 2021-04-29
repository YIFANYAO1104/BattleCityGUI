package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.Controller;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.LaserType;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import org.junit.Test;

import static org.junit.Assert.*;

public class TriggerTest {

    @Test
    public void test1() throws Exception{
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
    public void test2() throws Exception{
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player player = new Player(240, 480);
        GameMap gameMap = new GameMap(MapType.SMALL);
        ArmorTrigger ap= new ArmorTrigger(260,480,200,20);
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        System.out.println(player.getMaxHp());
        player.setVelocity(new Point2D(5,0));
        for(;;){
            player.move();
            if(player.intersects(ap)){
                System.out.println("Armor trigger touched");
                ap.handle(player);
                assertEquals(player.getMaxHp(),200,0);

                break;
            }
        }
    }
    @Test
    public void test3() throws Exception{
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

}
package com.bham.bd.components.shooting;

import com.bham.bd.components.Controller;
import com.bham.bd.components.characters.Player;
import com.bham.bd.components.characters.Side;
import com.bham.bd.components.characters.agents.enemies.Nova;
import com.bham.bd.components.environment.MapType;
import javafx.embed.swing.JFXPanel;
import static org.junit.Assert.*;

import org.junit.Test;

public class armoryTest {

    @Test
    public void bulletTypeTest() {
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player x =  new Player(10,10);
        Gun gun =x.testGun();
        assertTrue(gun.testBullet().equals(BulletType.DEFAULT));
        assertTrue(gun.testBullet().WIDTH==6);
        assertTrue(gun.testBullet().HEIGHT==12);
    }

    @Test
    public void laserTest() throws Exception {
        new JFXPanel();
        Controller.setMode(MapType.SMALL);
        Player x =  new Player(10,10);
        Gun gun =x.testGun();
        LaserType currentLaser =gun.testLaser();
        assertTrue(currentLaser.equals(LaserType.Default));
        assertTrue(currentLaser.getWidth()==10);
        assertTrue(currentLaser.getHeight()==200);
        assertTrue(currentLaser.getImage()!=null);
    }

    @Test
    public void hitAndDamageTest() {
        new JFXPanel();
        int i =0;
        Controller.setMode(MapType.SMALL);
        Player player = new Player(510, 480);
        Nova enemy1 =new Nova(510,470);
        //GameMap gameMap = new GameMap(MapType.SMALL);
        System.out.println(enemy1.getHp());
        System.out.println(enemy1.getFullHp());
        //PathPlanner p = new PathPlanner(player, gameMap.getGraph());
        player.testDIRECTION_SET();
        Gun gun = player.testGun();
        Bullet b1 = new DefaultBullet(510,480,player.getHeading(), Side.ALLY);
        while(enemy1.getHp()==enemy1.getFullHp()){
            b1.move();
            if(b1.intersects(enemy1)){
                b1.handle(enemy1);
                System.out.println(enemy1.getHp());
            }
        }
        assertTrue(enemy1.getHp()==enemy1.getFullHp()-10);



        }


}

package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.HomeTank2;
import com.bham.bc.components.characters.TrackableCharacter;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.environment.obstacles.CommonWall;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.HomeTank;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class CenterController extends BaseGameEntity {


    public static CenterController centerController = new CenterController();
    /**
     * The message to indicate Victory Or Defeat
     */
    private Boolean win=false,lose=false;
    /** Initialize HomeTank(Player Tank)*/
    private HomeTank homeTank;
    private HomeTank2 homeTank2;
    /** Initialize a Container of Enemy Tanks */
    private List<Enemy> enemyTanks = new ArrayList<Enemy>();
    /** Initialize a Container of Tanks that got bombed and hit*/
    private List<BombTank> bombTanks = new ArrayList<BombTank>();
    /** Initialize a Container of All Bullets created by All Tanks*/
    private List<Bullet> bullets = new ArrayList<Bullet>();
    /** Initialize an Object Of GameMap*/
    private GameMap gameMap = new GameMap();


    //These are functions that might be used by frontend----------------------------------------------------
    /**
     * Method to determine if player wins the game
     * @return
     */
    public boolean isWin(){
        return enemyTanks.isEmpty() && gameMap.isHomeLive() && homeTank.isLive();
    }

    /**
     * Status to indicates defeat
     * Home destroyed OR Player Tank dead
     * @return
     */
    public boolean isLoss(){
        return !gameMap.isHomeLive() || !homeTank.isLive();
    }

    /**
     * Get the number of enemy tanks from enemy tank list
     * @return
     */
    public int getEnemyNumber(){
        return enemyTanks.size();
    }
    /**
     *
     * @return Current HP of Player Tank
     */
    public int getLife(){
        return homeTank.getLife();
    }
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is released and use as Parameter to determine Player Tank's Action
     * @param e
     */
    public void keyReleased(KeyEvent e){
        homeTank.keyReleased(e);
        homeTank2.keyReleased(e);
    }
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is pressed and use as Parameter to determine Player Tank's Action
     * @param e
     */
    public void keyPressed(KeyEvent e){
        homeTank.keyPressed(e);
        homeTank2.keyPressed(e);
    }
    //These are functions that might be used by frontend----------------------------------------------------


    /**
     * Clear all objects on tht map
     */
    public void clear(){
        gameMap.clearAll();
        enemyTanks.clear();
        bullets.clear();
        bombTanks.clear();
//        homeTank.setLive(false);
    }


    /**
     * Constructor of CenterController
     */
    public CenterController(){
        super(GetNextValidID(),-1,-1);
        homeTank = new HomeTank(300, 560, Direction.STOP);
        homeTank2 = new HomeTank2(500, 560, Direction.STOP);
        initEnemies();
    }
    /**
     * A method to generate certain number of Enemy Tanks
     * Adding created Objects into enemyTanks(list)
     */
    private void initEnemies() {
        for (int i = 0; i < 20; i++) {
            if (i < 9){
                enemyTanks.add(new Enemy(150 + 70 * i, 40,  Direction.D));
            } else if (i < 15){
                enemyTanks.add(new Enemy(700, 140 + 50 * (i - 6), Direction.D));
            } else{
                enemyTanks.add(new Enemy(10, 50 * (i - 12),  Direction.D));
            }
        }
    }


    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    /**
     /** Overriding Method to indicates Game Logic \
     */

    @Override
    public void update() {

        //move-----------------
        homeTank.update();
        homeTank2.update();
        /*
        for (Enemy e : enemyTanks) {
            e.update();
        }

         */


        //move-----------------

        //map----------------------------------
        gameMap.update(homeTank);
        gameMap.update(homeTank2);

        /*
        for (Enemy e : enemyTanks) {
            gameMap.update(e);
        }




        //map----------------------------------

        //tanks----------------------------------

        homeTank.collideWithTanks(enemyTanks);
        for (Enemy e : enemyTanks) {
            e.collideWithTanks(enemyTanks);
        }

         */


        //tanks----------------------------------
        /**
         * Use nested For Loop to update game state
         * Keep Tracking Bullets to see if Bullets hit Bullets, and Updates game state (Inner Loop)
         * Keep Tracking Bullets to see(Outer Loop):
         * 1.If Bullets hit enemy Tanks(Updates game state)
         * 2.If Bullets hit Player (Updates game state)
         * 3.If Bullets hits environment Objects e.g Home and Wall (Updates game state)
         */

        /*
        for (int i = 0; i < bullets.size(); i++) {
            Bullet m = bullets.get(i);
            m.update();
            m.hitTanks(enemyTanks);
            m.hitTank(homeTank);
            for(int j=0;j<bullets.size();j++){
                if (i==j) continue;
                Bullet bts=bullets.get(j);
                m.hitBullet(bts);
            }
            gameMap.update(m);
        }

         */




        gameMap.updateObstacles();
    }

    @Override
    public void render(GraphicsContext gc) {
        /**
         *  The order of Render does matter
         *  The latter render will cover the previous render
         *  For example,rending Tree at the end leads to successfully Shading
         */
        /*
        for (int i = 0; i < bullets.size(); i++) {
            Bullet t = bullets.get(i);
            t.render(gc);
        }

         */

        //the blood bar is here. But it's covered currently
        homeTank.render(gc);
        homeTank2.render(gc);
        /*
        for (int i = 0; i < enemyTanks.size(); i++) {
            Enemy t = enemyTanks.get(i);
            t.render(gc);
        }
        for (int i = 0; i < bombTanks.size(); i++) {
            BombTank bt = bombTanks.get(i);
            bt.render(gc);
        }

         */


        gameMap.renderAll(gc);


    }



    //These are functions that might be used by backend----------------------------------------------------
    //For example, bullet created a bomb 'b', it will call addBombTank(b), but not bombTanks.add(b);
    //By this, we could protect the memebers in CenterController

    /** The calling from each other member must implements the centerController Interface*/
    /**
     * Turn Back
     * @param t
     */
    public void changToOldDir(MovingEntity t){
        t.changToOldDir();
    }

    public void removeWall(CommonWall w){
        gameMap.removeHomeWall(w);
    }

    public void removeEnemy(Enemy enemy){
        enemyTanks.remove(enemy);
    }

    /**
     * Adding bombtank to bomb tank list
     * @param b
     */
    public void addBombTank(BombTank b){
        bombTanks.add(b);
    }

    /**
     * Remove bomb tank from bomb tank list
     * @param b
     */
    public void removeBombTank(BombTank b){
        bombTanks.remove(b);
    }

    public int getHomeTankX(){
        return homeTank.getX();
    }

    public int getHomeTankY(){
        return homeTank.getY();
    }

    public Rectangle getHomeHitBox(){
        return homeTank.getHitBox();
    }

    public TrackableCharacter getHomeTank() {
        return homeTank;
    }

    public TrackableCharacter getHomeTank2() {
        return homeTank2;
    }

    /**
     * Adding bullets to bullets list
     * @param m
     */
    public void addBullet(Bullet m){
        bullets.add(m);
    }

    /**
     * Removing bullets to bullets list
     * @param m
     */
    public void removeBullet(Bullet m){
        bullets.remove(m);
    }
    //These are functions that might be used by backend----------------------------------------------------

    @Override
    public Rectangle getHitBox() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return "Centerctroller";
    }
}

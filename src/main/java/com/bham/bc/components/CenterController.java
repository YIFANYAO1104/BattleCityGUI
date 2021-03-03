package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
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

public abstract class CenterController extends BaseGameEntity implements FrontendServices,BackendServices{


    private static CenterController centerController;
    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    public static void setMode(Mode mode){
        switch (mode) {
            case Survival:
                centerController = new SurvivalController();
                break;
            case Challenge:
                centerController = new ChallengeController();
                break;
        }
        frontendServices = centerController;
        backendServices = centerController;
    }

    /**
     * The message to indicate Victory Or Defeat
     */
    protected Boolean win=false,lose=false;
    /** Initialize HomeTank(Player Tank)*/
    protected HomeTank homeTank;
    /** Initialize a Container of Enemy Tanks */
    protected List<Enemy> enemyTanks = new ArrayList<Enemy>();
    /** Initialize a Container of Tanks that got bombed and hit*/
    protected List<BombTank> bombTanks = new ArrayList<BombTank>();
    /** Initialize a Container of All Bullets created by All Tanks*/
    protected List<Bullet> bullets = new ArrayList<Bullet>();



    //These are functions that might be used by frontend----------------------------------------------------
    /**
     * Method to determine if player wins the game
     * @return
     */
    public abstract boolean isWin();

    /**
     * Status to indicates defeat
     * Home destroyed OR Player Tank dead
     * @return
     */
    public abstract boolean isLoss();

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
    }
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is pressed and use as Parameter to determine Player Tank's Action
     * @param e
     */
    public void keyPressed(KeyEvent e){
        homeTank.keyPressed(e);
    }



    /**
     * Clear all objects on tht map
     */
    public void clear(){
        enemyTanks.clear();
        bullets.clear();
        bombTanks.clear();
//        homeTank.setLive(false);
    }
    //These are functions that might be used by frontend----------------------------------------------------

    /**
     * Constructor of CenterController
     */
    public CenterController(){
        super(GetNextValidID(),-1,-1);
    }



    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
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

//    public void removeWall(CommonWall w){
//        gameMap.removeHomeWall(w);
//    }

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

    @Override
    public boolean isIntersect(BaseGameEntity b) {
        return false;
    }
}

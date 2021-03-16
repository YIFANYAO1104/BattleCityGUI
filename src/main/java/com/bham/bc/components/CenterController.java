package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.components.characters.TrackableCharacter;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.Player;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public abstract class CenterController extends BaseGameEntity implements FrontendServices, BackendServices {

    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    public static void setMode(MODE mode){
        CenterController centerController = null;
        switch (mode) {
            case SURVIVAL:
                centerController = new SurvivalController();

                break;
            case CHALLENGE:
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
    protected Player player;
    /** Initialize a Container of Enemy Tanks */
    protected List<Enemy> enemies = new ArrayList<Enemy>();
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
        return enemies.size();
    }
    /**
     *
     * @return Current HP of Player Tank
     */
    public int getLife(){
        return player.getHp();
    }
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is released and use as Parameter to determine Player Tank's Action
     * @param e
     */
    public void keyReleased(KeyEvent e){
        player.keyReleased(e);
    }
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is pressed and use as Parameter to determine Player Tank's Action
     * @param e
     */
    public void keyPressed(KeyEvent e){
        player.keyPressed(e);
    }

    public TrackableCharacter getHomeTank(){
        return player;
    }

    @Override
    public void renderHitBoxes(AnchorPane hitBoxPane) {
        hitBoxPane.getChildren().clear();

        Rectangle mapConstrain = new Rectangle(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, Color.TRANSPARENT);
        mapConstrain.setStroke(Color.RED);
        mapConstrain.setStrokeWidth(5);

        Shape playerHitBox = player.getHitBox();
        playerHitBox.setFill(Color.TRANSPARENT);
        playerHitBox.setStroke(Color.RED);
        playerHitBox.setStrokeWidth(2);

        hitBoxPane.getChildren().addAll(playerHitBox, mapConstrain);
    }
    //These are functions that might be used by frontend----------------------------------------------------


    /**
     * Clear all objects on tht map
     */
    public void clear(){
        enemies.clear();
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
    public void changeToOldDir(MovingEntity t){
        t.changToOldDir();
    }

    public void addEnemy(Enemy enemy) { return; }

    public void removeEnemy(Enemy enemy){
        enemies.remove(enemy);
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

    public double getPlayerX(){
        return player.getX();
    }

    public double getPlayerY(){
        return player.getY();
    }

    public Shape getHomeHitBox(){
        return player.getHitBox();
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

    @Override
    public void removeObstacle(GenericObstacle go) {
    }

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
    public boolean intersects(BaseGameEntity b) {
        return false;
    }
}

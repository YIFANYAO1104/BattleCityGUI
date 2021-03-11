package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.TrackableCharacter;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.HomeTank;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.messaging.MessageTypes;
import com.bham.bc.components.characters.Player;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import static com.bham.bc.utils.messaging.MessageTypes.*;

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

    private List<Bullet> bullets = new ArrayList<Bullet>();
    /** Initialize an Object Of GameMap*/
    private GameMap gameMap = new GameMap("/test.json");
    //survival
    private SparseGraph sg;


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
        homeTank = new HomeTank(200, 400, Direction.STOP);
        initEnemies();
        System.out.println("start");
        intialMap();
        System.out.println("over");
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
        for (Enemy e : enemyTanks) {
            e.update();
        }
        //move-----------------

        //map----------------------------------
        gameMap.update(homeTank);
        for (Enemy e : enemyTanks) {
            gameMap.update(e);
        }
        //map----------------------------------

        //tanks----------------------------------
        homeTank.collideWithTanks(enemyTanks);
        for (Enemy e : enemyTanks) {
            e.collideWithTanks(enemyTanks);
        }
        //tanks----------------------------------
        /**
         * Use nested For Loop to update game state
         * Keep Tracking Bullets to see if Bullets hit Bullets, and Updates game state (Inner Loop)
         * Keep Tracking Bullets to see(Outer Loop):
         * 1.If Bullets hit enemy Tanks(Updates game state)
         * 2.If Bullets hit Player (Updates game state)
         * 3.If Bullets hits environment Objects e.g Home and Wall (Updates game state)
         */
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
        gameMap.updateObstacles();
    }

    private void intialMap(){
        HandyGraphFunctions hgf = new HandyGraphFunctions(); //operation class
        sg = new SparseGraph<NavNode, GraphEdge>(false); //single direction
        hgf.GraphHelper_CreateGrid(sg,1200,1600,50,50); //make network
        ArrayList<Vector2D> vectors11 = sg.getAllVector(); //get all nodes
//        sg.display();
        for (int i = 0; i < vectors11.size(); i++) { //remove invalid nodes
            Vector2D vv1 = vectors11.get(i);
            gameMap.collideWithRectangle(sg.ID(),i,new Rectangle(vv1.getX(),vv1.getY(),14.0,14.0));
//            Rectangle rr1 = new Rectangle(vv1.x,vv1.y,14.0,14.0);
        }

        //remove unreachale nodes
        Floodfill fl = new Floodfill(sg.TrickingTank(homeTank.getPositionV()));

        sg = fl.stratFLood(sg);

    }

    @Override
    public void render(GraphicsContext gc) {
        /**
         *  The order of Render does matter
         *  The latter render will cover the previous render
         *  For example,rending Tree at the end leads to successfully Shading
         */

        for (int i = 0; i < bullets.size(); i++) {
            Bullet t = bullets.get(i);
            t.render(gc);
        }

        //the blood bar is here. But it's covered currently
        homeTank.render(gc);
        for (int i = 0; i < enemyTanks.size(); i++) {
            Enemy t = enemyTanks.get(i);
            t.render(gc);
        }
        for (int i = 0; i < bombTanks.size(); i++) {
            BombTank bt = bombTanks.get(i);
            bt.render(gc);
        }

        gameMap.renderAll(gc);
        // -----------------------------------------------------------------------------------------------
        sg.render(gc);
        sg.TrickingTank(homeTank.getPositionV(),gc);
        for (int i = 0; i < enemyTanks.size(); i++) {
            Enemy t = enemyTanks.get(i);
            sg.TrickingTank(t.getPositionV(),gc);
        }
        //------------------------------------------------------------------------------------------
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

    public int getPlayerX(){
        return player.getX();
    }

    public int getPlayerY(){
        return player.getY();
    }

    public Rectangle getHomeHitBox(){
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
    public void removeObstacle(GenericObstacle go) {}

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

    public void setGameMap(GameMap gm) {
        this.gameMap = gm;
    }
}

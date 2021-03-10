package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.TrackableCharacter;
import com.bham.bc.components.environment.obstacles.Home;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.algrithem.Floodfill;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;
import com.bham.bc.utils.messaging.MessageDispatcher;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.environment.obstacles.CommonWall;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.HomeTank;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.messaging.MessageTypes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import static com.bham.bc.utils.messaging.MessageTypes.*;

public class CenterController extends BaseGameEntity {


    public static CenterController centerController = new CenterController();
    /**
     * The message to indicate Victory Or Defeat
     */
    private Boolean win=false,lose=false;
    /** Initialize HomeTank(Player Tank)*/
    private HomeTank homeTank;
    /** Initialize a Container of Enemy Tanks */
    private List<Enemy> enemyTanks = new ArrayList<Enemy>();
    /** Initialize a Container of Tanks that got bombed and hit*/
    private List<BombTank> bombTanks = new ArrayList<BombTank>();
    /** Initialize a Container of All Bullets created by All Tanks*/
    private List<Bullet> bullets = new ArrayList<Bullet>();
    /** Initialize an Object Of GameMap*/
    private GameMap gameMap = new GameMap();

    private SparseGraph sg;


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
    }
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is pressed and use as Parameter to determine Player Tank's Action
     * @param e
     */
    public void keyPressed(KeyEvent e){
        homeTank.keyPressed(e);
    }

    public TrackableCharacter getHomeTank(){
        return homeTank;
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
        homeTank = new HomeTank(200, 400, Direction.STOP);
        initEnemies();
        System.out.println("start");
        intialMap();
        System.out.println("over");
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
        HandyGraphFunctions hgf = new HandyGraphFunctions();
        SparseGraph<NavNode, GraphEdge> shg = new SparseGraph<NavNode, GraphEdge>(false);
        sg = shg;
        hgf.GraphHelper_CreateGrid(sg,1200,1600,50,50);
        ArrayList<Vector2D> vectors11 = sg.getAllVector();
//        sg.display();
        for (int i = 0; i < vectors11.size(); i++) {
            Vector2D vv1 = vectors11.get(i);
            gameMap.collideWithRectangle(sg.ID(),i,new Rectangle(vv1.getX(),vv1.getY(),14.0,14.0));
//            Rectangle rr1 = new Rectangle(vv1.x,vv1.y,14.0,14.0);
        }


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
        NavNode n1 = sg.TrickingTank(homeTank.getPositionV());
        for (int i = 0; i < enemyTanks.size(); i++) {
            Enemy t = enemyTanks.get(i);
            sg.TrickingTank(t.getPositionV(),gc);
            NavNode n2 = sg.TrickingTank(t.getPositionV());
            Vector2D v3 = n1.Pos().mines(n2.Pos());
            Direction d1 = Direction.STOP;
            if(v3.getY()>1){
                d1 = Direction.D;
            }else if(v3.getY()<-1){
                d1 = Direction.U;
            }else if(v3.getX()>1){
                d1 =Direction.R;
            }else if(v3.getX()<-1){
                d1 =Direction.L;
            }else
                d1 =Direction.STOP;
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,centerController.ID(),t.ID(),Msg_direction,d1);

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

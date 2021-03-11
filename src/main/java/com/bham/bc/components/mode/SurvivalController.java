package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.entity.Direction;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.graph.HandyGraphFunctions;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.algrithem.Floodfill;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SurvivalController extends CenterController {
    /** Initialize an Object Of GameMap*/
    private GameMap gameMap;

    public SurvivalController(){
        super();
        gameMap = new GameMap("/64x64.json");
        player = new Player(16*32, 16*32, Direction.STOP);
        initEnemies();
        System.out.println("start");
        intialMap();
        System.out.println("over");
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
        Floodfill fl = new Floodfill(sg.TrickingTank(player.getPositionV()));

        sg = fl.stratFLood(sg);

    }

    /**
     * A method to generate certain number of Enemy Tanks
     * Adding created Objects into enemyTanks(list)
     */
    private void initEnemies() {
        enemies.add(new Enemy(16*3, 16*3,  Direction.D));
        enemies.add(new Enemy(16*61, 16*3,  Direction.D));
        enemies.add(new Enemy(16*3, 16*61,  Direction.D));
        enemies.add(new Enemy(16*61, 16*61,  Direction.D));
    }

    /**
     /** Overriding Method to indicates Game Logic \
     */

    @Override
    public void update() {

        //move-----------------
        player.update();
        for (Enemy e : enemies) {
            e.update();
        }
        //move-----------------

        //map----------------------------------
        gameMap.update(player);
        for (Enemy e : enemies) {
            gameMap.update(e);
        }
        //map----------------------------------

        //tanks----------------------------------
        player.collideWithTanks(enemies);
        for (Enemy e : enemies) {
            e.collideWithTanks(enemies);
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
            m.hitTanks(enemies);
            m.hitTank(player);
            for(int j=0;j<bullets.size();j++){
                if (i==j) continue;
                Bullet bts=bullets.get(j);
                m.hitBullet(bts);
            }
            gameMap.update(m);
        }
        gameMap.updateObstacles();
    }

    @Override
    public void render(GraphicsContext gc) {
        /**
         *  The order of Render does matter
         *  The latter render will cover the previous render
         *  For example,rending Tree at the end leads to successfully Shading
         */
        gameMap.renderBottomLayer(gc);
        for (int i = 0; i < bullets.size(); i++) {
            Bullet t = bullets.get(i);
            t.render(gc);
        }

        //the blood bar is here. But it's covered currently
        player.render(gc);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy t = enemies.get(i);
            t.render(gc);
        }
        for (int i = 0; i < bombTanks.size(); i++) {
            BombTank bt = bombTanks.get(i);
            bt.render(gc);
        }
        gameMap.renderTopLayer(gc);

        // -----------------------------------------------------------------------------------------------
        sg.render(gc);
        sg.TrickingTank(player.getPositionV(),gc);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy t = enemies.get(i);
            sg.TrickingTank(t.getPositionV(),gc);
        }
        //------------------------------------------------------------------------------------------

    }

    /**
     * Method to determine if player wins the game
     * @return
     */
    @Override
    public boolean isWin(){
        return enemies.isEmpty() && gameMap.isHomeLive() && player.isAlive();
    }

    /**
     * Status to indicates defeat
     * Home destroyed OR Player Tank dead
     * @return
     */
    @Override
    public boolean isLoss(){
        return !gameMap.isHomeLive() || !player.isAlive();
    }

    /**
     * Clear all objects on tht map
     */
    public void clear(){
        super.clear();
        gameMap.clearAll();
    }
}

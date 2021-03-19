package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.Direction;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.graph.SparseGraph;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;


import java.util.ArrayList;

public class SurvivalController extends CenterController {


    private SparseGraph sg;

    public SurvivalController(MapType mapType){
        super();
        gameMap = new GameMap(mapType);
        gameMap.initialGraph(new Point2D(16*32, 16*32));
        player = new Player(16*32, 16*32, Direction.STOP,gameMap);
        initEnemies();
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
    public void removeObstacle(GenericObstacle go) {
        gameMap.removeObstacle(go);
        player.createNewRequestItem();//每个pathpalnner只有一个任务
//        player.createNewRequestAStar(;
        gameMap.initialGraph(player.getPosition());         // update the map, But it seems really slow, I would improve it
    }


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
        gameMap.renderGraph(gc, allCharactersLocation());
    }

    /**
     *
     * @return temp1 the list with all characters' location inlcuding player.
     */
    public ArrayList<Point2D> allCharactersLocation(){
        ArrayList<Point2D> temp1 = new ArrayList<Point2D>();
        temp1.add(player.getPosition());
        for (Enemy e1 :enemies){
            temp1.add(e1.getPosition());
        }
        return temp1;
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

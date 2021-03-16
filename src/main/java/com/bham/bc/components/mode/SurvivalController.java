package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.graph.SparseGraph;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;


import java.util.ArrayList;

public class SurvivalController extends CenterController {
    /** Initialize an Object Of GameMap*/
    private GameMap gameMap;

    public SurvivalController(){
        super();
        gameMap = new GameMap("/64x64.json");
        player = new Player(16*32, 16*32);
        gameMap.initialGraph(player.getPosition());
        initEnemies();
    }

    /**
     * A method to generate certain number of Enemy Tanks
     * Adding created Objects into enemyTanks(list)
     */
    private void initEnemies() {
        enemies.add(new Enemy(16*3, 16*3));
        enemies.add(new Enemy(16*61, 16*3));
        enemies.add(new Enemy(16*3, 16*61));
        enemies.add(new Enemy(16*61, 16*61));
    }

    /**
     /** Overriding Method to indicates Game Logic \
     */

    @Override
    public void removeObstacle(GenericObstacle go) { gameMap.removeObstacle(go); }


    @Override
    public void update() {

        player.update();
        enemies.stream().forEach(Enemy::update);
        gameMap.update(player);

        //for (Enemy e : enemies) { gameMap.update(e); }

        //player.collideWithTanks(enemies);

        //for (Enemy e : enemies) { e.collideWithTanks(enemies); }
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
            //m.hitTanks(enemies);
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
        ArrayList<Point2D> temp1 = new ArrayList<>();
        temp1.add(player.getPosition());
        for (Enemy e1 :enemies){
            temp1.add(e1.getPosition());
        }
        return temp1;
    }


    @Override
    public boolean isWin(){
        return enemies.isEmpty() && gameMap.isHomeLive() && player.exists();
    }


    @Override
    public boolean isLoss(){
        return !gameMap.isHomeLive() || !player.exists();
    }

    /**
     * Clear all objects on the map
     */
    public void clear(){
        super.clear();
        gameMap.clearAll();
    }
}

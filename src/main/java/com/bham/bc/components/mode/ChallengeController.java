package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.entity.Direction;
import com.bham.bc.entity.physics.BombTank;
import javafx.scene.canvas.GraphicsContext;

public class ChallengeController extends CenterController {

    @Override
    public boolean isWin() {
        return false;
    }

    @Override
    public boolean isLoss() {
        return false;
    }

    public ChallengeController(){
        super();
        player = new Player(16*32, 16*32, Direction.STOP,null);
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
    public void update() {

        if (enemies.isEmpty()) initEnemies();
        //move-----------------
        player.update();
        for (Enemy e : enemies) {
            e.update();
        }
        //move-----------------

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
        }
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
        player.render(gc);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy t = enemies.get(i);
            t.render(gc);
        }
        for (int i = 0; i < bombTanks.size(); i++) {
            BombTank bt = bombTanks.get(i);
            bt.render(gc);
        }
    }
}

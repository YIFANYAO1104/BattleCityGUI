package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Character;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.GenericObstacle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;


import java.util.ArrayList;
import java.util.stream.Collectors;

public class SurvivalController extends CenterController {

    public SurvivalController(){
        super();
        gameMap = new GameMap("/64x64.json");
        player = new Player(16*32 - Player.WIDTH/2, 16*32-Player.HEIGHT/2);
        gameMap.initGraph(player.getPosition());
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

    @Override
    /**TODO: instead of using loops, ask entities to check map areas if they are free.
     * TODO: If they are not free, then search through all the entities to find which one intersects it*/
    public void update() {
        gameMap.update();
        player.update();
        enemies.forEach(Enemy::update);
        bullets.forEach(Bullet::update);

        gameMap.handleAll(player, enemies, bullets);
        player.handleAll(getCharacters(), bullets);
        enemies.forEach(enemy -> enemy.handleAll(getCharacters(), bullets));

        bullets.removeIf(b -> !b.exists());
        enemies.removeIf(e -> !e.exists());
    }

    @Override
    public void render(GraphicsContext gc) {
        gameMap.renderBottomLayer(gc);

        bullets.forEach(bullet -> bullet.render(gc));
        player.render(gc);
        enemies.forEach(enemy -> enemy.render(gc));
        bombTanks.forEach(bombTank -> bombTank.render(gc));

        gameMap.renderTopLayer(gc);
        gameMap.renderGraph(gc, allCharactersLocation());
    }

    /**
     *
     * @return temp1 the list with all characters' location inlcuding player.
     */
    public ArrayList<Point2D> allCharactersLocation() {
        //return (ArrayList<Point2D>) getCharacters().stream().map(Character::getPosition).collect(Collectors.toList());
        ArrayList<Point2D> temp1 = new ArrayList<>();
        temp1.add(player.getPosition());
        for (Enemy e1 :enemies){
            temp1.add(e1.getPosition());
        }
        return temp1;
    }


    @Override
    public boolean isWin(){
        return enemies.isEmpty()  && player.exists();
    }

    @Override
    public boolean isLoss(){
        return !player.exists();
    }

    /**
     * Clear all objects on the map
     */
    public void clear(){
        super.clear();
        gameMap.clearAll();
    }
}

package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Kamikaze;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import javafx.geometry.Point2D;

/**
 * Represents a controller for the survival game mode
 */
public class SurvivalController extends CenterController {

    /**
     * Constructs the controller by selecting a specific map and creating components
     */
    public SurvivalController(MapType mapType){
        super();
        gameMap = new GameMap(mapType);
        gameMap.initialGraph(new Point2D(16*32, 16*32));
        player = new Player(16*32, 16*32,gameMap);
        characters.add(player);
        initEnemies();
    }

    /**
     * Spawns all the initial enemies
     */
    private void initEnemies() {
        characters.add(new Kamikaze(16*28, 16*28));
        /*
        enemies.add(new DefaultEnemy(16*3, 16*3));
        enemies.add(new DefaultEnemy(16*61, 16*3));
        enemies.add(new DefaultEnemy(16*3, 16*61));
        enemies.add(new DefaultEnemy(16*61, 16*61));*/
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}

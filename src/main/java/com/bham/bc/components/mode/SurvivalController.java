package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.*;
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
        player = new Player(16*32, 16*32);
        gameMap.initialGraph(player);
        player.initNavigationService(gameMap.getGraph());
        characters.add(player);
    }

    /**
     * Spawns all the initial enemies
     */
    private void initEnemies() {
        characters.add(new Kamikaze(16*28, 16*28));
        characters.add(new Shooter(16*32, 16*28));
        characters.add(new Teaser(16*36, 16*28));
        characters.add(new Tank(16*28, 16*36));
        characters.add(new Trapper(16*32, 16*32));
    }

    @Override
    public void startGame() {
        initEnemies();
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}

package com.bham.bc.components;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.*;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.physics.MapDivision;
import javafx.geometry.Point2D;

import java.util.ArrayList;

/**
 * Represents a controller for the survival game mode
 */
public class SurvivalController extends CenterController {

    /**
     * Constructs the controller by selecting a specific map and preparing that map for the game session
     */
    public SurvivalController(){
        super();
    }

    @Override
    public void loadMap(MapType mapType) {
        gameMap = new GameMap(mapType);
    }

    /**
     * Spawns all the initial characters
     */
    private void initCharacters() {
        // Init players
        double playerX = gameMap.getHomeTerritory().getCenterX() - Player.SIZE/2.0;
        double playerY = gameMap.getHomeTerritory().getCenterY() - Player.SIZE;
        player = new Player(playerX, playerY);
        characters.add(player);

        // Temp: init enemies, later, we will initialize director AI which will spawn enemies automatically
//        characters.add(new Shooter(16*26, 16*26));
        characters.add(new Kamikaze(16*61, 16*4));
        //characters.add(new Kamikaze(16*26, 16*26));
        //characters.add(new Teaser(16*36, 16*28));
        //characters.add(new Tank(16*28, 16*36));
        //characters.add(new Trapper(16*32, 16*32));

        characters.add(new Splitter(16*4, 16*4));
        characters.add(new Shooter(16*6, 16*4));
        characters.add(new Kamikaze(16*61, 16*4));
        characters.add(new Teaser(16*61, 16*61));
        characters.add(new Tank(16*4, 16*61));
    }

    /**
     * Once all the entities are initialized, they can be added to map division which will handle collision checks much faster
     */
    private void initDivision() {
        mapDivision = new MapDivision<>(GameMap.getWidth(), GameMap.getHeight(), GameMap.getNumTilesX(), GameMap.getNumTilesY(), 50);
        mapDivision.addEntities(new ArrayList<>(gameMap.getInteractiveObstacles()));
        mapDivision.addEntities(new ArrayList<>(characters));
    }

    @Override
    public void startGame() {
        initCharacters();
        initDivision();
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}

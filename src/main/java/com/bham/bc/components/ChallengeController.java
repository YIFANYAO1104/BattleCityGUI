package com.bham.bc.components;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.*;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;

/**
 * Represents a controller for the survival game mode
 */
public class ChallengeController extends CenterController {

    /**
     * Constructs the controller by selecting an empty map and creating components
     */
    public ChallengeController(){
        super();

    }

    @Override
    public void loadMap(MapType mapType) {
        gameMap = new GameMap(mapType);
    }

    @Override
    public void startGame() {
        player = new Player(16*32, 16*32);
        characters.add(player);
        characters.add(new Follower1(16*26, 16*26));
//        characters.add(new Wanderer1(16*26, 16*26));
    }

//    @Override
//    public void update() { player.update(); }
//
//    @Override
//    public void render(GraphicsContext gc) { player.render(gc); }
}

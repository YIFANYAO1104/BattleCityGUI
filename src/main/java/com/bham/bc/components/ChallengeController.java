package com.bham.bc.components;

import com.bham.bc.components.CenterController;
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
    public ChallengeController(MapType mapType){
        super();
        player = new Player(16*32, 16*32);

        gameMap = new GameMap(mapType);
        characters.add(player);
    }

    @Override
    public void startGame() {
        characters.add(new Follower1(16*26, 16*26));
//        characters.add(new Wanderer1(16*26, 16*26));
    }

//    @Override
//    public void update() { player.update(); }
//
//    @Override
//    public void render(GraphicsContext gc) { player.render(gc); }
}

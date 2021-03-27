package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a controller for the survival game mode
 */
public class ChallengeController extends CenterController {

    /**
     * Constructs the controller by selecting an empty map and creating components
     */
    public ChallengeController(MapType mapType){
        super();
        gameMap = new GameMap(mapType);
        player = new Player(16*32, 16*32);
        gameMap.initialGraph(player);
    }

//    @Override
//    public void update() { player.update(); }
//
//    @Override
//    public void render(GraphicsContext gc) { player.render(gc); }
}

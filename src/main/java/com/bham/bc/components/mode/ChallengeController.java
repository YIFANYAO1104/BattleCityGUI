package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.characters.Player;
import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a controller for the survival game mode
 */
public class ChallengeController extends CenterController {

    /**
     * Constructs the controller by selecting an empty map and creating components
     */
    public ChallengeController(){
        super();
        player = new Player(16*32, 16*32);
    }

    @Override
    public void update() { player.update(); }

    @Override
    public void render(GraphicsContext gc) { player.render(gc); }
}

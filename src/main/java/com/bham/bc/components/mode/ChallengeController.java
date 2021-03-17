package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.entity.physics.BombTank;
import javafx.scene.canvas.GraphicsContext;

public class ChallengeController extends CenterController {

    @Override
    public boolean isWin() { return false; }

    @Override
    public boolean isLoss() { return false; }

    public ChallengeController(){
        super();
        player = new Player(16*32, 16*32);
    }

    @Override
    public void update() {
        player.update();
    }

    @Override
    public void render(GraphicsContext gc) {
        player.render(gc);
    }
}

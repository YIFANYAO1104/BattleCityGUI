package com.bham.bc.components.mode;

import com.bham.bc.components.CenterController;
import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.DIRECTION;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.utils.graph.SparseGraph;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;


import java.util.ArrayList;

import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_removeSoft;

/**
 * Represents a controller for the survival game mode
 */
public class SurvivalController extends CenterController {


    private SparseGraph sg;

    /**
     * Constructs the controller by selecting a specific map and creating components
     */
    public SurvivalController(MapType mapType){
        super();
        gameMap = new GameMap(mapType);
        gameMap.initialGraph(new Point2D(16*32, 16*32));
        player = new Player(16*32, 16*32,gameMap);
        initEnemies();
    }

    /**
     * Spawns all the initial enemies
     */
    private void initEnemies() {
        enemies.add(new Enemy(16*3, 16*3));
        enemies.add(new Enemy(16*61, 16*3));
        enemies.add(new Enemy(16*3, 16*61));
        enemies.add(new Enemy(16*61, 16*61));
    }

    @Override
    public boolean isGameOver() {
        return enemies.isEmpty()  && player.exists();
    }

    @Override
    public void clear(){
        super.clear();
        gameMap.clearAll();
    }
}

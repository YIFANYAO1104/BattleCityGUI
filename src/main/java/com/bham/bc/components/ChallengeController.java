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
public class ChallengeController extends CenterController {

    /**
     * Constructs the controller by selecting an empty map and creating components
     */
    public ChallengeController(){
        super();

    }

    @Override
    public void loadMap(MapType mapType) {
        gameMap = new GameMap(MapType.Map1);
    }

    @Override
    public void startGame() {
        player = new Player(16*32, 16*32);
        characters.add(player);
        //characters.add(new Follower1(16*26, 16*26));
//        characters.add(new Wanderer1(16*26, 16*26));

        characters.add(new TestEnemy(16*36, 16*26));
        TestEnemy e2 = new TestEnemy(16*26, 16*30);
        e2.setVelocity(new Point2D(2, -1));

        TestEnemy e3 = new TestEnemy(16*23, 16*36);
        e2.setVelocity(new Point2D(1, -3));

        TestEnemy e4 = new TestEnemy(16*30, 16*28);
        e4.setVelocity(new Point2D(1, 2));

        characters.add(e2);
        //characters.add(e3);characters.add(e4);

        initDivision();
    }


    private void initDivision() {
        mapDivision = new MapDivision<>(GameMap.getWidth(), GameMap.getHeight(), GameMap.getNumTilesX(), GameMap.getNumTilesY(), 50);
        mapDivision.addEntities(new ArrayList<>(gameMap.getInteractiveObstacles()));
        mapDivision.addEntities(new ArrayList<>(characters));
    }


//    @Override
//    public void update() { player.update(); }
//
//    @Override
//    public void render(GraphicsContext gc) { player.render(gc); }
}

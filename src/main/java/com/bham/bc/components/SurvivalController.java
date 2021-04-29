package com.bham.bc.components;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.enemies.*;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.physics.MapDivision;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a controller for the survival game mode
 */
public class SurvivalController extends Controller {

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
        //characters.add(new Kamikaze(16*61, 16*4));
        //characters.add(new Kamikaze(16*26, 16*26));
        //characters.add(new Teaser(16*36, 16*28));
        //characters.add(new Tank(16*28, 16*36));
        //characters.add(new Trapper(16*32, 16*32));

        //characters.add(new Splitter(16*4, 16*4));
        //characters.add(new Shooter(16*6, 16*4));
        //characters.add(new Kamikaze(16*61, 16*4));
        //characters.add(new Teaser(16*61, 16*61));
        //characters.add(new Tank(16*4, 16*61));
    }

    /**
     * Once all the entities are initialized, they can be added to map division which will handle collision checks much faster
     * and Intial the second step of graph system here
     */
    private void initDivisionAndGraph2() {
//        mapDivision = new MapDivision<>(GameMap.getWidth(), GameMap.getHeight(), GameMap.getNumTilesX(), GameMap.getNumTilesY(), 50);
        mapDivision = new MapDivision<>(GameMap.getWidth(), GameMap.getHeight(),10,10, 200);
        mapDivision.addObstacles(new ArrayList<>(gameMap.getInteractiveObstacles()));
//        mapDivision.addEntities(new ArrayList<>(gameMap.getInteractiveObstacles()));

        mapDivision.addEntities(new ArrayList<>(characters));

        /**
         * It is wired initial graph here
         */
        SparseGraph graphSystem =  gameMap.getGraph();

        ArrayList<Point2D> allNodesLocations = graphSystem.getAllVector(); //get all nodes location
//        System.out.println("start");
        double percen = 1 / (double) allNodesLocations.size() * 0.4;
        for (int index = 0; index < allNodesLocations.size(); index++) { //remove invalid nodes
            Point2D vv1 = allNodesLocations.get(index);
            double maxCharacterRadius = Math.sqrt((GameCharacter.MAX_SIZE/2.0)*(GameCharacter.MAX_SIZE/2.0));

            List<BaseGameEntity> list1 = mapDivision.calculateNeighborsArray(vv1,maxCharacterRadius);

            for (BaseGameEntity b1 : list1){
                try{
                    Obstacle o1 = (Obstacle) b1;
                    o1.interacts(graphSystem.getID(), index, new Rectangle(
                            vv1.getX()-maxCharacterRadius,vv1.getY()-maxCharacterRadius,maxCharacterRadius* 2,maxCharacterRadius * 2));
                }catch (Exception e){}

            }
            graphSystem.setRealContrustPercentage(percen + graphSystem.getRealContrustPercentage());
//            System.out.println(graphSystem.getRealContrustPercentage());

        }
//        System.out.println("over");
    }

    @Override
    public void startGame() {
        initCharacters();
        initDivisionAndGraph2();
    }

    @Override
    public void spawnEnemyRandomly(EnemyType enemyType) {
        Circle randomEnemyArea = getEnemyAreas()[new Random().nextInt(getEnemyAreas().length)];
        Point2D spawnPoint = GeometryEnhanced.randomPointInCircle(randomEnemyArea).subtract(GameCharacter.MAX_SIZE*.5, GameCharacter.MAX_SIZE*.5);

        try {
            addCharacter(enemyType.newInstance(spawnPoint.getX(), spawnPoint.getY()));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
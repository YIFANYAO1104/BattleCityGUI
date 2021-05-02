package com.bham.bc.components;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.characters.enemies.*;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import com.bham.bc.entity.physics.MapDivision;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a controller for the survival game mode
 */
public class SurvivalController extends Controller {
    public static final double MAX_HOME_HP = 10_000;
    public static final double MAX_HOME_DAMAGE = 5;

    /**
     * Constructs the controller by selecting a specific map and preparing that map for the game session
     */
    public SurvivalController() {
        super();
    }

    /**
     * Loads the graph of the map
     *
     * <p>This method goes through every node and with the help of {@link MapDivision} initializes them based on whether there is an
     * obstacle or not. To be more precise, it removes the nodes which are invalid/covered by obstacles.</p>
     */
    private void loadGraph() {
        SparseGraph<NavNode, GraphEdge> graphSystem =  gameMap.getGraph();
        ArrayList<Point2D> allNodePositions = graphSystem.getAllVector();
        double percent = .4 / allNodePositions.size();

        for (int i = 0; i < allNodePositions.size(); i++) {
            Point2D nodePosition = allNodePositions.get(i);
            double maxCharacterRadius = Math.hypot(GameCharacter.MAX_SIZE/2.0, GameCharacter.MAX_SIZE/2.0);
            List<BaseGameEntity> surroundingEntities = mapDivision.getIntersectedEntities(nodePosition, maxCharacterRadius);

            for(BaseGameEntity entity : surroundingEntities) {
                if(entity instanceof Obstacle) {
                    ((Obstacle) entity).interacts(graphSystem.getID(), i, new Rectangle(nodePosition.getX()-maxCharacterRadius, nodePosition.getY()-maxCharacterRadius, maxCharacterRadius*2, maxCharacterRadius*2));
                }
            }

            graphSystem.setRealContrustPercentage(percent + graphSystem.getRealContrustPercentage());
        }
    }

    @Override
    public void loadGame(MapType mapType) {
        gameMap = new GameMap(mapType);

        double playerX = gameMap.getHomeTerritory().getCenterX() - Player.SIZE/2.0;
        double playerY = gameMap.getHomeTerritory().getCenterY() - Player.SIZE;
        characters.add(new Player(playerX, playerY));
//        characters.add(new Neuron(16*60, 16*60));
        characters.add(new Neuron(16*60, 16*60));

        mapDivision = new MapDivision<>(GameMap.getWidth(), GameMap.getHeight(), 10, 10);
        mapDivision.addCrossZoneEntities(new ArrayList<>(gameMap.getInteractiveObstacles()));
        mapDivision.addCrossZoneEntities(new ArrayList<>(interactiveTriggers));
        mapDivision.addEntities(new ArrayList<>(characters));

        loadGraph();
    }

    // CALCULATIONS -----------------------------------------------
    @Override
    public Point2D getClosestCenter(Point2D position, ItemType item) {
        Stream<Point2D> closestPoints;

        switch(item) {
            case ALLY:
                closestPoints = characters.stream().filter(c -> c.getSide() == Side.ALLY).map(GameCharacter::getCenterPosition);
                break;
            case SOFT:
                Stream<BaseGameEntity> obstacles = mapDivision.getIntersectedEntities(position, 90).stream().filter(entity -> entity instanceof Obstacle);
                closestPoints = obstacles.filter(entity -> ((Obstacle) entity).getAttributes().contains(Attribute.BREAKABLE)).map(BaseGameEntity::getCenterPosition);
                break;
            case ENEMY_AREA:
                closestPoints = Arrays.stream(gameMap.getEnemySpawnAreas()).map(circle -> new Point2D(circle.getCenterX(), circle.getCenterY()));
                break;
            case HOME:
                return new Point2D(gameMap.getHomeTerritory().getCenterX(), gameMap.getHomeTerritory().getCenterY());
            default:
                return position;
        }

        return closestPoints.min(Comparator.comparing(point -> point.distance(position))).orElse(position);
    }

    public Point2D getFreeArea(Point2D pivot, double pivotRadius, double areaRadius) {
        return null;
    }

    @Override
    public boolean intersectsObstacles(Rectangle path) {
        return gameMap.getInteractiveObstacles().stream().anyMatch(o -> o.intersects(path));
    }
    // ------------------------------------------------------------

    // LOGIC ------------------------------------------------------
    public void changeScore(double score) {
        this.score = Math.max(0, this.score + score);
    }

    @Override
    public void occupyHome(Enemy enemy) {
        if(enemy.intersects(gameMap.getHomeTerritory())) {
            homeHp -= SurvivalController.MAX_HOME_DAMAGE;
        }
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
    //-------------------------------------------------------------
}
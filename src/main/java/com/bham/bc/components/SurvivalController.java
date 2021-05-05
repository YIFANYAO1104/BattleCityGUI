package com.bham.bc.components;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.characters.enemies.*;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.triggers.TriggerType;
import com.bham.bc.components.triggers.powerups.HealthGiver;
import com.bham.bc.components.triggers.powerups.Immune;
import com.bham.bc.components.triggers.powerups.SpeedTrigger;
import com.bham.bc.components.triggers.powerups.TripleBullet;
import com.bham.bc.components.triggers.traps.Freeze;
import com.bham.bc.components.triggers.traps.InverseTrap;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import com.bham.bc.entity.physics.MapDivision;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
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

        mapDivision = new MapDivision<>(GameMap.getWidth(), GameMap.getHeight(), 10, 10);
        mapDivision.addCrossZoneEntities(new ArrayList<>(gameMap.getInteractiveObstacles()));
        mapDivision.addCrossZoneEntities(new ArrayList<>(triggers));
        mapDivision.addEntities(new ArrayList<>(characters));

        loadGraph();

        //spawnEnemyRandomly(EnemyType.KAMIKAZE);
        // spawnEnemyRandomly(EnemyType.TEASER);
        // spawnEnemyRandomly(EnemyType.SHOOTER);spawnEnemyRandomly(EnemyType.SHOOTER);
        //spawnEnemyRandomly(EnemyType.TANK);

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

    @Override
    public GameCharacter getClosestALLY(Point2D position){
        GameCharacter gc = null;
        double min = Double.MAX_VALUE;

        for (GameCharacter character : characters) {
            if (character.getSide() == Side.ALLY && character.getCenterPosition().distance(position)<min){
                gc = character;
                min = character.getCenterPosition().distance(position);
            }
        }
        if (gc == null){
            System.out.println("Could not find target even if with node recording");
        }
        return gc;
    }

    @Override
    public Point2D getFreeArea(Point2D center, double innerRadius, double outerRadius, double areaSize, Pos pos) {
        Circle innerArea = new Circle(center.getX(), center.getY(), innerRadius);
        List<BaseGameEntity> entities = mapDivision.getIntersectedEntities(center, outerRadius);
        List<Obstacle> obstacles = entities.stream().filter(e -> e instanceof Obstacle && !e.intersects(innerArea)).map(e -> (Obstacle) e).collect(Collectors.toList());

        for(int i = 0; i < 50; i++) {
            double areaRadius = Math.hypot(areaSize/2, areaSize/2);
            Point2D freeCenter = GeometryEnhanced.randomPointInCircle(center, outerRadius);
            Circle freeArea = new Circle(freeCenter.getX(), freeCenter.getY(), areaRadius);

            if(!innerArea.intersects(freeArea.getBoundsInLocal()) && obstacles.stream().noneMatch(o -> o.intersects(freeArea))) {
                if(pos == Pos.TOP_LEFT) {
                    return freeCenter.subtract(new Point2D(areaSize/2, areaSize/2));
                } else {
                    return freeCenter;
                }
            }
        }

        return null;
    }

    @Override
    public boolean intersectsObstacles(Rectangle path) {
        return gameMap.getInteractiveObstacles().stream().anyMatch(o -> o.intersects(path));
    }
    // ------------------------------------------------------------

    // LOGIC ------------------------------------------------------
    @Override
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

    @Override
    public boolean spawnTriggerAroundPoint(TriggerType triggerType, Point2D center, double innerConstraint, double outerConstraint) {
        Point2D spawnPoint = getFreeArea(center, innerConstraint, outerConstraint, 32, Pos.TOP_LEFT);
        if(spawnPoint == null) return false;

        switch (triggerType) {
            case HEALTH_GIVER:
                addTrigger(new HealthGiver((int) spawnPoint.getX(), (int) spawnPoint.getY(), 20, 0));
                break;
            case IMMUNE:
                addTrigger(new Immune((int) spawnPoint.getX(), (int) spawnPoint.getY(), 20, 0));
                break;
            case TRIPLE_BULLETS:
                addTrigger(new TripleBullet((int) spawnPoint.getX(), (int) spawnPoint.getY(), 20, 0));
                break;
            case FREEZE:
                addTrigger(new Freeze((int) spawnPoint.getX(), (int) spawnPoint.getY(), 5, 0));
                break;
            case INVERSE_TRAP:
                addTrigger(new InverseTrap((int) spawnPoint.getX(), (int) spawnPoint.getY(), 10, 0));
        }

        return true;
    }
    //-------------------------------------------------------------
}
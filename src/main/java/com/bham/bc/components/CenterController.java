package com.bham.bc.components;

import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import com.bham.bc.components.triggers.Trigger;

import com.bham.bc.entity.physics.MapDivision;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.GameCharacter;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class defining the common elements and behavior for both survival and challenge controllers
 */
public abstract class CenterController extends BaseGameEntity implements FrontendServices, BackendServices {

    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    protected boolean isGameOver;
    protected double homeHp;

    protected GameMap gameMap;
    protected ArrayList<Trigger> triggers;
    protected ArrayList<Bullet> bullets;
    protected ArrayList<GameCharacter> characters;

    protected MapDivision<BaseGameEntity> mapDivision;

    //temp
    protected Player player;


    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public CenterController() {
        super(GetNextValidID(),-1,-1);
        triggers = new ArrayList<>();
        bullets = new ArrayList<>();
        characters = new ArrayList<>();
        homeHp = 1000;
    }

    /**
     * Sets the mode of the game the user has chosen
     * @param mode SURVIVAL or CHALLENGE value to be passed as a game mode
     * @param mapType layout of map that will be used by a specific mode
     */
    public static void setMode(Mode mode, MapType mapType) {
        CenterController centerController = null;
        switch (mode) {
            case SURVIVAL:
                centerController = new SurvivalController();
                break;
            case CHALLENGE:
                centerController = new ChallengeController();
                break;
        }
        frontendServices = centerController;
        backendServices = centerController;

        centerController.loadMap(mapType);
        centerController.startGame();
    }

    // TEMPORARY METHODS -------------------------------------------
    public abstract void loadMap(MapType mapType);
    public abstract void startGame();

    public void occupyHome(Enemy enemy) {
        if(enemy.intersects(gameMap.getHomeTerritory())) {
            homeHp -= 1;
            System.out.println("Home HP: " + homeHp);
        }
    }

    public Circle[] getEnemyAreas() {
        return gameMap.getEnemySpawnAreas();
    }

    public Circle getHomeArea() {
        return gameMap.getHomeTerritory();
    }

    @Override
    public Point2D getClosestCenter(Point2D position, ItemType item) {
        Stream<Point2D> closestPoints;

        switch(item) {
            case ALLY:
                closestPoints = characters.stream().filter(c -> c.getSide() == Side.ALLY).map(GameCharacter::getCenterPosition);
                break;
            case SOFT:
                Stream<BaseGameEntity> obstacles = mapDivision.calculateNeighborsArray(position, 50).stream().filter(entity -> entity instanceof Obstacle);
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
    public Point2D getFreeArea(Point2D pivot, double pivotRadius, double areaRadius) {
        return null;
    }

    public ArrayList<BaseGameEntity> allInfoCharacter(){
        return new ArrayList<>(characters);
    }

    @Override
    public boolean intersectsObstacles(Rectangle path) {
        return gameMap.getInteractiveObstacles().stream().anyMatch(o -> o.intersects(path));
    }

    @Override
    public void renderHitBoxes(AnchorPane hitBoxPane) {
        hitBoxPane.getChildren().clear();

        // Add bullet hit-boxes
        bullets.forEach(b -> {
            Shape bulletHitBox = b.getHitBox();
            bulletHitBox.setFill(Color.TRANSPARENT);
            bulletHitBox.setStroke(Color.RED);
            bulletHitBox.setStrokeWidth(1);
            hitBoxPane.getChildren().add(bulletHitBox);
        });

        // Add character hit-boxes
        characters.forEach(c -> {
            Shape cHitBox = c.getHitBox();
            cHitBox.setFill(Color.TRANSPARENT);
            cHitBox.setStroke(Color.RED);
            cHitBox.setStrokeWidth(1);
            hitBoxPane.getChildren().add(cHitBox);
        });


        characters.forEach(c -> {
            List<Shape> smoothingBoxes = c.getSmoothingBoxes();
            for (Shape smoothingBox : smoothingBoxes) {
                smoothingBox.setFill(Color.TRANSPARENT);
                smoothingBox.setStroke(Color.GREEN);
                smoothingBox.setStrokeWidth(1);
                hitBoxPane.getChildren().add(smoothingBox);
            }
        });
    }
    // ------------------------------------------------------------


    // CHECKERS ---------------------------------------------------
    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public double getPlayerHP() {
        return player.getHp();
    }
    // ------------------------------------------------------------

    // UI ---------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }
    // ------------------------------------------------------------

    // ADDERS -----------------------------------------------------
    @Override
    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
        mapDivision.addEntity(bullet);
    }

    @Override
    public void addCharacter(GameCharacter character) {
        characters.add(character);
        mapDivision.addEntity(character);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }
    // ------------------------------------------------------------

    // GETTERS ----------------------------------------------------
    @Override
    public SparseGraph<NavNode, GraphEdge> getGraph() {
        return gameMap.getGraph();
    }

    @Override
    public Point2D getPlayerCenterPosition() {
        return player.getCenterPosition();
    }

    @Override
    public ArrayList<Point2D> allCharacterPositions() {
        return (ArrayList<Point2D>) characters.stream().map(GameCharacter::getPosition).collect(Collectors.toList());
    }
    // ------------------------------------------------------------

    // OTHER ------------------------------------------------------
    @Override
    public boolean canPass(Point2D start, Point2D end, Point2D radius, List<Shape> array) {
        double angle = end.subtract(start).angle(new Point2D(0,-1));
        //angle between vectors are [0,180), so we need add extra direction info
        if (end.subtract(start).getX()<0) angle = -angle;
        double dis = start.distance(end);

        Point2D center = start.midpoint(end);
        Point2D topLeft = center.subtract(radius.multiply(0.5)).subtract(0,dis/2);
        Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), radius.getX()+10, radius.getY()+dis+5);
        hitBox.getTransforms().add(new Rotate(angle, center.getX(),center.getY()));
        array.add(hitBox);

        return !intersectsObstacles(hitBox);
    }

    @Override
    public void update() {
        mapDivision.updateObstacles(new ArrayList<>(gameMap.getInteractiveObstacles()));
        gameMap.update();

        characters.forEach(GameCharacter::update);
        characters.forEach(character -> character.handle(mapDivision.calculateNeighborsArray(character,40)));

        bullets.forEach(Bullet::update);
        bullets.forEach(bullet -> bullet.handle(mapDivision.calculateNeighborsArray(bullet, bullet.getHitboxRadius())));

        triggers.forEach(Trigger::update);
        triggers.forEach(trigger -> trigger.handle(mapDivision.calculateNeighborsArray(trigger.getCenterPosition(), 40)));

        // Performed before removals
        bullets.forEach(b -> mapDivision.updateEntity(b));
        characters.forEach(c -> mapDivision.updateEntity(c));

        // Performed last
        bullets.removeIf(b -> !b.exists());
        characters.removeIf(c -> !c.exists());
        triggers.removeIf(t -> !t.exists());
    }

    @Override
    public void render(GraphicsContext gc) {
        gameMap.renderBottomLayer(gc);

        triggers.forEach(trigger -> trigger.render(gc));
        bullets.forEach(bullet -> bullet.render(gc));
        characters.forEach(character -> character.render(gc));

        // gameMap.renderTopLayer(gc);

        gameMap.renderGraph(gc, allInfoCharacter());
        gameMap.renderTerritories(gc);
    }

    @Override
    public void clear() {
        triggers.clear();
        characters.clear();
        bullets.clear();
        gameMap.clear();
    }
    // ------------------------------------------------------------

    // INHERITED --------------------------------------------------
    @Override
    public Rectangle getHitBox() { return null; }

    @Override
    public boolean intersects(BaseGameEntity b) { return false; }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Controller"; }
    // ------------------------------------------------------------
}

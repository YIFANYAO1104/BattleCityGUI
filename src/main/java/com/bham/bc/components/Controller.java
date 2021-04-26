package com.bham.bc.components;

import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.Attribute;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.shooting.LaserGun;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.algorithms.AlgorithmDriver;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.*;
import java.util.stream.Stream;

/**
 * Class defining the common elements and behavior for any controller
 */
public abstract class Controller extends BaseGameEntity implements Services {

    public static Services services;

    protected double homeHp;
    protected double score;
    protected GameMap gameMap;
    protected ArrayList<Trigger> triggers;
    protected ArrayList<Bullet> bullets;
    protected ArrayList<GameCharacter> characters;

    protected MapDivision<BaseGameEntity> mapDivision;
    protected AlgorithmDriver driver;

    //temp
    protected Player player;



    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public Controller() {
        super(getNextValidID(),-1,-1);
        triggers = new ArrayList<>();
        bullets = new ArrayList<>();
        characters = new ArrayList<>();
        driver = new AlgorithmDriver(1000);
        homeHp = 1000;
        score = 0;
    }

    /**
     * Sets the mode of the game the user has chosen
     * @param mapType layout of map that will be used
     */
    public static void setMode(MapType mapType) {
        Controller controller = new SurvivalController();
        services = controller;

        controller.loadMap(mapType);
        controller.startGame();
    }

    // TEMPORARY METHODS -------------------------------------------
    protected abstract void loadMap(MapType mapType);
    protected abstract void startGame();

    public void changeScore(double score) {
        this.score = Math.max(0, this.score + score);
    }

    public double getScore() {
        return score;
    }

    @Override
    public double getHomeHp() {
        return homeHp;
    }

    @Override
    public double getHomeHpFraction() {
        return Math.max(0, homeHp/1000);
    }

    public void occupyHome(Enemy enemy) {
        if(enemy.intersects(gameMap.getHomeTerritory())) {
            homeHp -= 1;
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
                Stream<BaseGameEntity> obstacles = mapDivision.calculateNeighborsArray(position, 90).stream().filter(entity -> entity instanceof Obstacle);
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
    public AlgorithmDriver getDriver() {
        return driver;
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
        driver.runAlgorithm();
        mapDivision.updateObstacles(new ArrayList<>(gameMap.getInteractiveObstacles()));
        gameMap.update();

        characters.forEach(GameCharacter::update);
        characters.forEach(character -> character.handle(mapDivision.calculateNeighborsArray(character)));

        bullets.forEach(Bullet::update);
        bullets.forEach(bullet -> bullet.handle(mapDivision.calculateNeighborsArray(bullet)));

        triggers.forEach(Trigger::update);
        triggers.forEach(trigger -> trigger.handle(mapDivision.calculateNeighborsArray(trigger.getCenterPosition(), trigger.getHitBoxRadius() * 4)));

        // Performed before removals
        bullets.forEach(b -> mapDivision.updateMovingEntity(b));
        characters.forEach(c -> mapDivision.updateMovingEntity(c));

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

        // TODO: remove
        triggers.forEach(trigger -> trigger.renderHitBox(gc));
        bullets.forEach(bullet -> bullet.renderHitBox(gc));
        characters.forEach(character -> character.renderHitBox(gc));


        gameMap.renderTopLayer(gc);

        gameMap.renderGraph(gc, new ArrayList<>(characters));
        mapDivision.render(gc);
//        System.out.println(mapDivision.sizeOfCells());
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
    public Rectangle getHitBox() {
        return null;
    }

    @Override
    public double getHitBoxRadius() {
        return 0;
    }

    @Override
    public boolean intersects(BaseGameEntity b) {
        return false;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return "Controller";
    }
    // ------------------------------------------------------------
}

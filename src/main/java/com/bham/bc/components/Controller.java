package com.bham.bc.components;

import com.bham.bc.components.shooting.Bullet;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.components.triggers.effects.HitMarker;
import com.bham.bc.components.triggers.effects.RingExplosion;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.director.Director;
import com.bham.bc.entity.ai.navigation.AlgorithmDriver;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class defining the common elements and behavior for any controller
 */
public abstract class Controller extends BaseGameEntity implements Services {

    public static Services services;

    protected double homeFullHp;
    protected double homeHp;
    protected double score;
    protected GameMap gameMap;
    protected ArrayList<Trigger> triggers;
    protected ArrayList<Bullet> bullets;
    protected ArrayList<GameCharacter> characters;
    protected Director director;

    protected MapDivision<BaseGameEntity> mapDivision;
    protected AlgorithmDriver driver;

    int time = 50;
    Point2D myP = new Point2D(0, 0);

    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public Controller() {
        super(getNextValidID(),-1,-1);
        triggers = new ArrayList<>();
        bullets = new ArrayList<>();
        characters = new ArrayList<>();
        director = new Director();
        driver = new AlgorithmDriver(500);
        homeHp = 10000;
        homeFullHp = 10000;
        score = 0;
    }

    /**
     * Sets the mode of the game the user has chosen
     * @param mapType layout of map that will be used
     */
    public static void setMode(MapType mapType) {
        Controller controller = new SurvivalController();
        services = controller;

        controller.loadGame(mapType);
    }

    /**
     * Loads and starts the game
     *
     * <p>Loads the game by providing the type of map to load. It initializes the player and any additional tools,
     * such map division and graph.</p>
     *
     * @param mapType type of map the game will be played on
     */
    protected abstract void loadGame(MapType mapType);


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
    public MapDivision<BaseGameEntity> getMapDivision() {
        return mapDivision;
    }

    @Override
    public AlgorithmDriver getDriver() {
        return driver;
    }


    @Override
    public ArrayList<GameCharacter> getCharacters() {
        return characters;
    }

    @Override
    public ArrayList<GameCharacter> getCharacters(Side side) {
        return characters.stream().filter(c -> c.getSide() == side).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Player getPlayer() {
        return (Player) characters.stream().filter(c ->  c instanceof Player ).findFirst().orElse(null);
    }

    public Circle[] getEnemyAreas() {
        return gameMap.getEnemySpawnAreas();
    }

    public Circle getHomeArea() {
        return gameMap.getHomeTerritory();
    }

    public ArrayList<Bullet> getBullets(){
    	return bullets;
    }
    // ------------------------------------------------------------



    // FRAME ITERATIONS -------------------------------------------
    @Override
    public void update() {
        driver.runAlgorithm();
//        mapDivision.updateObstacles(new ArrayList<>(gameMap.getInteractiveObstacles()));
        gameMap.update();

        director.update();

        characters.forEach(GameCharacter::update);
        characters.forEach(character -> character.handle(mapDivision.getRelevantEntities(character)));

        bullets.forEach(Bullet::update);
        bullets.forEach(bullet -> bullet.handle(mapDivision.getIntersectedEntities(bullet.getCenterPosition(),150)));

        triggers.forEach(Trigger::update);
        //Moving entity has one zone only. But in fact they are cross zone.
        // We have no time to fix this. So we use bigger hitbox.
        //by this, more entity could be tested, the biggest enemy size is 150 so far.
        //for bullet, it's also like that
        triggers.stream().filter(t -> !(t instanceof Dissolve) && !(t instanceof HitMarker) && !(t instanceof RingExplosion)).collect(Collectors.toList())
                .stream().forEach(t -> t.handle(mapDivision.getIntersectedEntities(t.getCenterPosition(),150)));

        // Performed before removals
        bullets.forEach(b -> mapDivision.updateMovingEntityZone(b));
        characters.forEach(c -> mapDivision.updateMovingEntityZone(c));
        mapDivision.cleanNonExistingEntities();

        // Performed last
        bullets.removeIf(b -> !b.exists());
        characters.removeIf(c -> !c.exists());
        triggers.removeIf(t -> !t.exists());
    }

    @Override
    public void render(GraphicsContext gc) {
        gameMap.renderBottomLayer(gc);

        bullets.forEach(bullet -> bullet.render(gc));
        characters.forEach(character -> character.render(gc));
        triggers.forEach(trigger -> trigger.render(gc));

        // TODO: remove
        triggers.forEach(trigger -> trigger.renderHitBox(gc));
        // bullets.forEach(bullet -> bullet.renderHitBox(gc));
        // characters.forEach(character -> character.renderHitBox(gc));

        // gameMap.renderTopLayer(gc);

        // gameMap.renderGraph(gc, characters);
        // gameMap.renderTerritories(gc);
        // mapDivision.render(gc);
    }

    @Override
    public void clear() {
        triggers.clear();
        characters.clear();
        bullets.clear();
        gameMap.clear();
    }
    //-------------------------------------------------------------



    // UI ---------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        if(getPlayer() != null) {
            getPlayer().keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(getPlayer() != null) {
            getPlayer().keyReleased(e);
        }
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public double getHomeHpFraction() {
        return homeHp / homeFullHp;
    }

    @Override
    public double getPlayerHpFraction() {
        return getPlayer() == null ? 0 : getPlayer().getHp() / getPlayer().getFullHp();
    }

    @Override
    public boolean gameOver() {
        return getHomeHpFraction() <= 0 || getPlayer() == null;
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

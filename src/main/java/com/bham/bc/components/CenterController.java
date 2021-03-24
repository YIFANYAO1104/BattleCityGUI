package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.GameCharacter;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Class defining the common elements and behavior for both survival and challenge controllers
 */
public abstract class CenterController extends BaseGameEntity implements FrontendServices, BackendServices {

    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    protected boolean isGameOver;
    protected GameMap gameMap;
    protected Player player;                //TODO: remove as it is the 0th objcet in characters list
    protected ArrayList<Bullet> bullets;
    protected ArrayList<Trigger> triggers;
    protected ArrayList<GameCharacter> characters;

    //temp
    protected ArrayList<BombTank> bombTanks = new ArrayList<>();


    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public CenterController() {
        super(GetNextValidID(),-1,-1);
        bullets = new ArrayList<>();
        triggers = new ArrayList<>();
        characters = new ArrayList<>();
        // TODO: add empty map by default
    }

    /**
     * Sets the mode of the game the user has chosen
     * @param mode SURVIVAL or CHALLENGE value to be passed as a game mode
     * @param mapType layout of map that will be used by a specific mode
     */
    public static void setMode(MODE mode, MapType mapType) {
        CenterController centerController = null;
        switch (mode) {
            case SURVIVAL:
                centerController = new SurvivalController(mapType);

                break;
            case CHALLENGE:
                centerController = new ChallengeController(mapType);
                break;
        }
        frontendServices = centerController;
        backendServices = centerController;
    }

    // TEMPORARY METHODS -------------------------------------------
    @Override
    public void testAStar() {
        player.createNewRequestAStar();
    }

    @Override
    public void testDjistra() {
        player.createNewRequestItem();
    }

    @Override
    public void addBombTank(BombTank b) {
        bombTanks.add(b);
    }

    @Override
    public boolean intersectsObstacles(Shape hitBox) {
        return gameMap.intersectsObstacles(hitBox);
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
    }

    @Override
    public void addCharacter(GameCharacter character) {
        characters.add(character);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        gameMap.addTrigger(trigger);
    }
    // ------------------------------------------------------------

    // GETTERS ----------------------------------------------------
    @Override
    public SparseGraph getGraph() {
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
    public void update() {
        gameMap.update();
        characters.forEach(GameCharacter::update);
        bullets.forEach(Bullet::update);

        gameMap.handleAll(characters, bullets);
        characters.forEach(character -> character.handleAll(characters, bullets));

        bullets.removeIf(b -> !b.exists());
        characters.removeIf(c -> !c.exists());
    }

    @Override
    public void render(GraphicsContext gc) {
        gameMap.renderBottomLayer(gc);

//        bombTanks.forEach(b -> render(gc)); // TEMP
        for (BombTank bombTank : bombTanks) {
            bombTank.render(gc);
        }

        bullets.forEach(bullet -> bullet.render(gc));
        characters.forEach(character -> character.render(gc));

        gameMap.renderTopLayer(gc);
        gameMap.renderGraph(gc, allCharacterPositions());
    }

    @Override
    public void clear(){
        characters.clear();
        bullets.clear();
        gameMap.clearAll();
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

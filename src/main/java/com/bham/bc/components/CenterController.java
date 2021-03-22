package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.triggers.Trigger;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.physics.BombTank;
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
    protected Player player;
    protected ArrayList<BombTank> bombTanks;
    protected ArrayList<Trigger> triggers;
    protected ArrayList<Bullet> bullets;
    protected ArrayList<GameCharacter> characters;


    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public CenterController() {
        super(GetNextValidID(),-1,-1);
        bullets = new ArrayList<>();
        bombTanks = new ArrayList<>();
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
    public void renderHitBoxes(AnchorPane hitBoxPane) {
        hitBoxPane.getChildren().clear();

        // Add map hit-box
        Rectangle mapConstrain = new Rectangle(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, Color.TRANSPARENT);
        mapConstrain.setStroke(Color.RED);
        mapConstrain.setStrokeWidth(5);
        hitBoxPane.getChildren().add(mapConstrain);

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

            Shape enemyLine = c.getLine();
            enemyLine.setStroke(Color.RED);
            enemyLine.setStrokeWidth(1);
            hitBoxPane.getChildren().add(enemyLine);
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
    }

    @Override
    public void addCharacter(GameCharacter character) {
        characters.add(character);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        gameMap.addTrigget(trigger);
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
    // ------------------------------------------------------------

    // OTHER ------------------------------------------------------
    @Override
    public void update() {
        gameMap.update();
        player.update();
        characters.forEach(GameCharacter::update);
        bullets.forEach(Bullet::update);

        gameMap.handleAll(characters, bullets);
        player.handleAll(getCharacters(), bullets);
        characters.forEach(character -> character.handleAll(characters, bullets));

        bullets.removeIf(b -> !b.exists());
        characters.removeIf(c -> !c.exists());
    }

    @Override
    public void render(GraphicsContext gc) {
        gameMap.renderBottomLayer(gc);

        bullets.forEach(bullet -> bullet.render(gc));
        player.render(gc);
        characters.forEach(character -> character.render(gc));
        bombTanks.forEach(bombTank -> bombTank.render(gc));

        gameMap.renderTopLayer(gc);
        gameMap.renderGraph(gc, allCharactersLocation());
    }

    @Override
    public void clear(){
        characters.clear();
        bullets.clear();
        bombTanks.clear();
        gameMap.clearAll();
    }
    // ------------------------------------------------------------




    /**
     * Adding bombtank to bomb tank list
     * @param b
     */
    public void addBombTank(BombTank b){
        bombTanks.add(b);
    }

    public void addExplosiveTrigger(int x, int y) {gameMap.addBombTrigger(x,y);}

    public void testAStar() {
        player.createNewRequestAStar();
    }

    public void testDjistra() {
        player.createNewRequestItem();
    }



    /**
     * Gets all characters in the game
     * @return list of all the characters in the game
     */
    public ArrayList<GameCharacter> getCharacters() {
        //ArrayList<GameCharacter> gameCharacters = new ArrayList<>(enemies);
        //gameCharacters.add(player);

        return characters;
    }




    //-----------------------------------------
    // TODO: move to physics
    @Override
    public boolean intersectsObstacles(Shape hitBox) {
        return gameMap.intersectsObstacles(hitBox);
    }
    //------------------------------------------------------


    /**
     * Gets the positions of all the characters in the game
     * @return Point2D list with all character locations
     */
    public ArrayList<Point2D> allCharactersLocation() {
        //return (ArrayList<Point2D>) getCharacters().stream().map(Character::getPosition).collect(Collectors.toList());
        //ArrayList<Point2D> temp1 = new ArrayList<>();
        //temp1.add(player.getPosition());
        //for (Enemy e1 :enemies){
            //temp1.add(e1.getPosition());
        //}
        return (ArrayList<Point2D>) characters.stream().map(GameCharacter::getPosition).collect(Collectors.toList());
    }

    // INHERITED
    @Override
    public Rectangle getHitBox() { return null; }

    @Override
    public boolean intersects(BaseGameEntity b) { return false; }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Center Controller"; }
}

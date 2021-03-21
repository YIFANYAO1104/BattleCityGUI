package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Character;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

/**
 * Class defining the common elements and behavior for both survival and challenge controllers
 */
public abstract class CenterController extends BaseGameEntity implements FrontendServices, BackendServices {

    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    protected boolean isGameOver;
    protected GameMap gameMap;
    protected Player player;
    protected ArrayList<Enemy> enemies = new ArrayList<>();
    protected ArrayList<BombTank> bombTanks = new ArrayList<>();
    protected ArrayList<Bullet> bullets = new ArrayList<>();

    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public CenterController() { super(GetNextValidID(),-1,-1); }

    /**
     * Adding bombtank to bomb tank list
     * @param b
     */
    public void addBombTank(BombTank b){
        bombTanks.add(b);
    }

    public SparseGraph getNavigationGraph(){
        return gameMap.getGraph();
    }

    public void testAStar() {
        player.createNewRequestAStar();
    }

    public void testDjistra() {
        player.createNewRequestItem();
    }

    /**
     * Sets the mode of the game the user has chosen
     * @param mode SURVIVAL or CHALLENGE value to be passed as a game mode
     */
    public static void setMode(MODE mode, MapType mapType){
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

    /**
     * Gets all characters in the game
     * @return list of all the characters in the game
     */
    public ArrayList<Character> getCharacters() {
        ArrayList<Character> characters = new ArrayList<>(enemies);
        characters.add(player);

        return characters;
    }

    @Override
    public void update() {
        gameMap.update();
        player.update();
        enemies.forEach(Enemy::update);
        bullets.forEach(Bullet::update);

        gameMap.handleAll(player, enemies, bullets);
        player.handleAll(getCharacters(), bullets);
        enemies.forEach(enemy -> enemy.handleAll(getCharacters(), bullets));

        bullets.removeIf(b -> !b.exists());
        enemies.removeIf(e -> !e.exists());
    }

    @Override
    public void render(GraphicsContext gc) {
        gameMap.renderBottomLayer(gc);

        bullets.forEach(bullet -> bullet.render(gc));
        player.render(gc);
        enemies.forEach(enemy -> enemy.render(gc));
        bombTanks.forEach(bombTank -> bombTank.render(gc));

        gameMap.renderTopLayer(gc);
        gameMap.renderGraph(gc, allCharactersLocation());
    }

    /**
     * Gets the positions of all the characters in the game
     * @return Point2D list with all character locations
     */
    public ArrayList<Point2D> allCharactersLocation() {
        //return (ArrayList<Point2D>) getCharacters().stream().map(Character::getPosition).collect(Collectors.toList());
        ArrayList<Point2D> temp1 = new ArrayList<>();
        temp1.add(player.getPosition());
        for (Enemy e1 :enemies){
            temp1.add(e1.getPosition());
        }
        return temp1;
    }


    @Override
    public boolean isGameOver() { return isGameOver; }

    @Override
    public double getPlayerHP() { return player.getHp(); }

    @Override
    public void keyReleased(KeyEvent e) { player.keyReleased(e); }

    @Override
    public void keyPressed(KeyEvent e) { player.keyPressed(e); }


    @Override
    public void renderHitBoxes(AnchorPane hitBoxPane) {
        hitBoxPane.getChildren().clear();

        // Add map hit-box
        Rectangle mapConstrain = new Rectangle(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, Color.TRANSPARENT);
        mapConstrain.setStroke(Color.RED);
        mapConstrain.setStrokeWidth(5);
        hitBoxPane.getChildren().add(mapConstrain);

        // Add player hit-box
        Shape playerHitBox = player.getHitBox();
        playerHitBox.setFill(Color.TRANSPARENT);
        playerHitBox.setStroke(Color.RED);
        playerHitBox.setStrokeWidth(2);
        hitBoxPane.getChildren().add(playerHitBox);

        // Add bullet hit-boxes
        bullets.forEach(b -> {
            Shape bulletHitBox = b.getHitBox();
            bulletHitBox.setFill(Color.TRANSPARENT);
            bulletHitBox.setStroke(Color.RED);
            bulletHitBox.setStrokeWidth(1);
            hitBoxPane.getChildren().add(bulletHitBox);
        });

        // Add enemy hit-boxes
        enemies.forEach(e -> {
            Shape enemyHitBox = e.getHitBox();
            enemyHitBox.setFill(Color.TRANSPARENT);
            enemyHitBox.setStroke(Color.RED);
            enemyHitBox.setStrokeWidth(1);
            hitBoxPane.getChildren().add(enemyHitBox);
        });
    }


    @Override
    public void addEnemy(Enemy enemy) { enemies.add(enemy); }

    @Override
    public void clear(){
        enemies.clear();
        bullets.clear();
        bombTanks.clear();
    }

    @Override
    public void addBullet(Bullet bullet){ bullets.add(bullet); }

    @Override
    public Rectangle getHitBox() { return null; }

    @Override
    public boolean intersects(BaseGameEntity b) { return false; }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Center Controller"; }
}

package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.DefaultEnemy;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Character;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

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
     * Sets the mode of the game the user has chosen
     * @param mode SURVIVAL or CHALLENGE value to be passed as a game mode
     */
    public static void setMode(MODE mode){
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
    }

    //-----------------------------------------

    // TODO: move to physics
    @Override
    public boolean intersectsCharacters(Shape hitBox, List<Character> characters) {
        return characters.stream().anyMatch(c -> c.getHitBox().intersects(hitBox.getBoundsInLocal()));
    }

    // TODO: move to physics
    @Override
    public boolean intersectsObstacles(Shape hitBox) {
        return gameMap.intersectsObstacles(hitBox);
    }

    @Override
    public int getPlayerID() {
        return player.getID();
    }

    @Override
    public Point2D getPlayerCenterPosition() {
        return player.getCenterPosition();
    }

    @Override
    public Point2D getPlayerPosition() {
        return player.getPosition();
    }

    //------------------------------------------------------

    @Override
    public ArrayList<Character> getCharacters() {
        ArrayList<Character> characters = new ArrayList<>(enemies);
        characters.add(player);

        return characters;
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

        Shape playerHitBox2 = player.getImageHitbox();
        playerHitBox2.setFill(Color.TRANSPARENT);
        playerHitBox2.setStroke(Color.RED);
        playerHitBox2.setStrokeWidth(2);
        hitBoxPane.getChildren().add(playerHitBox2);

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

            Shape enemyLine = e.getLine();
            enemyLine.setStroke(Color.RED);
            enemyLine.setStrokeWidth(1);
            hitBoxPane.getChildren().add(enemyLine);
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

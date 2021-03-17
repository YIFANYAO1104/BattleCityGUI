package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.GenericObstacle;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.components.characters.TrackableCharacter;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.characters.Character;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

import static com.bham.bc.entity.EntityManager.entityManager;

public abstract class CenterController extends BaseGameEntity implements FrontendServices, BackendServices {

    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    protected Boolean win = false, lose = false;
    protected GameMap gameMap;
    protected Player player;
    protected ArrayList<Enemy> enemies = new ArrayList<>();
    protected ArrayList<BombTank> bombTanks = new ArrayList<>();
    protected ArrayList<Bullet> bullets = new ArrayList<>();

    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public CenterController(){ super(GetNextValidID(),-1,-1); }

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

    public ArrayList<Character> getCharacters() {
        ArrayList<Character> characters = new ArrayList<>(enemies);
        characters.add(player);

        return characters;
    }


    /**
     * Get the number of enemy tanks from enemy tank list
     * @return
     */
    public int getEnemyNumber(){ return enemies.size(); }

    @Override
    public int getLife() { return player.getHp(); }

    @Override
    public void keyReleased(KeyEvent e){ player.keyReleased(e); }

    @Override
    public void keyPressed(KeyEvent e){ player.keyPressed(e); }

    public Player getHomeTank(){
        return player;
    }

    @Override
    public void renderHitBoxes(AnchorPane hitBoxPane) {
        hitBoxPane.getChildren().clear();

        Rectangle mapConstrain = new Rectangle(Constants.MAP_WIDTH, Constants.MAP_HEIGHT, Color.TRANSPARENT);
        mapConstrain.setStroke(Color.RED);
        mapConstrain.setStrokeWidth(5);

        Shape playerHitBox = player.getHitBox();
        playerHitBox.setFill(Color.TRANSPARENT);
        playerHitBox.setStroke(Color.RED);
        playerHitBox.setStrokeWidth(2);

        hitBoxPane.getChildren().addAll(playerHitBox, mapConstrain);
    }


    /**
     * Clear all objects in the map
     */
    public void clear(){
        enemies.clear();
        bullets.clear();
        bombTanks.clear();
    }

    @Override
    public void addEnemy(Enemy enemy) { return; }

    @Override
    public void removeEnemy(Enemy enemy) {
        entityManager.removeEntity(enemy);
        enemies.remove(enemy);
    }

    @Override
    public void addBombTank(BombTank b){
        bombTanks.add(b);
    }

    @Override
    public void removeBombTank(BombTank b){
        bombTanks.remove(b);
    }

    @Override
    public Shape getPlayerHitBox() { return player.getHitBox(); }

    @Override
    public void addBullet(Bullet bullet){ bullets.add(bullet); }

    @Override
    public void removeBullet(Bullet bullet) {
        entityManager.removeEntity(bullet);
        bullets.remove(bullet);
    }

    @Override
    public Rectangle getHitBox() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() { return "Center Controller"; }

    @Override
    public boolean intersects(BaseGameEntity b) {
        return false;
    }
}

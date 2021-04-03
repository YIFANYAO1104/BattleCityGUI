package com.bham.bc.components;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.ChallengeController;
import com.bham.bc.components.mode.Mode;
import com.bham.bc.components.mode.SurvivalController;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.entity.triggers.Trigger;
import static com.bham.bc.utils.Constants.*;

import com.bham.bc.entity.triggers.TriggerSystem;
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
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class defining the common elements and behavior for both survival and challenge controllers
 */
public abstract class CenterController extends BaseGameEntity implements FrontendServices, BackendServices {

    public static FrontendServices frontendServices;
    public static BackendServices backendServices;

    protected boolean isGameOver;

    protected GameMap gameMap;
    protected TriggerSystem triggerSystem;
    protected ArrayList<Bullet> bullets;
    protected ArrayList<GameCharacter> characters;

    //temp
    protected ArrayList<BombTank> bombTanks = new ArrayList<>();
    protected Player player;


    /**
     * Constructs center controller as a {@link com.bham.bc.entity.BaseGameEntity} object
     */
    public CenterController() {
        super(GetNextValidID(),-1,-1);
        gameMap = new GameMap(MapType.EmptyMap);
        triggerSystem = new TriggerSystem();
        bullets = new ArrayList<>();
        characters = new ArrayList<>();
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
                centerController = new SurvivalController(mapType);
                break;
            case CHALLENGE:
                centerController = new ChallengeController(mapType);
                break;
        }
        frontendServices = centerController;
        backendServices = centerController;

        centerController.startGame();

        initialMapDivision(centerController);

    }

    public static void initialMapDivision(CenterController c1){
        //--Add all game elemnets to mapDivision------
        c1.gameMap.getMapDivision().addToMapDivision(
                new ArrayList<>(c1.characters));
        //-----------------------------------------------
    }

    // TEMPORARY METHODS -------------------------------------------
    public abstract void startGame();

    @Override
    public Point2D getMapCenterPosition() {
        return new Point2D(16*32, 16*32);
    }

    @Override
    public Point2D getNearestOppositeSideCenterPosition(Point2D point, Side side) {
        return characters.stream().filter(c -> c.getSide() != side).map(GameCharacter::getCenterPosition).min(Comparator.comparing(c -> c.distance(point))).get();
    }

    @Override
    public void addBombTank(BombTank b) {
        bombTanks.add(b);
    }

    @Override
    public boolean intersectsObstacles(Shape hitBox) {
        return gameMap.intersectsObstacles(hitBox);
    }

    @Override
    public void renderHitBoxes(AnchorPane hitBoxPane) {
        hitBoxPane.getChildren().clear();

        // Add map hit-box
        Rectangle mapConstrain = new Rectangle(MAP_WIDTH, MAP_HEIGHT, Color.TRANSPARENT);
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

            //Shape enemyLine = c.getLine();
            //enemyLine.setStroke(Color.RED);
            //enemyLine.setStrokeWidth(1);
            //hitBoxPane.getChildren().add(enemyLine);
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

//        List<Shape> smoothingBoxes = player.getSmoothingBoxes();
//        for (Shape smoothingBox : smoothingBoxes) {
//            smoothingBox.setFill(Color.TRANSPARENT);
//            smoothingBox.setStroke(Color.GREEN);
//            smoothingBox.setStrokeWidth(1);
//            hitBoxPane.getChildren().add(smoothingBox);
//        }

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
        gameMap.getMapDivision().AddEntity(bullet);
        bullets.add(bullet);
    }

    @Override
    public void addCharacter(GameCharacter character) {
        characters.add(character);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        triggerSystem.register(trigger);
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

    public ArrayList<BaseGameEntity> allInfoCharacter(){
        return new ArrayList<BaseGameEntity>(characters);
    }
    // ------------------------------------------------------------

    // OTHER ------------------------------------------------------
    @Override
    public boolean couldWalkThrough(Point2D start, Point2D end, Point2D radius, List<Shape> array){
        double angle = end.subtract(start).angle(new Point2D(0,-1));
        //angle between vectors are [0,180), so we need add extra direction info
        if (end.subtract(start).getX()<0) angle = -angle;
        double dis = start.distance(end);

        Point2D center = start.midpoint(end);
        Point2D topLeft = center.subtract(radius.multiply(0.5)).subtract(0,dis/2);
        Rectangle hitBox = new Rectangle(topLeft.getX(), topLeft.getY(), radius.getX()+10, radius.getY()+dis+5);
        hitBox.getTransforms().add(new Rotate(angle, center.getX(),center.getY()));
        array.add(hitBox);

        return !gameMap.intersectsObstacles(hitBox);
    }

    @Override
    public void update() {
        gameMap.update();
        characters.forEach(GameCharacter::update);
        // Update bullets
        for(Bullet b1 : bullets){
            b1.update();
            gameMap.getMapDivision().UpdateEntity(b1);       // Removed the not exist bullet stored in Mapdivision
        }

        characters.forEach(c1-> gameMap.getMapDivision().UpdateEntity(c1));     //Update characters
        gameMap.handleAll(characters, bullets);
//        characters.forEach(character -> character.handleAll(characters, bullets));
        characters.forEach(character -> character.handleAll(
                gameMap.getMapDivision().CalculateNeighborsArray(character,40)));

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

//        gameMap.renderTopLayer(gc);
        gameMap.renderGraph(gc, allInfoCharacter());
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

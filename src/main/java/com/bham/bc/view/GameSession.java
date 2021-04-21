package com.bham.bc.view;

import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;

import static com.bham.bc.components.Controller.*;

import com.bham.bc.view.menu.EndMenu;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

/**
 * Class managing the animations of a running game
 */
public class GameSession {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FRAME_RATE = 24;

    public static AnchorPane gamePane;
    private Scene gameScene;
    public static Stage gameStage;
    private GraphicsContext gc;

    private Stage menuStage;

    public static AnimationTimer gameTimer;

    private Camera camera;

    /**
     * Constructs the view manager
     */
    public GameSession(MapType mapType) {
        setMode(mapType);
        initializeStage();
        createKeyListeners();
    }

    /**
     * initializes the game stage
     */
    private void initializeStage() {
        Canvas canvas = new Canvas(GameMap.getWidth(), GameMap.getHeight());
        gc = canvas.getGraphicsContext2D();
        camera = new Camera(gc);

        gamePane = new AnchorPane(canvas);
        gameScene = new Scene(gamePane, WIDTH, HEIGHT);


        gameStage = new Stage();

        gameStage.setScene(gameScene);
        gameStage.setTitle("Defenders");
        gameStage.setResizable(false);

        try {
            gameScene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }



        CustomStage customStage=new CustomStage(gameStage,gameScene,gamePane);
        customStage.createTitleBar(gamePane, WIDTH, HEIGHT);
    }

    /**
     * starts the game loop and shows the game view
     * @param menuStage menu stage to be hidden
     */
    public void createNewGame(Stage menuStage) {
        this.menuStage = menuStage;
        this.menuStage.hide();

        createGameLoop();
        gameStage.show();
    }

    public void show() {
        gameStage.show();
    }

    /**
     * creates the input listeners. Key presses are handled by the center controller class.
     */
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(e -> {
            if(!EndMenu.isshown&&e.getCode() == KeyCode.Q) {  // Testing purposes
                gameTimer.stop();
                Random r=new Random();
                MenuSession.showEndMenu(gamePane, r.nextDouble()*999);
            } else if(!EndMenu.isshown && (e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE)) {
                showPauseMenu();
            } else {
                services.keyPressed(e);
            }
        });
        gameScene.setOnKeyReleased(e -> services.keyReleased(e));
    }


    public void showPauseMenu() {
        MenuSession.showPauseMenu(gamePane, gameTimer);
    }


    /**
     * renders the score of a currently running game
     */
    private void renderScoreBoard(){
        gc.setFill(Color.BLUE);
        gc.setFont(new Font("Times New Roman", 20));

        gc.fillText("Health: ", 580, 70);
        //gc.fillText("" + frontendServices.getPlayerHP(), 650, 70);
    }


    /**
     * handles state of a finished game
     * @returns true if game ended and false otherwise
     */
    private boolean gameEnded() {
        if(false){
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Times New Roman", 40));
            gc.fillText("Congratulations!", 200, 300);

            return true;
        }
        return false;
    }

    /**
     * Runs methods which update the state of the game (i.e., renderings, entity updates)
     */
    private void tick() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        services.render(gc);
        renderScoreBoard();

        camera.update();
        services.update();

        if (gameEnded()) {
            services.clear();
            gameTimer.stop();
        }
    }

    /**
     * Creates animation timer which updates the game state every {@code n} seconds where {@code n = 1/FRAME_RATE}
     */
    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            long lastTick = 0;
            @Override
            public void handle(long now) {
                if(now - lastTick >= 1_000_000_000. / FRAME_RATE) {
                    lastTick = now;
                    tick();
                }
            }
        };
        gameTimer.start();
    }
}

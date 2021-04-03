package com.bham.bc.view;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.Mode;
import com.bham.bc.utils.Constants;
import static com.bham.bc.components.CenterController.*;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Class managing the animations of a running game
 */
public class GameSession {

    private static final int GAME_WIDTH = Constants.WINDOW_WIDTH;
    private static final int GAME_HEIGHT = Constants.WINDOW_HEIGHT;

    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Canvas canvas;
    private GraphicsContext gc;

    private Stage menuStage;

    private AnimationTimer gameTimer;

    private Camera cmr;

    private AnchorPane hbPane;

    /**
     * Constructs the view manager
     */
    public GameSession(Mode mode, MapType mapType) {
        setMode(mode, mapType);
        initializeStage();
        createKeyListeners();
    }

    /**
     * initializes the game stage
     */
    private void initializeStage() {
        canvas = new Canvas(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        hbPane = new AnchorPane();
        hbPane.setPrefWidth(Constants.MAP_WIDTH);
        hbPane.setPrefHeight(Constants.MAP_HEIGHT);

        gamePane = new AnchorPane(canvas, hbPane);
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT, Color.GREY);
        cmr = new Camera();
        gameScene.setCamera(cmr);

        gameStage = new Stage();

        gameStage.setScene(gameScene);
        gameStage.setTitle("A very cool game");
        gameStage.setResizable(false);

        KeyCodeCombination keyCodeCombination=new KeyCodeCombination(KeyCode.ESCAPE);
        gameScene.getAccelerators().put(keyCodeCombination, new Runnable() {
            @Override
            public void run() {
                System.out.println("press showPauseMenu");
                MenuSession.showPauseMenu(gamePane);
            }
        });

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
        gameScene.setOnKeyPressed(e -> frontendServices.keyPressed(e));
        gameScene.setOnKeyReleased(e -> frontendServices.keyReleased(e));
    }

    /**
     * renders the score of a currently running game
     */
    private void renderScoreBoard(){
        gc.setFill(Color.BLUE);
        gc.setFont(new Font("Times New Roman", 20));

        gc.fillText("Health: ", 580, 70);
        gc.fillText("" + frontendServices.getPlayerHP(), 650, 70);
    }


    /**
     * handles state of a finished game
     * @returns true if game ended and false otherwise
     */
    private boolean gameEnded() {
        if(frontendServices.isGameOver()){
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Times New Roman", 40));
            gc.fillText("Congratulations!", 200, 300);

            return true;
        }

        return false;
    }

    private void tick() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        frontendServices.render(gc);
        frontendServices.renderHitBoxes(hbPane);
        renderScoreBoard();

        cmr.update();
        backendServices.update();

        if (gameEnded()) {
            backendServices.clear();
            gameTimer.stop();
        }
    }

    /**
     * runs methods which update the state of the game
     */
    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            long lastTick = 0;
            @Override
            public void handle(long now) {
                if(lastTick == 0) {
                    lastTick = now;
                    tick();
                    return;
                }

                if(now - lastTick > 1_000_000_000 / 20) {
                    lastTick = now;
                    tick();
                }
            }

        };
        gameTimer.start();
    }
}

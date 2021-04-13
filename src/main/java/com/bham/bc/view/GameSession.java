package com.bham.bc.view;

import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.Mode;

import static com.bham.bc.components.CenterController.*;

import com.bham.bc.view.model.MenuSlider;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
    private Canvas canvas;
    private GraphicsContext gc;

    private Stage menuStage;

    public static AnimationTimer gameTimer;

    private Camera cmr;

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
        canvas = new Canvas(GameMap.getWidth(), GameMap.getHeight());
        gc = canvas.getGraphicsContext2D();

        gamePane = new AnchorPane(canvas);
        gamePane.getStylesheets().add(MenuSlider.class.getResource("../../../../../GUIResources/Style.css").toExternalForm());

        gameScene = new Scene(gamePane, WIDTH, HEIGHT, Color.GREY);
        cmr = new Camera(gc);

        gameStage = new Stage();

        gameStage.setScene(gameScene);
        gameStage.setTitle("Defenders");
        gameStage.setResizable(false);


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
            if (e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) {
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

    private void tick() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        services.render(gc);
        renderScoreBoard();

        cmr.update();
        services.update();

        if (gameEnded()) {
            services.clear();
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

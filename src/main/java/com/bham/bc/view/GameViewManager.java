package com.bham.bc.view;

import com.bham.bc.utils.Constants;
import static com.bham.bc.components.CenterController.*;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Class managing the animations of a running game
 */
public class GameViewManager {

    private static final int GAME_WIDTH = Constants.WINDOW_WIDTH;
    private static final int GAME_HEIGHT = Constants.WINDOW_HEIGHT;

    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Canvas canvas;
    private GraphicsContext gc;

    private Stage menuStage;

    private AnimationTimer gameTimer;

    /**
     * Constructs the view manager
     */
    public GameViewManager() {
        initializeStage();
        createKeyListeners();
    }

    /**
     * initializes the game stage
     */
    private void initializeStage() {
        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        gamePane = new AnchorPane();
        gamePane.getChildren().add(canvas);

        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT, Color.GREEN);
        gameStage = new Stage();

        gameStage.setScene(gameScene);
        gameStage.setTitle("A very cool game");
        gameStage.setResizable(false);
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

    /**
     * creates the input listeners. Key presses are handled by the center controller class.
     */
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(e -> centerController.keyPressed(e));
        gameScene.setOnKeyReleased(e -> centerController.keyReleased(e));
    }

    /**
     * renders the score of a currently running game
     */
    private void renderScoreBoard(){
        gc.setFill(Color.BLUE);
        gc.setFont(new Font("Times New Roman", 20));

        gc.fillText("Tanks left in the field: ", 200, 70);
        gc.fillText("" + centerController.getEnemyNumber(), 400, 70);
        gc.fillText("Health: ", 580, 70);
        gc.fillText("" + centerController.getLife(), 650, 70);
    }


    /**
     * handles state of a finished game
     * @returns true if game ended and false otherwise
     */
    private boolean gameEnded() {
        if(centerController.isLoss()){
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Times New Roman", 40));
            gc.fillText("Sowwy. You lose!", 200, 300);

            return true;
        } else if(centerController.isWin()){
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Times New Roman", 40));
            gc.fillText("Congratulations!", 200, 300);

            return true;
        }

        return false;
    }

    /**
     * runs methods which update the state of the game
     */
    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Would be better to wrap rendering into one function
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());  // Clear canvas before every frame
                centerController.render(gc);                                      // Render backend content
                renderScoreBoard();                                               // Render backend content

                centerController.update();                                        // Update backend content

                // Would be better for the backend to trigger
                // this state so that less checks are performed
                if(gameEnded()) {                                                 // Stop game loop if game ended
                    centerController.clear();
                    gameTimer.stop();
                }
            }

        };
        gameTimer.start();
    }
}
package com.bham.bc.view;

import com.bham.bc.audio.SoundTrack;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.components.Controller.*;

import com.bham.bc.view.menu.EndMenu;
import com.bham.bc.view.menu.PauseMenu;
import com.bham.bc.view.model.GameFlowEvent;
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
    public static final SoundTrack[] PLAYLIST = new SoundTrack[]{ SoundTrack.REVOLUTION, SoundTrack.CORRUPTION, SoundTrack.TAKE_LEAD };


    private final PauseMenu PAUSE_MENU;

    private AnchorPane gamePane;
    private Scene gameScene;
    public static Stage gameStage;
    private GraphicsContext gc;

    private Stage menuStage;

    private AnimationTimer gameTimer;

    private Camera camera;

    /**
     * Constructs the view manager
     */
    public GameSession(MapType mapType) {
        setMode(mapType);

        PAUSE_MENU = new PauseMenu();


        initializeStage();
        createKeyListeners();
        gamePane.addEventFilter(GameFlowEvent.PAUSE_GAME, e -> pauseGame());
        gamePane.addEventFilter(GameFlowEvent.LEAVE_GAME, e -> leaveGame());
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
        gameStage.setTitle("Blueland Defenders");
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
    public void startGame(Stage menuStage) {
        this.menuStage = menuStage;
        this.menuStage.hide();

        createGameLoop();
        gameStage.show();
    }

    /**
     * creates the input listeners. Key presses are handled by the center controller class.
     */
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.Q) {  // Testing purposes
                gameTimer.stop();
                Random r=new Random();
                MenuSession.showEndMenu(gamePane, r.nextDouble()*999);
            } else if(e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) {
                pauseGame();
            } else {
                services.keyPressed(e);
            }
        });
        gameScene.setOnKeyReleased(e -> services.keyReleased(e));
    }


    /**
     * Handles the event of pausing the game
     *
     * <p>If the pause menu is contained within the <i>gamePane</i>, it removes the pause menu, resumes the <i>gameTimer</i>
     * and the background music. Otherwise, it attaches the pause menu, stops the game and the playing music.</p>
     */
    private void pauseGame() {
        if(gamePane.getChildren().contains(PAUSE_MENU)) {
            audioManager.playMusic();
            PAUSE_MENU.hide(gamePane);
            gameTimer.start();
        } else {
            audioManager.pauseMusic();
            PAUSE_MENU.show(gamePane);
            gameTimer.stop();
        }
    }

    /**
     * Handles the event of leaving the game
     *
     * <p>This method simply closes the game window and returns to the menu window which was passed when the stages were
     * swapped together in {@link #startGame(Stage)}. It should not be called during the actual gameplay, i.e., when the
     * <i>gameTimer</i> is running.</p>
     */
    private void leaveGame() {
        gameStage.hide();
        menuStage.show();
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

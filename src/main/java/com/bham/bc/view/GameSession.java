package com.bham.bc.view;

import com.bham.bc.audio.SoundTrack;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.components.Controller.*;
import static com.bham.bc.utils.Timer.CLOCK;
import static com.bham.bc.view.MenuSession.*;

import com.bham.bc.view.menu.EndMenu;
import com.bham.bc.view.menu.MainMenu;
import com.bham.bc.view.menu.PauseMenu;
import com.bham.bc.view.model.CustomStage;
import com.bham.bc.view.tools.GameFlowEvent;
import com.bham.bc.view.tools.Camera;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <h1>GameSession</h1>
 *
 * <p>Class managing the animations and events of a running game.</p>
 */
public class GameSession {

    /** Window's width (in pixels) */
    public static final int WIDTH = 1024; // old: 800

    /** Window's height (in pixels) */
    public static final int HEIGHT = 768; // old: 600

    /** Desired number of updates per second */
    public static final int FRAME_RATE = 24;

    /** Songs to be played in sequence during the game session */
    public static final SoundTrack[] PLAYLIST = new SoundTrack[]{ SoundTrack.REVOLUTION, SoundTrack.CORRUPTION, SoundTrack.TAKE_LEAD };

    /** Pause menu layout to be attached to the game pane whenever the game is paused*/
    private final PauseMenu PAUSE_MENU;

    /** Camera to follow the player */
    private Camera camera;

    /** Graphics context to render things on */
    private GraphicsContext gc;

    /** Root game layout to which all the elements are attached */
    private AnchorPane gamePane;

    /** Scene to be showed on the window during gameplay */
    private Scene gameScene;

    /** Scene to be showed on the window after gameplay */
    private Scene menuScene;

    /** Main stage (window) on which the scenes are loaded */
    private Stage mainStage;

    /** Game timer to track how much time has passed since the game was started */
    private AnimationTimer gameTimer;

    /** The timestamp at which the game was started */
    private long startTimestamp;

    /** Property of how much time has passed since the game was started */
    private StringProperty timeSurvived;

    /** Property of how much score the player has achieved */
    private StringProperty scoreAchieved;

    /** Property of how much health (fraction) the home territory has remaining */
    private DoubleProperty healthFraction;

    /**
     * Constructs the game session for the provided stage
     * @param mainStage the stage on which the game scene will be loaded
     * @param mapType   the type of map
     */
    public GameSession(Stage mainStage, MapType mapType) {
        this.mainStage = mainStage;

        PAUSE_MENU = new PauseMenu();
        setMode(mapType);
        initLayout();
        initProgressPane();
        createKeyListeners();

        VBox titleAndRoot2=new VBox();
        customStage.setTitleAndRoote(titleAndRoot2);
        titleAndRoot2.getChildren().addAll(gpTitle, gamePane);
        gameScene.setRoot(titleAndRoot2);
    }

    /**
     * Initializes the layout of the game session, i.e., sets up the root pane and event filters.
     */
    private void initLayout() {
        Canvas canvas = new Canvas(GameMap.getWidth(), GameMap.getHeight());
        gc = canvas.getGraphicsContext2D();

        camera = new Camera(gc);
        gamePane = new AnchorPane(canvas);
        gamePane.addEventFilter(GameFlowEvent.PAUSE_GAME, this::pauseGame);
        gamePane.addEventFilter(GameFlowEvent.LEAVE_GAME, this::leaveGame);

        gameScene = new Scene(gamePane, WIDTH, HEIGHT+34);

        try {
            gameScene.getStylesheets().add(getClass().getClassLoader().getResource("model/style.css").toExternalForm());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the progress pane, i.e., sets up the home health bar, score and time labels.
     */
    private void initProgressPane() {
        // Declare the global colors used in the CSS file
        String FG_1 = "#135ADD";  // -fx-primary-color (foreground primary)
        String FG_2 = "#B0CAFF";  // -fx-lighten-color (foreground secondary)
        String BG_1 = "#080A1E";  // -fx-bg-color (background primary)

        // Define all the properties out game pane will depend on
        timeSurvived = new SimpleStringProperty("00:00");
        scoreAchieved = new SimpleStringProperty("0");
        healthFraction = new SimpleDoubleProperty(1);

        // Set up home health bar
        StackPane homeHealthBar = new StackPane();
        homeHealthBar.setBackground(new Background(new BackgroundFill(Color.web(FG_1), new CornerRadii(20), new Insets(0))));
        homeHealthBar.setId("home-health-bar");

        // Set up home health label
        Label homeHealthTxt = new Label("        Territory taken over       ");
        homeHealthTxt.setTextFill(Color.web(FG_2));
        homeHealthTxt.setEffect(new Glow(1));
        homeHealthTxt.setId("home-health-label");

        // Make the colors of the home health bar and text dynamic
        healthFraction.addListener((obsVal, oldVal, newVal) -> {
            Stop[] homeHealthBarStops = new Stop[]{ new Stop(newVal.doubleValue(), Color.web(FG_1)), new Stop(newVal.doubleValue(), Color.web(BG_1)) };
            Stop[] homeHealthTxtStops = new Stop[]{ new Stop(newVal.doubleValue(), Color.web(FG_2)), new Stop(newVal.doubleValue(), Color.web(FG_1)) };
            LinearGradient homeHealthBarGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, homeHealthBarStops);
            LinearGradient homeHealthTxtGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, homeHealthTxtStops);
            homeHealthBar.setBackground(new Background(new BackgroundFill(homeHealthBarGradient, new CornerRadii(20), new Insets(0))));
            homeHealthTxt.setTextFill(homeHealthTxtGradient);
        });

        // Add home health bar and label to container
        StackPane healthProgress = new StackPane();
        healthProgress.getChildren().addAll(homeHealthBar, homeHealthTxt);

        // Set up score label and add to container
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(scoreAchieved);
        HBox scoreProgress = new HBox();
        scoreProgress.getChildren().add(scoreLabel);
        scoreProgress.getStyleClass().add("progress-cell");

        // Set up time label and add to container
        Label timeLabel = new Label();
        timeLabel.textProperty().bind(timeSurvived);
        HBox timeProgress = new HBox();
        timeProgress.getChildren().add(timeLabel);
        timeProgress.getStyleClass().add("progress-cell");

        // Add home health bar, score and time containers to the progress layout
        TilePane progressPane = new TilePane();
        progressPane.setMinSize(WIDTH, HEIGHT);
        progressPane.getChildren().addAll(healthProgress, scoreProgress, timeProgress);
        progressPane.getStyleClass().add("progress-pane");

        // Add the progress layout to the game pane
        gamePane.getChildren().add(progressPane);
    }

    /**
     * Creates the input listeners. Key presses are handled by the center controller class (except for {@code P} and {@code ESC}).
     */
    private void createKeyListeners() {
        gamePane.getScene().setOnKeyPressed(e -> {
            if((e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) && !services.gameOver()) {
                gamePane.fireEvent(new GameFlowEvent(GameFlowEvent.PAUSE_GAME));
            } else {
                services.keyPressed(e);
            }
        });
        gamePane.getScene().setOnKeyReleased(e -> services.keyReleased(e));
    }

    /**
     * Handles the event of pausing the game
     *
     * <p>If the pause menu is contained within the <i>gamePane</i>, it removes the pause menu, resumes the <i>gameTimer</i>
     * and the background music. Otherwise, it attaches the pause menu, stops the game and the playing music.</p>
     *
     * @param e <i>PAUSE_GAME_EVENT</i> which is not used anywhere, purely just to denote a pause event was fired
     */
    private void pauseGame(GameFlowEvent e) {
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
     * Handles the event of ending the game
     *
     * <p>This method stops the <i>gameTimer</i> and the background music, clears the content present in services,
     * attaches end menu to game session's root pane and passes the final game score the player acquired.</p>
     */
    public void endGame() {
        gameTimer.stop();
        services.clear();
        audioManager.stopMusic();
        EndMenu endMenu = new EndMenu();
        endMenu.show(gamePane, services.getScore());
    }

    /**
     * Handles the event of leaving the game
     *
     * <p>This method simply closes the game session and returns to the menu scene which was passed when the scenes were
     * swapped together in {@link #startGame(Scene)}. It should not be called during the actual gameplay, i.e., when the
     * <i>gameTimer</i> is running. The playlist of menu songs also starts playing.</p>
     *
     * <p><b>Note:</b> this is bad practice but currently <i>LEAVE_GAME_EVENT</i> is handled by MainMenu layout to update
     * the scores table and therefore its location is hardcoded. This is based how the {@link MenuSession} and the
     * {@link CustomStage} structure their layouts.</p>
     *
     * @param e <i>LEAVE_GAME_EVENT</i> which provides details about the name and the score to be saved in the leaderboard
     */
    private void leaveGame(GameFlowEvent e) {
        MenuSession.customStage.changeMainSkin.getSelectionModel().select(CustomStage.selected);

        mainStage.setScene(menuScene);
        menuScene.setRoot(titleAndRoot);

        try {
            AnchorPane menuPane = (AnchorPane) ((AnchorPane) titleAndRoot.getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable().get(1);
            menuPane.getChildren().get(1).fireEvent(e);
        } catch(ClassCastException | NullPointerException exception) {
            exception.printStackTrace();
            System.out.println("Make sure MainMenu is located as follows:\nStage -> Scene -> Root -> (AnchorPane)Child(1) -> Child(1)");
        }

        audioManager.loadSequentialPlayer(true, MenuSession.PLAYLIST);
        audioManager.playMusic();
    }

    /**
     * Updates the state of the game
     *
     * <p>Three main things are updated for the user interface:</p>
     * <ul>
     *     <li>Home territory's HP</li>
     *     <li>The total score (based on enemies killed)</li>
     *     <li>The time passed</li>
     * </ul>
     * <p>It also checks if the game is over and ends the session if it is.</p>
     */
    private void updateGameState() {
        long currentTime = CLOCK.getCurrentTime() - startTimestamp;

        DateFormat timeFormat = new SimpleDateFormat("mm:ss");
        timeSurvived.set(timeFormat.format(currentTime));

        scoreAchieved.set(String.format("%.0f", services.getScore()));
        healthFraction.set(services.getHomeHpFraction());

        if(services.gameOver()){
            endGame();
        }
    }

    /**
     * Starts the game loop and swaps the menu scene with the game scene
     * @param menuScene menu scene to be hidden and later reloaded when the game finishes
     */
    public void startGame(Scene menuScene) {
        this.menuScene = menuScene;
        mainStage.setScene(gameScene);
        startTimestamp = CLOCK.getCurrentTime();
        createGameLoop();
    }

    /**
     * Runs methods which update the state of the game (i.e., renderings, entity updates)
     */
    private void tick() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        services.render(gc);
        camera.update();
        services.update();
        updateGameState();
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

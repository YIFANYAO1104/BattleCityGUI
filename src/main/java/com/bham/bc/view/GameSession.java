package com.bham.bc.view;

import com.bham.bc.audio.SoundTrack;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.MapType;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.components.Controller.*;
import static com.bham.bc.utils.Timer.CLOCK;

import com.bham.bc.view.menu.EndMenu;
import com.bham.bc.view.menu.PauseMenu;
import com.bham.bc.view.model.GameFlowEvent;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
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
 * Class managing the animations of a running game
 */
public class GameSession {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FRAME_RATE = 24;
    public static final SoundTrack[] PLAYLIST = new SoundTrack[]{ SoundTrack.REVOLUTION, SoundTrack.CORRUPTION, SoundTrack.TAKE_LEAD };

    private final PauseMenu PAUSE_MENU;

    private Camera camera;
    private GraphicsContext gc;
    private AnchorPane gamePane;
    private Stage gameStage;
    private Stage menuStage;
    private AnimationTimer gameTimer;

    private long startTimestamp;
    private StringProperty timeSurvived;
    private StringProperty scoreAchieved;
    private DoubleProperty healthFraction;

    /**
     * Constructs the game session
     */
    public GameSession(MapType mapType) {
        PAUSE_MENU = new PauseMenu();
        setMode(mapType);
        initLayout();
        initWindow();
        initProgressPane();
        createKeyListeners();
    }

    /**
     * Initializes the layout of the game session, i.e., sets up the root pane and event filters
     */
    private void initLayout() {
        Canvas canvas = new Canvas(GameMap.getWidth(), GameMap.getHeight());
        gc = canvas.getGraphicsContext2D();
        camera = new Camera(gc);
        gamePane = new AnchorPane(canvas);
        gamePane.addEventFilter(GameFlowEvent.PAUSE_GAME, this::pauseGame);
        gamePane.addEventFilter(GameFlowEvent.LEAVE_GAME, this::leaveGame);

        try {
            gamePane.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the window of the menu session, i.e., sets up the scene and the custom stage
     */
    private void initWindow() {
        Scene gameScene = new Scene(gamePane, WIDTH, HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.setResizable(false);
        gameStage.setTitle("Blueland Defenders");
        CustomStage customStage=new CustomStage(gameStage, gameScene,gamePane);
        customStage.createTitleBar(gamePane, WIDTH);
    }

    /**
     * Initializes the progress pane, i.e., sets up the home health bar, score and time labels
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
            if(e.getCode() == KeyCode.Q) {  // TODO: remove
                endGame();
            } else if(e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) {
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
    private void endGame() {
        gameTimer.stop();
        services.clear();
        audioManager.stopMusic();
        EndMenu endMenu = new EndMenu();
        endMenu.show(gamePane, services.getScore());
    }

    /**
     * Handles the event of leaving the game
     *
     * <p>This method simply closes the game window and returns to the menu window which was passed when the stages were
     * swapped together in {@link #startGame(Stage)}. It should not be called during the actual gameplay, i.e., when the
     * <i>gameTimer</i> is running. The playlist of menu songs also starts playing.</p>
     *
     * <p><b>Note:</b> this is bad practice but currently <i>LEAVE_GAME_EVENT</i> is handled by MainMenu layout to update
     * the scores table and therefore its location is hardcoded. This is based how the {@link MenuSession} and the
     * {@link CustomStage} structure their layouts.</p>
     *
     * @param e <i>LEAVE_GAME_EVENT</i> which provides details about the name and the score to be saved in the leaderboard
     */
    private void leaveGame(GameFlowEvent e) {
        gameStage.hide();
        menuStage.show();

        MenuSession.customStage.changeMainSkin.getSelectionModel().select(CustomStage.selected);

        try {
            AnchorPane mainPane = (AnchorPane) menuStage.getScene().getRoot().getChildrenUnmodifiable().get(1);
            mainPane.getChildren().get(1).fireEvent(e);
        } catch(ClassCastException | NullPointerException exception) {
            exception.printStackTrace();
            System.out.println("Make sure MainMenu is located as follows:\nStage -> Scene -> Root -> (AnchorPane)Child(1) -> Child(1)");
        }

        audioManager.loadSequentialPlayer(true, MenuSession.PLAYLIST);
        audioManager.playMusic();
    }

    private void updateGameState() {
        long currentTime = CLOCK.getCurrentTime() - startTimestamp;


        DateFormat timeFormat = new SimpleDateFormat("mm:ss");
        timeSurvived.set(timeFormat.format(currentTime));

        scoreAchieved.set(String.format("%.0f", services.getScore()));
        healthFraction.set(services.getHomeHpFraction());
    }

    /**
     * Starts the game loop and shows the game view
     * @param menuStage menu stage to be hidden
     */
    public void startGame(Stage menuStage) {
        this.menuStage = menuStage;
        this.menuStage.hide();
        startTimestamp = CLOCK.getCurrentTime();

        createGameLoop();
        gameStage.show();
    }

    /**
     * Runs methods which update the state of the game (i.e., renderings, entity updates)
     */
    private void tick() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        updateGameState();
        services.render(gc);

        camera.update();
        services.update();
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

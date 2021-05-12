package com.bham.bc.view;

import com.bham.bc.audio.SoundTrack;
import com.bham.bc.view.menu.MainMenu;
import com.bham.bc.view.model.CustomStage;
import com.bham.bc.view.model.MenuBackground;
import com.bham.bc.view.tools.GameFlowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * <h1>Menu Session</h1>
 *
 * <p>This class manages all the menus. Not only it sets up a stage for the main menu, it also is
 * responsible for bringing up pause menu and end screen during the gameplay. Therefore, it has a
 * strong communication with {@link com.bham.bc.view.GameSession}.</p>
 */
public class MenuSession {

    /** Window's width (in pixels) */
    public static final int WIDTH = 1024;

    /** Window's height (in pixels) */
    public static final int HEIGHT = 768;

    /** Songs to be played in sequence during the menu session */
    public static final SoundTrack[] PLAYLIST = new SoundTrack[]{ SoundTrack.NIGHT_BREAK };

    /** Main layout containing all the menu session's elements */
    private AnchorPane mainPane;

    /** Scene to be showed on the window before gameplay */
    private Scene menuScene;

    /** Main stage (window) on which the scenes are loaded */
    private Stage mainStage;

    /** Custom stage */
    public static CustomStage customStage;

    /**
     * Constructs the menu session
     */
    public MenuSession() {
        initLayout();
        initWindow();
        initMainMenu();
    }

    /**
     * Initializes the layout of the menu session, i.e., sets up the root pane and event filters
     */
    private void initLayout() {
        mainPane = new AnchorPane();
        mainPane.addEventFilter(GameFlowEvent.START_GAME, this::createGameSession);
        menuScene = new Scene(mainPane, WIDTH, HEIGHT);

        try {
            menuScene.getStylesheets().add(getClass().getClassLoader().getResource("model/style.css").toExternalForm());
        } catch(IllegalArgumentException | IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the window of the menu session, i.e., sets up the scene and the custom stage
     */
    private void initWindow() {
        mainStage = new Stage();
        mainStage.setScene(menuScene);
        mainStage.setResizable(false);
        mainStage.setTitle("Blueland Defenders");

        // customStage = new CustomStage(mainStage);
        // customStage.createMainTitlebar(mainPane, WIDTH);
    }

    /**
     * Initializes the main menu which the user will see and from where they can start a new game session
     */
    private void initMainMenu() {
        MainMenu mainMenu = new MainMenu();
        MenuBackground menuBackground = new MenuBackground();
        mainPane.getChildren().addAll(menuBackground, mainMenu);

        audioManager.loadSequentialPlayer(true, PLAYLIST);
        audioManager.playMusic();
    }

    /**
     * Handles the event of starting the game
     *
     * <p>Creates a single Game Session based on the chosen parameters. The parameters can be the number of players,
     * type of map and the game mode. Currently only 1 player and survival mode is supported.</p>
     *
     * @param e event from which the parameters of a new game session are checked
     */
    public void createGameSession(GameFlowEvent e) {
        audioManager.loadSequentialPlayer(true, GameSession.PLAYLIST);
        GameSession gameSession = new GameSession(mainStage, e.getMapType());
        gameSession.startGame(menuScene);

        audioManager.playMusic();
    }

    /**
     * Returns the main stage used for the main menu
     * @return the menu stage
     */
    public Stage getMainStage() {
        return mainStage;
    }
}

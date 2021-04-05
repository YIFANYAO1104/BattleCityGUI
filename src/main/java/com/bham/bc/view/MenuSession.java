package com.bham.bc.view;

import com.bham.bc.audio.TRACK;
import com.bham.bc.view.menu.EndMenu;
import com.bham.bc.view.menu.MainMenu;
import com.bham.bc.view.menu.PauseMenu;
import com.bham.bc.view.model.MenuBackground;
import com.bham.bc.view.model.NewGameEvent;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * <h1>Menu Session</h1>
 *
 * <p>This class manages all the menus. Not only it sets up a stage for the main menu, it also is
 * responsible for bringing up pause menu and end screen during the gameplay. Therefore, it has a
 * strong communication with {@link com.bham.bc.view.GameSession}</p>
 */
public class MenuSession {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private static PauseMenu pauseMenu = new PauseMenu();

    /**
     * Constructs the menu view manager
     */
    public MenuSession() {
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();
        mainStage.setScene(mainScene);

        initMainMenu();
        CustomStage customStage=new CustomStage(mainStage,mainScene,mainPane);
        customStage.createCommonTitlebar(mainPane,WIDTH,HEIGHT);

        mainPane.addEventFilter(NewGameEvent.START_GAME, this::createGameSession);
    }

    /**
     * Creates the main menu from where the user can start a new game session
     */
    private void initMainMenu() {
        MainMenu mainMenu = new MainMenu();
        MenuBackground menuBackground = new MenuBackground();

        mainPane.getChildren().addAll(menuBackground, mainMenu);

        audioManager.createSequentialPlayer(TRACK.BREAK);
        audioManager.play();
    }


    /**
     * Attaches (detaches) pause menu to the provided game pane and shows (hides) it
     * @param gamePane game pane the pause menu will be attached (detached) to
     * @param timer animation timer to be stopped (started)
     */
    public static void showPauseMenu(AnchorPane gamePane, AnimationTimer timer) {
        if(gamePane.getChildren().contains(pauseMenu)) {
            pauseMenu.hide(gamePane);
            timer.start();
        } else {
            pauseMenu.show(gamePane);
            timer.stop();
        }
    }

    /**
     * Attaches Options menu to the provided game pane and shows (hides) it
     * @param gamePane game pane the Options menu will be attached (detached) to
     * @param timer animation timer to be stopped (started)
     */
    public static void showOptionsMenu(AnchorPane gamePane, AnimationTimer timer) {
        if(gamePane.getChildren().contains(pauseMenu)) {
            pauseMenu.hide(gamePane);
            timer.start();
        } else {
            pauseMenu.showOptionsMenu(gamePane);
            timer.stop();
        }
    }





    public static void showEndMenu(AnchorPane gamePane, double score) {
        EndMenu endMenu = new EndMenu();
    }


    /**
     * Creates a single Game Session based on a chosen MODE
     * @param e SURVIVAL or CHALLENGE mode to be set in Controller
     */
    public void createGameSession(NewGameEvent e) {
        audioManager.createSequentialPlayer(TRACK.CORRUPTION, TRACK.LEAD, TRACK.REVOLUTION);
        GameSession gameSession = new GameSession(e.getMode(), e.getMapType());
        gameSession.createNewGame(mainStage);

        audioManager.play();
    }

    /**
     * Returns the main stage used for the main menu
     * @return the menu stage
     */
    public Stage getMainStage() {
        return mainStage;
    }
}

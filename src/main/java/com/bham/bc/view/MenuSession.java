package com.bham.bc.view;

import com.bham.bc.audio.TRACK;
import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.utils.Constants;
import com.bham.bc.view.menu.EndMenu;
import com.bham.bc.view.menu.MainMenu;
import com.bham.bc.view.menu.PauseMenu;
import com.bham.bc.view.model.MenuBackground;
import com.bham.bc.view.model.NewGameEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * <h1>Menu Session</h1>
 *
 * <p>This class manages all the menus. Not only it sets up a stage for the main menu, it also is
 * responsible for bringing up pause menu and end screen during the gameplay. Therefore, it has a
 * strong communication with {@link com.bham.bc.view.GameSession}</p>
 */
public class MenuSession {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;



    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private MainMenu mainMenu;
    private static PauseMenu pauseMenu;



    /**
     * Constructs the menu view manager
     */
    public MenuSession() {
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();
        mainStage.setScene(mainScene);

        mainMenu = new MainMenu(this, WIDTH, HEIGHT);
        pauseMenu = new PauseMenu(this);

        initMainMenu();

        mainPane.addEventFilter(NewGameEvent.START_GAME, this::createGameSession);

    }

    /**
     * Creates the main menu from where the user can start a new game session
     */
    private void initMainMenu() {
        audioManager.createSequentialPlayer(TRACK.BREAK);

        MenuBackground menuBackground = new MenuBackground(WIDTH, HEIGHT);
        mainPane.getChildren().addAll(menuBackground, mainMenu);

        audioManager.play();
    }

    /**
     *
     * @param gamePane
     */

    public static void showPauseMenu(AnchorPane gamePane) {
        if (pauseMenu==null){

        }
        if (pauseMenu!=null){
            System.out.println("not null");
        }

        if(gamePane.getChildren().contains(pauseMenu)==false) {
            gamePane.getChildren().add(pauseMenu);

        }else if (PauseMenu.isshown==true){

            System.out.println("fadeOut");
            pauseMenu.fadeOut();
        }else if (PauseMenu.isshown==false){
            System.out.println("fadeIn");
            pauseMenu.fadeIn();
        }

    }

    private MainMenu getMainMenu() { return mainMenu; }
    private EndMenu getEndMenu(double score) { return new EndMenu(); }


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

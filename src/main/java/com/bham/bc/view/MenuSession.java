package com.bham.bc.view;

import com.bham.bc.view.menu.EndMenu;
import com.bham.bc.view.menu.MainMenu;
import com.bham.bc.view.menu.PauseMenu;
import com.bham.bc.view.model.MenuBackground;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Class managing the behavior of the main menu
 */
public class MenuSession {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private MainMenu mainMenu;
    private PauseMenu pauseMenu;
    private EndMenu endMenu;

    /**
     * Constructs the menu view manager
     */
    public MenuSession() {
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();
        mainStage.setScene(mainScene);

        mainMenu = new MainMenu(WIDTH, HEIGHT);
        pauseMenu = new PauseMenu();
        endMenu = new EndMenu();

        initMainMenu();
        createStartButton();
    }

    private void initMainMenu() {
        MenuBackground menuBackground = new MenuBackground(WIDTH, HEIGHT);

        mainPane.getChildren().addAll(menuBackground, mainMenu);
    }

















    /**
     * adds a default play button
     */
    private void createStartButton() {
        Button startButton = new Button("Play");

        startButton.setOnAction(e -> {
            // To do: add javafx concurrency in the background
           GameSession gameSession = new GameSession();
           gameSession.createNewGame(mainStage);
        });

        mainPane.getChildren().add(startButton);
    }

    /**
     * returns the main stage used for the menu
     * @return the menu stage
     */
    public Stage getMainStage() {
        return mainStage;
    }
}

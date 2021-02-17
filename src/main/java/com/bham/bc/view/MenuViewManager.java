package com.bham.bc.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Class managing the behavior of the main menu
 */
public class MenuViewManager {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    /**
     * Constructs the menu view manager
     */
    public MenuViewManager() {
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();
        mainStage.setScene(mainScene);

        createStartButton();
    }

    /**
     * adds a default play button
     */
    private void createStartButton() {
        Button startButton = new Button("Play");

        startButton.setOnAction(e -> {
           GameViewManager gameViewManager = new GameViewManager();
           gameViewManager.createNewGame(mainStage);
        });

        mainPane.getChildren().add(startButton);
    }

    /**
     * returns the main stage used for the menu
     * @return the main stage
     */
    public Stage getMainStage() {
        return mainStage;
    }
}

package com.bham.bd.application;

import com.bham.bd.view.MenuSession;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * <h1>Main Application</h1>
 *
 * <p>Represents the main JavaFX application which can be launched by firstly creating a menu session.</p>
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MenuSession manager = new MenuSession();
            primaryStage = manager.getMainStage();
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main function to launch the JavaFX application
     * @param args any additional arguments to be passed when the game is launched
     */
    public static void main(String[] args) {
        launch(args);
    }
}

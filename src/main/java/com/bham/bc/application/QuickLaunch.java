package com.bham.bc.application;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.view.GameSession;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * <h1>QuickLaunch</h1>
 *
 * <p>This is the class for starting game directly from Game Interface rather than Main Interface</p>
 */
public class QuickLaunch extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            GameSession manager = new GameSession(MapType.Map1);
            manager.startGame(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}
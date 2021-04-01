package com.bham.bc.application;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.Mode;
import com.bham.bc.view.GameSession;
import javafx.application.Application;
import javafx.stage.Stage;

public class QuickLaunch extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            GameSession manager = new GameSession(Mode.CHALLENGE, MapType.EmptyMap);
            manager.createNewGame(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}
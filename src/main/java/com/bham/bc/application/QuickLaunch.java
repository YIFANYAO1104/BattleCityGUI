package com.bham.bc.application;

import com.bham.bc.view.GameViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class QuickLaunch extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            GameViewManager manager = new GameViewManager();
            manager.createNewGame(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}
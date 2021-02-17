package com.bham.bc.application;

import com.bham.bc.view.MenuViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MenuViewManager manager = new MenuViewManager();
            primaryStage = manager.getMainStage();
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}

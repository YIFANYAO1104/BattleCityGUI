package com.bham.bc.application;

import com.bham.bc.view.MenuSession;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static MenuSession manager;

    @Override
    public void start(Stage primaryStage) {
        try {
            manager = new MenuSession();
            primaryStage = manager.getMainStage();
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}

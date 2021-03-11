package com.bham.bc;

import com.bham.bc.components.environment.GameMap;
import com.bham.bc.view.GameViewManager;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.Test;


public class GraphTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            GameViewManager manager = new GameViewManager();
            manager.createNewGame(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1(){
//        new JFXPanel();
//        GameMap gameMap = new GameMap("/floodMap1.json");
//        centerController.setGameMap(gameMap);
//        launch();
    }
}

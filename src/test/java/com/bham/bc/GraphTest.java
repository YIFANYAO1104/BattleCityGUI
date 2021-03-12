package com.bham.bc;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.view.GameSession;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;


public class GraphTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            GameSession manager = new GameSession(MODE.SURVIVAL, MapType.Map1);
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

package com.bham.bc;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.view.GameSession;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;


public class GraphTest extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            GameSession manager = new GameSession(MODE.SURVIVAL, MapType.TESTMap1);
            manager.createNewGame(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test1(){
        launch();
    }


}

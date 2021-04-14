package com.bham.bc;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.view.GameSession;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;


public class MapTest extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            GameSession manager = new GameSession(MapType.EmptyMap);
            manager.createNewGame(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test1(){
//        launch();
    }

}

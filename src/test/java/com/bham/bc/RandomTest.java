package com.bham.bc;

import com.bham.bc.utils.GeometryEnhanced;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.Test;

public class RandomTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        int windowHeight = 800;
        int windowWidth = 800;
        double radius = 300;
        Point2D center = new Point2D(windowWidth/2,windowHeight/2);
        double pointRadius = 4;

        AnchorPane gamePane;
        Scene gameScene;
        Stage gameStage;
        Canvas canvas;
        GraphicsContext gc;

        canvas = new Canvas(windowWidth, windowHeight);

        //draw bound
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.strokeOval(center.getX()-radius, center.getY()-radius, 2*radius, 2*radius);

        //draw points
        for (int i = 0;i<1000;i++){
            gc.setFill(Color.WHITE);
            Point2D p = GeometryEnhanced.randomPointInCircle(center,radius);
            gc.fillOval(p.getX()-pointRadius,p.getY()-pointRadius,2*pointRadius,2*pointRadius);
        }



        gamePane = new AnchorPane(canvas);
        gameScene = new Scene(gamePane, windowWidth, windowHeight, Color.GREY);


        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.setResizable(false);
        gameStage.show();


    }


    @Test
    public void test1(){
        launch();
    }
}

package com.bham.bc.view;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainMenu extends Application {

    private GameMenu gameMenu;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        InputStream is = Files.newInputStream(Paths.get("res/images/img.gif"));
        Image img = new Image(is);
        is.close();

        Title title = new Title ("T A N K 1 G A M E");
        title.setTranslateX(150);
        title.setTranslateY(100);

        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(600);
        imgView.setFitWidth(800);
        //imgView.setOpacity(0.9);

        gameMenu = new GameMenu();
        root.getChildren().addAll(imgView,gameMenu);
        root.getChildren().addAll(title);
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
    private static class Title extends StackPane {
        public Title(String name) {
            Rectangle bg = new Rectangle(475, 60);
            bg.setStroke(Color.WHITE);
            bg.setStrokeWidth(3);
            bg.setFill(null);


            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.TOP_CENTER);
            getChildren().addAll(bg,text);
        }
    }
    private class GameMenu extends Parent {
        public GameMenu() {
            VBox menu0 = new VBox(15);
            VBox menu1 = new VBox(15);

            menu0.setTranslateX(250);
            menu0.setTranslateY(200);

            menu1.setTranslateX(250);
            menu1.setTranslateY(200);

            final int offset = 400;
            menu1.setTranslateX(offset);

            MenuButtin resumeBtn = new MenuButtin("RESUME");
            resumeBtn.setOnMouseClicked(event -> {

            });

            MenuButtin btnOpetion = new MenuButtin("OPITION");
            btnOpetion.setOnMouseClicked(event -> {
                getChildren().add(menu1);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25),menu0);
                tt.setToX(menu0.getTranslateX()-offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5),menu1);
                tt1.setToX(menu0.getTranslateX());

                tt.play();
                tt1.play();
                tt.setOnFinished(evt -> {
                    getChildren().remove(menu0);
                });
            });
            MenuButtin btnExit = new MenuButtin("EXIT");
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });

            MenuButtin btnBack = new MenuButtin("BACK");
            btnBack.setOnMouseClicked(event -> {
                getChildren().add(menu0);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25),menu1);
                tt.setToX(menu1.getTranslateX()+offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5),menu0);
                tt1.setToX(menu1.getTranslateX());
                tt.play();
                tt1.play();
                tt.setOnFinished(evt -> {
                    getChildren().remove(menu1);
                });
            });

            MenuButtin btnSound = new MenuButtin("SOUND");
            MenuButtin btnVideo = new MenuButtin("VIDEO");

            menu0.getChildren().addAll(resumeBtn, btnOpetion, btnExit);
            menu1.getChildren().addAll(btnBack, btnSound, btnVideo);

            Rectangle bg = new Rectangle(800, 600);
            bg.setFill(Color.GRAY);
            bg.setOpacity(0.2);
            getChildren().addAll(bg, menu0);

        }
    }
    private static class MenuButtin extends StackPane {
        private Text text;

        public MenuButtin(String name) {
            text = new Text(name);
            text.setFont(text.getFont().font(20));
            text.setFill(Color.WHITE);

            Rectangle bg = new Rectangle(250, 30);
            bg.setOpacity(0.6);
            bg.setFill(Color.BLACK);
            bg.setEffect(new GaussianBlur(3.5));

            setAlignment(Pos.CENTER_LEFT);
            setRotate(-0.5);
            getChildren().addAll(bg, text);

            setOnMouseEntered(event -> {
                bg.setFill(Color.WHITE);
                text.setFill(Color.BLACK);
                bg.setTranslateX(10);
                text.setTranslateX(10);
            });

            setOnMouseExited(event -> {
                bg.setFill(Color.BLACK);
                text.setFill(Color.WHITE);
                bg.setTranslateX(0);
                text.setTranslateX(0);
            });

            DropShadow drop = new DropShadow(50, Color.WHITE);
            drop.setInput(new Glow());

            setOnMousePressed(event -> setEffect(drop));
            setOnMouseReleased(event -> setEffect(null));
        }


        public static void main(String[] args) {
            launch(args);
        }
    }
}

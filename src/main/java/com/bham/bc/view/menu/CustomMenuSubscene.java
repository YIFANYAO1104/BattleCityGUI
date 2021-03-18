package com.bham.bc.view.menu;


import com.bham.bc.audio.MusicThread;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.utils.Constants;
import com.bham.bc.view.GameSession;
import com.bham.bc.view.menuButtons.*;
import com.bham.bc.view.model.TankPicker;
import com.bham.bc.view.model.TankType;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.net.MalformedURLException;


public class CustomMenuSubscene extends SubScene {


    public static AnchorPane root;


    public static ButtonSubscene sceneToHide;
    public static Stage stage;
    private Scene gameScene;








    public CustomMenuSubscene(){
        super(new AnchorPane(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        prefHeight(Constants.WINDOW_HEIGHT);
        prefWidth(Constants.MAP_WIDTH);
        root= (AnchorPane) this.getRoot();
        setLayoutX(1024);

        setLayoutY(Constants.MAP_WIDTH/4);

        moveSubcene(this);
        createContent();
        GameSession.isshown=true;
        BackgroundImage image=new BackgroundImage(new Image("file:src/main/resources/GUIResources/menuIcon3.png",Constants.WINDOW_WIDTH,Constants.WINDOW_HEIGHT,false,true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(image));











    }
    public void moveSubcene(CustomMenuSubscene subcene){
        sceneMoveVoice();
        TranslateTransition translateTransition=new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(0.5));
        translateTransition.setNode(subcene);
        translateTransition.setToX(-1024+1024/8);
        translateTransition.play();


    }

    public void getStage(Stage gameStage){
        stage=gameStage;

    }


    private Parent createContent() {

        root.setPrefSize(1280, 720);


        VBox box = new VBox(
                30,
                new MenuItem("Mode Selection", () -> {PauseMenu.createGameModesButton();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }),
                new MenuItem("Mode of Play", () -> {PauseMenu.createModePlayButton();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }),
                new MenuItem("Choose Tank", () -> {PauseMenu.createChooseButton();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }),
                new MenuItem("Helper",()->{PauseMenu.createHelperButton();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }),

                new MenuItem("Scores",()->{PauseMenu.createScoresButton();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }),new MenuItem("Options", () -> {PauseMenu.createOptionsButton();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
             }),
                new MenuItem("QUIT", () -> {Platform.exit();
                    try {
                        clickVoice();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                })
        );


        box.setBackground(new Background(
                new BackgroundFill(Color.web("black", 0.6), null, null)
        ));

        box.setTranslateX(65);
        box.setTranslateY(110);

        root.getChildren().addAll(
                box
        );

        //分割
        MenuItem backToGame=new MenuItem("Back to Game",()->{
            try {
                clickVoice();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            moveBackScene();});

        backToGame.setTranslateX(600);
        backToGame.setTranslateY(30);
        backToGame.setBackground(new Background(
                new BackgroundFill(Color.web("black", 0.6), null, null)
        ));


        root.getChildren().addAll(backToGame);




        return root;
    }


    public void getGameScene(Scene scene){
        gameScene=scene;
        setEscEvent();
    }
    public void setEscEvent(){
        KeyCodeCombination keyCodeCombination=new KeyCodeCombination(KeyCode.ESCAPE);
        gameScene.getAccelerators().put(keyCodeCombination, new Runnable() {
            @Override
            public void run() {
                if (GameSession.isshown==true){
                System.out.println("press ESC");
                moveBackScene();}

            }
        });
    }


    private static class MenuItem extends StackPane {
        MenuItem(String name, Runnable action) {
            LinearGradient gradient = new LinearGradient(
                    0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0.1, Color.web("black", 0.75)),
                    new Stop(1.0, Color.web("black", 0.15))
            );
            Rectangle bg0 = new Rectangle(250, 30, gradient);
            Rectangle bg1 = new Rectangle(250, 30, Color.web("black", 0.2));

            FillTransition ft = new FillTransition(Duration.seconds(0.6),
                    bg1, Color.web("black", 0.2), Color.web("white", 0.3));

            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);

            hoverProperty().addListener((o, oldValue, isHovering) -> {
                if (isHovering) {
                    ft.playFromStart();
                } else {
                    ft.stop();
                    bg1.setFill(Color.web("black", 0.2));
                }
            });

            Rectangle line = new Rectangle(5, 30);
            line.widthProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(8).otherwise(5)
            );
            line.fillProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(Color.RED).otherwise(Color.GRAY)
            );

            Text text = new Text(name);
            text.setFont(Font.font(21.0));
            text.fillProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(Color.WHITE).otherwise(Color.GRAY)
            );

            setOnMouseClicked(e -> action.run());

            setOnMousePressed(e -> bg0.setFill(Color.LIGHTBLUE));

            setOnMouseReleased(e -> bg0.setFill(gradient));

            setAlignment(Pos.CENTER_LEFT);

            HBox box = new HBox(15, line, text);
            box.setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg0, bg1, box);
        }
    }





    public void sceneMoveVoice(){
        MusicThread musicThread=new MusicThread("src/main/resources/GUIResources/menuAppear.wav");
        musicThread.start();
    }

    public void moveBackScene(){

        TranslateTransition translateTransition=new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(0.5));
        translateTransition.setNode(this);
        translateTransition.setToX(1024);
        translateTransition.play();
        GameSession.isshown=false;
        sceneMoveVoice();

    }
    public static void showSubScene(ButtonSubscene subscene){
        if (CustomMenuSubscene.sceneToHide!=null){
            CustomMenuSubscene.sceneToHide.moveSubcene();

        }
        if (subscene==PauseMenu.chooseSubScene){
            subscene.moveSubcene("choose");

        }else {
            subscene.moveSubcene();

        }

        CustomMenuSubscene.sceneToHide=subscene;

    }










    public void clickVoice() throws MalformedURLException {
        MusicThread musicThread=new MusicThread("src/main/resources/GUIResources/clickVoice.wav");
        musicThread.start();

    }








    }






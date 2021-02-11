package sample;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;

public class First extends Application {
    public static String userNameInput;
    public static MediaPlayer mediaPlayer;
    public static Button playButton;
    public static Button rePlayButton;

    public static void main(String[] args) {
        launch(args);
    }

    public String DurationToString(Duration duration){
        int time = (int)duration.toSeconds();
        int hour = time /3600;
        int minute = (time-hour*3600)/60;
        int second = time %60;
        return hour + ":" + minute + ":" + second;
    }


    @Override
    public void start(Stage primaryStage) {
        // for mediaplayer start
        File url = new File("music/bgmusic6.wav");

        Media media = new Media(url.getAbsoluteFile().toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        Slider processSlider=new Slider();
        Label processLabel=new Label();
        playButton=new Button(">");
        rePlayButton=new Button(">>");
        Slider volumeSlider=new Slider();

        playButton.setOnAction(e->{
            String text=playButton.getText();
            if(text==">"){
                playButton.setText("||");
                mediaPlayer.play();
            }
            else {
                playButton.setText(">");
                mediaPlayer.pause();
            }
        });

        rePlayButton.setOnAction(e->{
            mediaPlayer.seek(Duration.ZERO);
        });

        volumeSlider.setValue(50);
        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

        Duration totalDuration = mediaPlayer.getTotalDuration();
        String totalString=DurationToString(totalDuration);
        double maxProcessSlider= processSlider.getMax();
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                processSlider.setValue(newValue.toSeconds() / totalDuration.toSeconds() * 100);
                processLabel.setText(DurationToString(newValue) + " " + DurationToString(mediaPlayer.getTotalDuration()));
            }
        });

        processSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                double totalTime=media.getDuration().toMillis();
                double newTime=processSlider.valueProperty().getValue()/100*totalTime;
                mediaPlayer.seek(Duration.millis(newTime));
            }
        });
        // for mediaplayer end




        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        userNameInput=Main.userTextField.getText();
        System.out.println("userName is "+userNameInput);
        primaryStage.setTitle("XXXX Game-Survival Mode   Player: "+userNameInput);
        // 使用BorderPane布局
        BorderPane borderPane = new BorderPane();
        MenuBar menuBar = new MenuBar();
//         --- Menu Modes
        Menu menuModes = new Menu("Mode Selection");
        MenuItem mode1 = new MenuItem("Survival Mode");
        mode1.setOnAction((ActionEvent t) -> {
            First.playButton.setText(">");
            First.mediaPlayer.pause();
            First first=new First(primaryStage);
            First.mediaPlayer.seek(Duration.ZERO);
            First.playButton.setText("||");
        });
        MenuItem mode2 = new MenuItem("Challenge Mode");
        mode2.setOnAction((ActionEvent t) -> {
            First.playButton.setText("");
            First.mediaPlayer.pause();
            Second second=new Second(primaryStage);
            Second.mediaPlayer.seek(Duration.ZERO);
            Second.playButton.setText("||");
        });
        MenuItem mode3 = new MenuItem("3rd Mode");
        mode3.setOnAction((ActionEvent t) -> {
            mediaPlayer.seek(Duration.ZERO);
            Third third=new Third(primaryStage);
        });
        menuModes.getItems().addAll(mode1, new SeparatorMenuItem(), mode2,new SeparatorMenuItem(),mode3);
        // --- Menu Helper
        Menu menuHelper = new Menu("Help");
        MenuItem help=new MenuItem("Game Rule");
        help.setOnAction((ActionEvent t) -> {
            new Alert(Alert.AlertType.NONE, "Game Rule: ", new ButtonType("Close")).show();
        });
        menuHelper.getItems().addAll(help);
        // --- Menu Option
        Menu menuOption = new Menu("Option");
        MenuItem quit=new MenuItem("Quit");
        quit.setOnAction((ActionEvent t) -> {
            System.exit(0);
        });
        MenuItem returnBack=new MenuItem("Return Back");
        returnBack.setOnAction((ActionEvent t) -> {
            primaryStage.hide();
            try {
                First.playButton.setText(">");
                First.mediaPlayer.pause();
                Main main=new Main(new Stage());
                Main.clip.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        MenuItem pause=new MenuItem("Pause");
        pause.setOnAction((ActionEvent t) -> {
            //handle event
        });
        MenuItem continueG=new MenuItem("Continue");
        continueG.setOnAction((ActionEvent t) -> {
            //handle event
        });
        menuOption.getItems().addAll(quit,new SeparatorMenuItem(),returnBack,new SeparatorMenuItem(),pause,new SeparatorMenuItem(),continueG);
        // -- Play Mode
        Menu menuPlay = new Menu("Play Option");
        MenuItem solo=new MenuItem("Play Solo");
        solo.setOnAction((ActionEvent t) -> {
            //handle event
        });
        MenuItem coop=new MenuItem("Play Co-op");
        coop.setOnAction((ActionEvent t) -> {
            //handle event
        });
        menuPlay.getItems().addAll(solo,new SeparatorMenuItem(),coop);
        // -- Scores
        Menu scores=new Menu("Scores");
        MenuItem score=new MenuItem("See the scores");
        score.setOnAction((ActionEvent t) -> {
            //handle event
            new Alert(Alert.AlertType.NONE, "Scores:\n\t\t"+userNameInput+":  "+"80 points", new ButtonType("Close")).show();
        });
        scores.getItems().addAll(score);
        menuBar.getMenus().addAll(menuModes,menuPlay,scores,menuOption,menuHelper);
        borderPane.setTop(menuBar);



        // for mediaplayer
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setId("mediaPlayer");
        hbox.getChildren().addAll(processSlider,processLabel,playButton,rePlayButton,volumeSlider);
        borderPane.setId("pane");
        borderPane.setBottom(hbox);
        borderPane.setCenter(mediaView);
        playButton.setText("||");
        mediaPlayer.play();






        // 1、init the scene
        Scene scene = new Scene(borderPane, 800, 700);

        // 2、put scene into stage
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("../Mode.css").toExternalForm());
        primaryStage.setResizable(false);
        // 3、open the stage
        primaryStage.show();



    }
    public First(Stage primaryStage) {

        //退出stage
        primaryStage.hide();
        Stage stage = new Stage();
        try {
            start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

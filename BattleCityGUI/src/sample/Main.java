package sample;

import Music.MusicThread;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Main extends Application {


    public static TextField userTextField;
    public static Boolean exit=false;
    public static AudioClip clip;


    public Main(Stage primaryStage) throws Exception {
        start(primaryStage);
    }
    public Main(){

    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });


        primaryStage.setTitle("XXXX Game");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("../login.css").toExternalForm());
        //根据target目录调整，默认位置在该文件在target目录所处地址
        //静态资源放resources里面

        primaryStage.setWidth(500);//设置宽度
        primaryStage.setHeight(666);//设置高度
        primaryStage.setMaxWidth(700);//最大宽度
        primaryStage.setMaxHeight(900);//最大高度
        primaryStage.setMinWidth(300);//最小宽度
        primaryStage.setMinHeight(300);//最小高度

        Text scenetitle2 = new Text(" Battle City");
        scenetitle2.setId("game-title");
        scenetitle2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle2, 0, 0, 2, 1);

        Text scenetitle = new Text("Welcome Venturer!");
        scenetitle.setId("welcome-text");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 2, 2, 1);
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 3);
        userTextField = new TextField();


//        userTextField.setText("please enter name here");
//        userTextField.setStyle("-fx-text-inner-color:  #808000;");
        grid.add(userTextField, 1, 3);
        Tooltip tooltip = new Tooltip();
        tooltip.setText(
                "Please enter your name "
        );
        userTextField.setTooltip(tooltip);


        Label gameMode = new Label("Game Mode:");
        grid.add(gameMode, 0, 4);
        ChoiceBox cb = new ChoiceBox();
        cb.setId("choicebox");
        cb.setItems(FXCollections.observableArrayList(
                "Survival Mode",
                new Separator(), "Challenge Mode", new Separator(), "3rd Mode")
        );
        cb.setTooltip(new Tooltip("Select the Mode of Game"));
        cb.getSelectionModel().selectFirst();
        grid.add(cb,1,4);


        Button btn = new Button("Start Game!");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");
        btn.setOnAction(new EventHandler<ActionEvent>() {


            @Override
            public void handle(ActionEvent e) {
                String name=userTextField.getText();
                System.out.println(name);


                //handle event
                if(name.equals("")){
                    new Alert(Alert.AlertType.NONE, "Please enter your name first! ", new ButtonType("Close")).show();
                    return;
                }
                clip.stop();
                if(cb.getValue()=="Survival Mode"){
                    First first=new First(primaryStage);
                }
                if(cb.getValue()=="Challenge Mode"){

                    Second second=new Second(primaryStage);
                }
                if(cb.getValue()=="3rd Mode"){

                    Third third=new Third(primaryStage);
                }


            }
        });


        userTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.ENTER) {
                    System.out.println("    You pressed Enter!");
                }

            }
        });
        userTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                String name=userTextField.getText();
                System.out.println(name);


                //handle event
                if(name.equals("")){
                    new Alert(Alert.AlertType.NONE, "Please enter your name first! ", new ButtonType("Close")).show();
                    return;
                }
                clip.stop();
                if(cb.getValue()=="Survival Mode"){
                    First first=new First(primaryStage);
                }
                if(cb.getValue()=="Challenge Mode"){

                    Second second=new Second(primaryStage);
                }
                if(cb.getValue()=="3rd Mode"){

                    Third third=new Third(primaryStage);
                }
            }}
        });

        //can not change the size
        primaryStage.setResizable(false);

        primaryStage.show();


    }


    public static void main(String[] args) throws MalformedURLException {
        File sound1=new File("music/bgmusic6.wav");//java只支持wav格式

        clip = Applet.newAudioClip(sound1.toURL());
        clip.loop();//播放
//        new MusicThread("music/bgmusic6.wav").start();
        launch(args);


    }
}

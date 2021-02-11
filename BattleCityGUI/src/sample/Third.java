package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Third extends Application {
    public static String userNameInput;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        userNameInput=Main.userTextField.getText();
        System.out.println("userName is "+userNameInput);
        primaryStage.setTitle("XXXX Game-3rd Mode   Player: "+userNameInput);
        // 使用BorderPane布局
        BorderPane borderPane = new BorderPane();
        MenuBar menuBar = new MenuBar();
//         --- Menu Modes
        Menu menuModes = new Menu("Mode Selection");
        MenuItem mode1 = new MenuItem("Survival Mode");
        mode1.setOnAction((ActionEvent t) -> {
            First first=new First(primaryStage);
        });
        MenuItem mode2 = new MenuItem("Challenge Mode");
        mode2.setOnAction((ActionEvent t) -> {
            Second second=new Second(primaryStage);
        });
        MenuItem mode3 = new MenuItem("3rd Mode");
        mode3.setOnAction((ActionEvent t) -> {
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
                Main main=new Main(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        MenuItem pause=new MenuItem("Pause");
        pause.setOnAction((ActionEvent t) -> {
            //逻辑处理
        });
        MenuItem continueG=new MenuItem("Continue");
        continueG.setOnAction((ActionEvent t) -> {
            //逻辑处理
        });
        menuOption.getItems().addAll(quit,new SeparatorMenuItem(),returnBack,new SeparatorMenuItem(),pause,new SeparatorMenuItem(),continueG);
        menuBar.getMenus().addAll(menuModes,menuHelper,menuOption);
        borderPane.setTop(menuBar);

        // 1、初始化一个场景
        Scene scene = new Scene(borderPane, 800, 700);
        // 2、将场景放入窗口
        primaryStage.setScene(scene);
        // 3、打开窗口
        primaryStage.show();



    }
    public Third(Stage primaryStage) {
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

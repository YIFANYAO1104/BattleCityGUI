package com.bham.bc.view.menu;

import com.bham.bc.view.GameSession;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.RecordsHandler;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;


/**
 * <h1>End Menu</h1>
 *
 * <p>Represents the end screen for a finished game session. End menu is observed every time
 * a player finishes a game. It asks the player to enter their name and saves their score of
 * a particular game mode</p>
 *
 * <b>Note:</b> the Menu asks for a name and allows to submit the score <i>only</i> if it makes
 * to the leaderboard of up to 10 highest all-time scores
 */
public class EndMenu extends AnchorPane {

    private MenuButton btnSubmit;
    public Rectangle bg;
    public static Stage primaryStage;
    private SubMenu subMenuEnd;

    private JSONObject jsObject;
    private double score;
    public static boolean isshown=false;

    public EndMenu() {
        setMinWidth(GameSession.WIDTH);
        setMinHeight(GameSession.HEIGHT);

        initBgDim();



    }
    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        bg = new Rectangle(GameSession.WIDTH, GameSession.HEIGHT);
        bg.setFill(Color.BLACK);
        bg.setOpacity(0.5);
        getChildren().add(bg);
    }

    /**
     * show when user get high socres
     */
    private void showWhenHigh() {
        VBox vBox=new VBox();
        Text tip=new Text("YOU GET HIGH SCORE!");
        tip.setStyle("-fx-fill: gold;-fx-font-size: 40px;-fx-font-weight: bold;");
        HBox hBox=new HBox();
        Text name=new Text("User name: ");
        name.setStyle("-fx-fill: white;-fx-font-size: 20px;-fx-font-weight: bold;");
        TextField userName=new TextField();
        hBox.getChildren().addAll(name,userName);

        btnSubmit = new MenuButton("Submit");
        btnSubmit.setOnMouseClicked(e -> { GameSession.gameStage.hide();
            MenuSession manager = new MenuSession();
            primaryStage = manager.getMainStage();
            System.out.println(userName.getText());
            primaryStage.show(); MainMenu.recordsHandler.createRecord(new RecordsHandler.Records("13",userName.getText(),score+"",RecordsHandler.getDate()));
            MainMenu.tableView.setItems(MainMenu.recordsHandler.sortAndGetData());
        });
        vBox.getChildren().addAll(tip,hBox,btnSubmit);
        vBox.setSpacing(30);
        vBox.setTranslateX(100);

        subMenuEnd = new SubMenu(this);
        subMenuEnd.getChildren().addAll(vBox);
    }

    /**
     * show when user get high socres
     */
    private void showWhenLow(){
        VBox vBox=new VBox();
        Text tip=new Text("SORRY, YOU GET LOW SCORE!");
        tip.setStyle("-fx-fill: gold;-fx-font-size: 40px;-fx-font-weight: bold;");
        MenuButton btnReturn = new MenuButton("RETURN TO MENU");
        btnReturn.setOnMouseClicked(e -> { GameSession.gameStage.hide();
            MenuSession manager = new MenuSession();
            primaryStage = manager.getMainStage();
            primaryStage.show(); });
        vBox.getChildren().addAll(tip,btnReturn);
        vBox.setSpacing(30);
        vBox.setTranslateX(60);
        subMenuEnd = new SubMenu(this);
        subMenuEnd.getChildren().addAll(vBox);

    }

    /**
     * Shows end menu with fade in transition
     * @param gamePane game pane menu will be attached to
     */
    public void show(AnchorPane gamePane,double sco) {
        isshown=true;
        gamePane.getChildren().add(this);
        score=sco;
        System.out.println(RecordsHandler.jsonArrayToFile.length());
        if (RecordsHandler.jsonArrayToFile.length()==0){
            showWhenHigh();
        }else {
        jsObject= (JSONObject) RecordsHandler.jsonArrayToFile.get(RecordsHandler.jsonArrayToFile.length()-1);
        if (Double.valueOf(jsObject.getString("score"))>score){
            System.out.println("low");
            showWhenLow();
        }else {
            System.out.println("high");
            showWhenHigh();
        } }

        FadeTransition ft = new FadeTransition(Duration.millis(300), bg);
        ft.setFromValue(0);
        ft.setToValue(0.7);

        ft.play();
        subMenuEnd.show();
    }



}

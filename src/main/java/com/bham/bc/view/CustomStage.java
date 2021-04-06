package com.bham.bc.view;

import com.bham.bc.utils.Constants;
import com.bham.bc.view.menu.PauseMenu;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.beans.value.ChangeListener;


/**
 * @author : YiFan Yaao
 * @version : 1.0
 * @project: BattleCityGUI
 * @name : CustomStageSubscene.java
 * @data : 2021/2/28
 * @time : 14:43
 */
public class CustomStage extends Stage{

    private Stage stage;
    private Scene gamescene;
    private double x = 0.00;
    private double y = 0.00;
    private double width = 0.00;
    private double height = 0.00;

    private boolean isMax = false;
    private boolean isRight;// 是否处于右边界调整窗口状态
    private boolean isBottomRight;// 是否处于右下角调整窗口状态
    private boolean isBottom;// 是否处于下边界调整窗口状态
    private double RESIZE_WIDTH = 5.00;
    private double MIN_WIDTH = 400.00;
    private double MIN_HEIGHT = 300.00;
    private double xOffset = 0, yOffset = 0;//自定义dialog移动横纵坐标
    public static String typeOf;
    private AnchorPane gamePane;
    public static Label setMenu;
    public static HBox gpTitle;
    public static String[] types;
    private HBox hBox;
    public static ChoiceBox changeMainSkin=new ChoiceBox(FXCollections.observableArrayList(
            "Classic Blue","Classic Green","Classic Black","Classic Orange"
    ));;
    public static ChoiceBox changePauseSkin=new ChoiceBox(FXCollections.observableArrayList(
            "Classic Blue","Classic Green","Classic Black","Classic Orange"
    ));;
    private Label btnMin;
    private Label btnClose;
    public static int selected=0;




    public CustomStage(Stage gameStage, Scene gameScene,AnchorPane pane) {
        stage=gameStage;
        gamescene=gameScene;
        gamePane=pane;

    }

    public void startMenuIcon(){
        setMenu = new Label("");
        setMenu.setId("setMenu");
        setMenu.setPrefWidth(29);
        setMenu.setPrefHeight(40);



        setMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                  MenuSession.showOptionsMenu(gamePane,GameSession.gameTimer);
            }
        });
        setMenu.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.ENTER) {
                    System.out.println("    You pressed Enter!");
                }

            }
        });







    }

    /**
     * create titler bar template
     * @param root
     * @param Width
     * @param Height
     * @param offset
     */

    public void titleBar(AnchorPane root, int Width, int Height,int offset,ChoiceBox changeSkin){

        stage.initStyle(StageStyle.TRANSPARENT);

        gpTitle = new HBox();
        gpTitle.setId("title");
        gpTitle.setSpacing(4);
        gpTitle.setPadding(new Insets(15,5,17,5));
        Label lbTitle = new Label("Battle City");
        lbTitle.setId("name");
        Glow glow=new Glow();
        lbTitle.setEffect(glow);
        glow.setLevel(0.6);

        gpTitle.setMinWidth(Width);
        gpTitle.setMaxWidth(Height);
        gpTitle.setMinHeight(15);
        gpTitle.setMaxHeight(35);
        gpTitle.getStylesheets().add(CustomStage.class.getResource("../../../../GUIResources/Style.css").toExternalForm());


        btnMin = new Label();
        btnMin.setPrefWidth(33);
        btnMin.setPrefHeight(26);

        BackgroundImage image2=new BackgroundImage(new Image("file:src/main/resources/GUIResources/Min.png",24,10,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);

        btnMin.setBackground(new Background(image2));

        btnClose = new Label();
        btnClose.setPrefWidth(33);
        btnClose.setPrefHeight(26);


        types=new String[]{"TYPE 1","TYPE 2","TYPE 3","TYPE 4","TYPE 5"};

        changeSkin.setId("changeSkin");
        changeSkin.setMaxSize(25,22);
        changeSkin.setMinSize(25,22);
        BackgroundImage image3=new BackgroundImage(new Image("file:src/main/resources/GUIResources/skinSet.png",25,22,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);
        changeSkin.setBackground(new Background(image3));
        changeSkin.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov,Number old_val,Number new_val)->{

            selected=new_val.intValue();
            typeOf=types[selected];
            if (typeOf.equals("TYPE 1")){
                gpTitle.setStyle(" -fx-background-color: linear-gradient(#2a3ade, #091376);");
                lbTitle.requestFocus();
                if (changeSkin==changePauseSkin){
                    PauseMenu.changeSkin.getSelectionModel().select(0);
                }





            }else if (typeOf.equals("TYPE 2")){


                gpTitle.setStyle(" -fx-background-color: linear-gradient(#61a2b1, #2A5058);");
                lbTitle.requestFocus();
                if (changeSkin==changePauseSkin){
                    PauseMenu.changeSkin.getSelectionModel().select(1);
                }



            }else if (typeOf.equals("TYPE 3")){

                gpTitle.setStyle(" -fx-background-color: linear-gradient(#636060, #000000);");
                lbTitle.requestFocus();
                if (changeSkin==changePauseSkin){
                    PauseMenu.changeSkin.getSelectionModel().select(2);
                }



            }else if (typeOf.equals("TYPE 4")){
                gpTitle.setStyle(" -fx-background-color: linear-gradient(#d4b288, #de8709);");

                lbTitle.requestFocus();
                if (changeSkin==changePauseSkin){
                    PauseMenu.changeSkin.getSelectionModel().select(3);
                }



            }



        });
        changeSkin.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                lbTitle.requestFocus();;
            }
        });






        btnClose.setId("winClose");
        btnMin.setId("winMin");
        //-540 -760
        lbTitle.setTranslateX(offset);
        btnMin.setTranslateY(6);
        btnMin.setTranslateX(3);



        if (setMenu==null){
            hBox=new HBox(5,changeSkin,btnMin,btnClose);
        }else {
            hBox=new HBox(5,setMenu,changeSkin,btnMin,btnClose);
        }

        gpTitle.getChildren().addAll(lbTitle,hBox);

        root.getChildren().add(gpTitle);

        gpTitle.setLayoutX(0);
        gpTitle.setLayoutY(0);
        gpTitle.setAlignment(Pos.CENTER_RIGHT);



        btnMin.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setIconified(true);
            }



        });

        btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.close();
            }
        });

        stage.xProperty().addListener(new ChangeListener<Number>() {


            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    x = newValue.doubleValue();
                }
            }
        });
        stage.yProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    y = newValue.doubleValue();
                }
            }
        });
        stage.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    width = newValue.doubleValue();
                }
            }
        });
        stage.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && !isMax) {
                    height = newValue.doubleValue();
                }
            }
        });

        root.setOnMouseMoved((MouseEvent event) -> {
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
            // 先将所有调整窗口状态重置
            isRight = isBottomRight = isBottom = false;
            if (y >= height - RESIZE_WIDTH) {
                if (x <= RESIZE_WIDTH) {// 左下角调整窗口状态
                    //不处理

                } else if (x >= width - RESIZE_WIDTH) {// 右下角调整窗口状态
                    isBottomRight = true;
                    cursorType = Cursor.SE_RESIZE;
                } else {// 下边界调整窗口状态
                    isBottom = true;
                    cursorType = Cursor.S_RESIZE;
                }
            } else if (x >= width - RESIZE_WIDTH) {// 右边界调整窗口状态
                isRight = true;
                cursorType = Cursor.E_RESIZE;
            }
            // 最后改变鼠标光标
            root.setCursor(cursorType);
        });

        root.setOnMouseDragged((MouseEvent event) -> {

            //根据鼠标的横纵坐标移动dialog位置
            event.consume();
            if (yOffset != 0 ) {
                stage.setX(event.getScreenX() - xOffset);
                if (event.getScreenY() - yOffset < 0) {
                    stage.setY(0);
                } else {
                    stage.setY(event.getScreenY() - yOffset);
                }
            }

            double x = event.getSceneX();
            double y = event.getSceneY();
            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = stage.getX();
            double nextY = stage.getY();
            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();
            if (isRight || isBottomRight) {// 所有右边调整窗口状态
                nextWidth = x;
            }
            if (isBottomRight || isBottom) {// 所有下边调整窗口状态
                nextHeight = y;
            }
            if (nextWidth <= MIN_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                nextWidth = MIN_WIDTH;
            }
            if (nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
                nextHeight = MIN_HEIGHT;
            }
            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            stage.setX(nextX);
            stage.setY(nextY);
            stage.setWidth(nextWidth);
            stage.setHeight(nextHeight);

        });
        //鼠标点击获取横纵坐标
        root.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            if (event.getSceneY() > 46) {
                yOffset = 0;
            } else {
                yOffset = event.getSceneY();
            }
        });


    }

    /**
     * create title bar(without options menu icon) to main interface
     * @param root
     * @param Width
     * @param Height
     */

    public void createCommonTitlebar(AnchorPane root, int Width, int Height){
           titleBar(root,Width,Height,-780,changeMainSkin);
           changeMainSkin.getSelectionModel().select(selected);


    }

    /**
     * create title bar(with options Menu icon) to gameSession
     * @param root
     * @param Width
     * @param Height
     */

    public void createTitleBar(AnchorPane root, int Width, int Height){
        startMenuIcon();
        titleBar(root,Width,Height,-540,changePauseSkin);
        changePauseSkin.getSelectionModel().select(selected);







    }

}

package com.bham.bc.view;

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


    private boolean isMax = false;
    private boolean isRight;
    private boolean isBottomRight;
    private boolean isBottom;
    private double RESIZE_WIDTH = 5.00;
    private double MIN_WIDTH = 400.00;
    private double MIN_HEIGHT = 300.00;
    private double xOffset = 0, yOffset = 0;
    public static String typeOf;
    private AnchorPane gamePane;
    public static Label setMenu;
    public static HBox gpTitle;
    public static String[] types;
    private HBox hBox;
    public  ChoiceBox changeMainSkin=new ChoiceBox(FXCollections.observableArrayList(
            "Classic Blue","Classic Green","Classic Black","Classic Orange"
    ));;
    public  ChoiceBox changePauseSkin=new ChoiceBox(FXCollections.observableArrayList(
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



    /**
     * create titler bar template
     * @param root
     * @param Width width of title bar
     * @param offset offset of game name
     */

    public void titleBar(AnchorPane root, int Width,int offset,ChoiceBox changeSkin){

        stage.initStyle(StageStyle.TRANSPARENT);

        gpTitle = new HBox();
        gpTitle.setId("title");
        gpTitle.setSpacing(4);
        gpTitle.setPadding(new Insets(15,5,17,5));
        Label lbTitle = new Label(stage.getTitle());
        lbTitle.setId("name");
        Glow glow=new Glow();
        lbTitle.setEffect(glow);
        glow.setLevel(0.6);

        gpTitle.setMinWidth(Width);

        gpTitle.setMinHeight(35);
        gpTitle.setMaxHeight(35);
        gpTitle.getStylesheets().add(CustomStage.class.getResource("../../../../style.css").toExternalForm());


        btnMin = new Label();
        btnMin.setPrefWidth(33);
        btnMin.setPrefHeight(26);

        BackgroundImage image2=new BackgroundImage(new Image("file:src/main/resources/img/menu/minimize.png",24,10,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);

        btnMin.setBackground(new Background(image2));

        btnClose = new Label();
        btnClose.setPrefWidth(33);
        btnClose.setPrefHeight(26);


        types=new String[]{"TYPE 1","TYPE 2","TYPE 3","TYPE 4","TYPE 5"};

        changeSkin.setId("changeSkin");
        changeSkin.setMaxSize(25,22);
        changeSkin.setMinSize(25,22);
        BackgroundImage image3=new BackgroundImage(new Image("file:src/main/resources/img/menu/skin.png",25,22,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);
        changeSkin.setBackground(new Background(image3));
        Glow glow1=new Glow();
        glow1.setLevel(1);
        changeSkin.setEffect(glow1);
        btnMin.setEffect(glow1);
        btnClose.setEffect(glow1);
        changeSkin.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov,Number old_val,Number new_val)->{

            selected=new_val.intValue();
            typeOf=types[selected];
            if (typeOf.equals("TYPE 1")){
                gpTitle.setStyle(" -fx-background-color: linear-gradient(to right, #334e9e 0%, #06065f 50%, #334e9e 100%);");
                lbTitle.requestFocus();






            }else if (typeOf.equals("TYPE 2")){


                gpTitle.setStyle(" -fx-background-color: linear-gradient(to right,#61a2b1 0%, #2A5058 50%,#61a2b1 100%);");
                lbTitle.requestFocus();




            }else if (typeOf.equals("TYPE 3")){

                gpTitle.setStyle(" -fx-background-color: linear-gradient(to right,#636060 0%, #000000 50%,#636060 100% );");
                lbTitle.requestFocus();




            }else if (typeOf.equals("TYPE 4")){
                gpTitle.setStyle(" -fx-background-color: linear-gradient(to right,#bb741b 0%, #552d03 50%,#bb741b 100%);");

                lbTitle.requestFocus();




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

        VBox titleAndRoot = new VBox();
        root.getScene().setRoot(titleAndRoot);
        titleAndRoot.getChildren().addAll(gpTitle, root);
        stage.setMinHeight(root.getScene().getHeight() + gpTitle.getMinHeight());
//        root.getChildren().add(gpTitle);

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







        titleAndRoot.setOnMouseDragged((MouseEvent event) -> {


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

            double nextX = stage.getX();
            double nextY = stage.getY();
            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();
            if (isRight || isBottomRight) {
                nextWidth = x;
            }
            if (isBottomRight || isBottom) {
                nextHeight = y;
            }
            if (nextWidth <= MIN_WIDTH) {
                nextWidth = MIN_WIDTH;
            }
            if (nextHeight <= MIN_HEIGHT) {
                nextHeight = MIN_HEIGHT;
            }

            stage.setY(nextY);
            stage.setWidth(nextWidth);
            stage.setHeight(nextHeight);

        });

        titleAndRoot.setOnMousePressed(event -> {
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
     * create title bar to main interface
     * @param root
     * @param Width width of title bar
     */

    public void createCommonTitlebar(AnchorPane root, int Width){
           titleBar(root,Width,-310,changeMainSkin);
           changeMainSkin.getSelectionModel().select(selected);


    }

    /**
     * create title bar to game interface
     * @param root
     * @param Width width of title bar
     */

    public void createTitleBar(AnchorPane root, int Width){

        titleBar(root,Width,-190,changePauseSkin);
        changePauseSkin.getSelectionModel().select(selected);







    }

}

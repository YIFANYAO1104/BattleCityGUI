package com.bham.bc.view.menuButtons;

import com.bham.bc.utils.Constants;
import com.bham.bc.view.GameSession;
import com.bham.bc.view.menu.CustomMenuSubscene;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * @author : YiFan Yaao
 * @version : 1.0
 * @project: BattleCityGUI
 * @name : BattleCitySubscenes.java
 * @data : 2021/2/26
 * @time : 20:53
 */
public class ButtonSubscene extends SubScene{

    private boolean ishidden;

    public ButtonSubscene() {
        super(new AnchorPane(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        prefHeight(Constants.WINDOW_WIDTH);
        prefWidth(Constants.WINDOW_HEIGHT);
        AnchorPane root2= (AnchorPane) this.getRoot();
        BackgroundImage image=new BackgroundImage(new Image("file:src/main/resources/GUIResources/img_3.png",330,450,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);
        root2.setBackground(new Background(image));
        setLayoutX(1024);
        setLayoutY(70);
        ishidden=true;

    }
    public ButtonSubscene(String choose){
        super(new AnchorPane(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        prefHeight(Constants.WINDOW_WIDTH);
        prefWidth(Constants.WINDOW_HEIGHT);
        AnchorPane root2= (AnchorPane) this.getRoot();
        BackgroundImage image=new BackgroundImage(new Image("file:src/main/resources/GUIResources/img_3.png",530,450,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);
        root2.setBackground(new Background(image));
        setLayoutX(1024);
        setLayoutY(70);
        ishidden=true;}

    public void fadeInSubcene(CustomMenuSubscene subcene){
        //Instantiating FadeTransition class
        FadeTransition fade = new FadeTransition();


        //setting the duration for the Fade transition
        fade.setDuration(Duration.millis(2000));

        //setting the initial and the target opacity value for the transition
        fade.setFromValue(0);
        fade.setToValue(10);

        //the transition will set to be auto reversed by setting this to true
        fade.setAutoReverse(false);
        //setting Circle as the node onto which the transition will be applied
        fade.setNode(subcene);

        //playing the transition
        fade.play();
        GameSession.isshown=true;
    }


    public void moveSubcene(){
        TranslateTransition translateTransition=new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(0.3));
        translateTransition.setNode(this);

        if (ishidden){
            translateTransition.setToX(-724);
            translateTransition.setToY(10);
            ishidden=false;



        }else{
            translateTransition.setToX(0);
            ishidden=true;




        }

        translateTransition.play();

    }
    public void moveSubcene(String choose){
        TranslateTransition translateTransition=new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(0.3));
        translateTransition.setNode(this);

        if (ishidden){
            translateTransition.setToX(-794);

            ishidden=false;



        }else{
            translateTransition.setToX(0);
            ishidden=true;
        }

        translateTransition.play();

    }
    public AnchorPane getPane(){
        return (AnchorPane) this.getRoot();
    }
}

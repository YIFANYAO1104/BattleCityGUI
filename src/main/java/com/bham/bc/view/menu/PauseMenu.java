package com.bham.bc.view.menu;

import com.bham.bc.utils.Constants;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * <h1>Pause Menu</h1>
 *
 * <p>Represents an in-game menu that is observed whenever a game session asks to pause the game.
 * The menu allows to resume, change the sound settings, and return to the main menu.</p>
 *
 * <b>Note:</b> neither state, nor the score of the game is saved when returning to the main menu.
 */
public class PauseMenu extends AnchorPane{

    private MenuSession menuSession;
    private MenuButton btnResume;       // resumes the current game
    private MenuButton btnSettings;     // opens configuration menu
    private MenuButton btnEndGame;      // returns to the main menu
    private MenuButton btnOptions;
    private MenuButton btnVolume;
    private MenuButton btnEffect;
    private MenuButton btnSkin;

    private AnchorPane parent;          // Parent node to attach the pause menu

    public static SubMenu subMenuPause;
    private SubMenu subMenuOptions;
    private Rectangle bg;
    public static boolean isshown=false;
    private VBox vBox;



    public PauseMenu(MenuSession menuSession) {

        this.menuSession = menuSession;

        this.setLayoutX(0);
        this.setLayoutY(0);

        initBgDim();
        createSubMenuPause();
        createSubMenuOptions();
        fadeIn();


    }

    private void createSubMenuPause() {
        vBox=new VBox(15,btnResume=new MenuButton("Resume"),
        btnSettings=new MenuButton("Settings"),
        btnEndGame=new MenuButton("Quit"), btnOptions=new MenuButton("Options"));
        vBox.setTranslateX(530);
        vBox.setTranslateY(430);


        btnSettings.setOnMouseClicked(e->{});
        btnEndGame.setOnMouseClicked(e->{});
        btnResume.setOnMouseClicked(e->{});
        btnOptions.setOnMouseClicked(e->{subMenuPause.hide();subMenuOptions.show();});
        
        subMenuPause=new SubMenu(this);
        subMenuPause.getChildren().addAll(vBox);


    }
    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        bg = new Rectangle(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        bg.setFill(Color.BLACK);
        bg.setOpacity(0.5);
        getChildren().add(bg);
    }

    private void createSubMenuOptions(){
        VBox vBox=new VBox(15,btnVolume=new MenuButton("Volume"),btnEffect=new MenuButton("Effect"),btnSkin=new MenuButton("Skin"));
        btnEffect.setOnMouseClicked(e->{});
        btnVolume.setOnMouseClicked(e->{});
        btnSkin.setOnMouseClicked(e->{});
        vBox.setTranslateX(0);
        vBox.setTranslateY(430);
        subMenuOptions=new SubMenu(this);
        subMenuOptions.getChildren().addAll(vBox);

    }
    public void fadeIn(){
        //Instantiating FadeTransition class
        FadeTransition fade1 = new FadeTransition();


        //setting the duration for the Fade transition
        fade1.setDuration(Duration.millis(1000));

        //setting the initial and the target opacity value for the transition
        fade1.setFromValue(0);
        fade1.setToValue(0.7);

        //the transition will set to be auto reversed by setting this to true
        fade1.setAutoReverse(false);
        //setting Circle as the node onto which the transition will be applied
        fade1.setNode(bg);

        //playing the transition
        fade1.play();
        subMenuPause.show();

        isshown=true;



    }

    public void fadeOut(){

        //Instantiating FadeTransition class
        FadeTransition fade2 = new FadeTransition();


        //setting the duration for the Fade transition
        fade2.setDuration(Duration.millis(1000));

        //setting the initial and the target opacity value for the transition
        fade2.setFromValue(0.7);
        fade2.setToValue(0);

        //the transition will set to be auto reversed by setting this to true
        fade2.setAutoReverse(false);
        //setting Circle as the node onto which the transition will be applied
        fade2.setNode(bg);

        //playing the transition
        fade2.play();
        subMenuPause.hide();
        subMenuOptions.hide();
        isshown=false;

    }




}

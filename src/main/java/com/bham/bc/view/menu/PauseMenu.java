package com.bham.bc.view.menu;

import com.bham.bc.utils.Constants;
import com.bham.bc.view.CustomStage;
import com.bham.bc.view.GameSession;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuSlider;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;

import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * <h1>Pause Menu</h1>
 *
 * <p>Represents an in-game menu that is observed whenever a game session asks to pause the game.
 * The menu allows to resume, change the sound settings, and return to the main menu.</p>
 *
 * <b>Note:</b> neither state, nor the score of the game is saved when returning to the main menu.
 */
public class PauseMenu extends AnchorPane {

    private SubMenu subMenuPause;
    public SubMenu subMenuOptions;
    public Rectangle bg;
    public static ChoiceBox changeSkin;


    /**
     * Constructs a pause menu based on Game window's size parameters
     */
    public PauseMenu() {
        setWidth(Constants.WINDOW_WIDTH);
        setHeight(Constants.WINDOW_HEIGHT);

        initBgDim();
        createSubMenuPause();
        createSubMenuOptions();
    }

    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        bg = new Rectangle(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        bg.setFill(Color.BLACK);
        bg.setOpacity(0.5);
        getChildren().add(bg);
    }

    /**
     * Creates layout for primary view for pause menu
     */
    private void createSubMenuPause() {
        MenuButton btnResume = new MenuButton("Resume");
        MenuButton btnOptions = new MenuButton("Options");
        MenuButton btnEndGame = new MenuButton("Quit");

        btnResume.setOnMouseClicked(e->{MenuSession.showPauseMenu(GameSession.gamePane,GameSession.gameTimer);});
        btnOptions.setOnMouseClicked(e->{subMenuPause.hide();subMenuOptions.show();});
        btnEndGame.setOnMouseClicked(e->{System.exit(0);});

        subMenuPause = new SubMenu(this);
        subMenuPause.getChildren().addAll(btnResume, btnOptions, btnEndGame);
    }

    /**
     * Creates layout for options in the pause menu
     */
    private void createSubMenuOptions() {
        MenuSlider bg=new MenuSlider("Volume:");

        DoubleProperty doubleProperty1 = bg.getValueProperty();
        doubleProperty1.addListener((obsVal, oldVal, newVal) -> {
            audioManager.setMusicVolume(newVal.doubleValue()/100);
            bg.getNumOfVolume().setText(newVal.intValue()+"%");
            bg.setSliderStyle();
        });
        MenuSlider sfx=new MenuSlider("SFX Volume:");

        DoubleProperty doubleProperty2=sfx.getValueProperty();
        doubleProperty2.addListener((obsVal, oldVal, newVal) -> {
            audioManager.setEffectVolume(newVal.doubleValue()/100);
            sfx.getNumOfVolume().setText(newVal.intValue()+"%");
            sfx.setSliderStyle();

        });
        createSkinChoose();
        Label skin=new Label("Skin Choose: ");
        skin.setStyle(" -fx-font-size: 18px;\n" +
                "-fx-text-fill: white;\n"+
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;\n" +
                "\n" +
                "    -fx-stroke: gold;");
        HBox hBox=new HBox(skin,changeSkin);



        MenuButton btnback = new MenuButton("Back");


        btnback.setOnMouseClicked(e->{subMenuOptions.hide();subMenuPause.show();});


        subMenuOptions = new SubMenu(this);
        subMenuOptions.getChildren().addAll(bg, sfx, hBox,btnback);
    }

    /**
     * Shows pause menu with fade in transition
     * @param gamePane game pane menu will be attached to
     */
    public void show(AnchorPane gamePane) {
        gamePane.getChildren().add(this);

        FadeTransition ft = new FadeTransition(Duration.millis(300), bg);
        ft.setFromValue(0);
        ft.setToValue(0.7);

        ft.play();
        subMenuPause.show();
    }

    /**
     * Shows options menu with fade in transition
     * @param gamePane game pane menu will be attached to
     */
    public void showOptionsMenu(AnchorPane gamePane) {
        gamePane.getChildren().add(this);

        FadeTransition ft = new FadeTransition(Duration.millis(300), bg);
        ft.setFromValue(0);
        ft.setToValue(0.7);

        ft.play();
        subMenuOptions.show();
    }



    /**
     * Hides pause menu with fade out transition
     * @param gamePane game pane menu will be detached from
     */
    public void hide(AnchorPane gamePane) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), bg);
        ft.setFromValue(0.7);
        ft.setToValue(0);

        ft.play();
        subMenuOptions.hide();
        subMenuPause.hide();

        ft.setOnFinished(e -> gamePane.getChildren().remove(this));
    }

    /**
     * to choose the skin style from pauseMenu
     */
    public void createSkinChoose(){
        CustomStage.types=new String[]{"TYPE 1","TYPE 2","TYPE 3","TYPE 4","TYPE 5"};
        changeSkin=new ChoiceBox(FXCollections.observableArrayList(
                "Classic Blue","Classic Green","Classic Black","Classic Orange","Classic Gold"
        ));

        changeSkin.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov,Number old_val,Number new_val)->{


            CustomStage.typeOf =  CustomStage.types[new_val.intValue()];
            if ( CustomStage.typeOf.equals("TYPE 1")){


                CustomStage.changePauseSkin.getSelectionModel().select(0);


            }else if ( CustomStage.typeOf.equals("TYPE 2")){


                CustomStage.changePauseSkin.getSelectionModel().select(1);

            }else if ( CustomStage.typeOf.equals("TYPE 3")){


                CustomStage.changePauseSkin.getSelectionModel().select(2);

            }else if ( CustomStage.typeOf.equals("TYPE 4")){


                CustomStage.changePauseSkin.getSelectionModel().select(3);

            }else if ( CustomStage.typeOf.equals("TYPE 5")){

                CustomStage.changePauseSkin.getSelectionModel().select(4);

            }



        });
        changeSkin.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                CustomStage.setMenu.requestFocus();
            }
        });
    }







}

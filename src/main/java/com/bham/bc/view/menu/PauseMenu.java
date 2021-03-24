package com.bham.bc.view.menu;

import com.bham.bc.utils.Constants;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private SubMenu subMenuOptions;
    private Rectangle bg;

    private VBox vBox;


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
        bg = new Rectangle(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
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

        btnResume.setOnMouseClicked(e->{});
        btnOptions.setOnMouseClicked(e->{subMenuPause.hide();subMenuOptions.show();});
        btnEndGame.setOnMouseClicked(e->{});

        subMenuPause = new SubMenu(this);
        subMenuPause.getChildren().addAll(btnResume, btnOptions, btnEndGame);

        subMenuPause.setTranslateY(430);

        getChildren().add(subMenuPause);
        subMenuPause.hide();
    }

    /**
     * Creates layout for options in the pause menu
     */
    private void createSubMenuOptions() {
        volumeSlider();
        MenuButton btnEffect = new MenuButton("Effect");
        MenuButton btnSkin = new MenuButton("Skin");

        btnEffect.setOnMouseClicked(e->{});
        btnSkin.setOnMouseClicked(e->{});

        subMenuOptions = new SubMenu(this);
        subMenuOptions.getChildren().addAll(vBox, btnEffect, btnSkin);

        subMenuOptions.setTranslateY(430);

        getChildren().add(subMenuOptions);
        subMenuOptions.hide();
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

    public void volumeSlider(){
        Slider volumeSlider = new Slider();
        volumeSlider.setValue(100);

        Label num=new Label((int)volumeSlider.valueProperty().getValue().doubleValue()+"");
        num.setStyle(" -fx-font-size: 15px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;");

        volumeSlider.valueProperty().addListener((obsVal, oldVal, newVal) -> {
            audioManager.setMusicVolume(newVal.doubleValue()/100);
            num.setText(newVal.intValue() + "");
        });

        HBox HBox=new HBox(volumeSlider,num);

        Label volume=new Label("Volume:");

        volume.setStyle(" -fx-font-size: 25px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;");
        vBox=new VBox(volume,HBox);
    }
}

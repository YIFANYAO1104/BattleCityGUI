package com.bham.bc.view.menu;

import com.bham.bc.application.Main;
import com.bham.bc.utils.Constants;
import com.bham.bc.view.CustomStage;
import com.bham.bc.view.GameSession;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuSlider;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public SubMenu subMenuSettings;
    public Rectangle bg;
    public static Stage primaryStage;


    /**
     * Constructs a pause menu based on Game window's size parameters
     */
    public PauseMenu() {
        setMinWidth(Constants.WINDOW_WIDTH);
        setMinHeight(Constants.WINDOW_HEIGHT);

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
        MenuButton btnResume = new MenuButton("RESUME");
        MenuButton btnSettings = new MenuButton("SETTINGS");
        MenuButton btnReturn = new MenuButton("RETURN TO MENU");

        btnResume.setOnMouseClicked(e -> { MenuSession.showPauseMenu(GameSession.gamePane,GameSession.gameTimer); });
        btnSettings.setOnMouseClicked(e -> { subMenuPause.hide(); subMenuSettings.show(); });
        btnReturn.setOnMouseClicked(e -> { GameSession.gameStage.hide();
            System.out.println(CustomStage.selected);MenuSession manager = new MenuSession();
            primaryStage = manager.getMainStage();
            primaryStage.show(); });

        subMenuPause = new SubMenu(this);
        subMenuPause.getChildren().addAll(btnResume, btnSettings, btnReturn);
    }

    /**
     * Creates layout for options in the pause menu
     */
    private void createSubMenuOptions() {
        MenuSlider musicVolume = new MenuSlider("MUSIC", 100);
        MenuSlider sfxVolume = new MenuSlider("EFFECTS", 100);
        MenuButton btnBack = new MenuButton("BACK");

        musicVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));
        sfxVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setEffectVolume(newVal.doubleValue()/100));
        btnBack.setOnMouseClicked(e -> { subMenuSettings.hide(); subMenuPause.show(); });






        subMenuSettings = new SubMenu(this);
        subMenuSettings.getChildren().addAll(musicVolume, sfxVolume, btnBack);
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
        subMenuSettings.show();
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
        subMenuSettings.hide();
        subMenuPause.hide();

        ft.setOnFinished(e -> gamePane.getChildren().remove(this));
    }


}

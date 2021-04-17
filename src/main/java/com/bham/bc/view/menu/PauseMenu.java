package com.bham.bc.view.menu;

import com.bham.bc.view.GameSession;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuSlider;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
        setMinWidth(GameSession.WIDTH);
        setMinHeight(GameSession.HEIGHT);

        initBgDim();
        createSubMenuPause();
        createSubMenuSettings();

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
     * Creates layout for primary view for pause menu
     */
    private void createSubMenuPause() {
        MenuButton btnResume = new MenuButton("RESUME");
        MenuButton btnSettings = new MenuButton("SETTINGS");
        MenuButton btnReturn = new MenuButton("RETURN TO MENU");

        btnResume.setOnMouseClicked(e -> { MenuSession.showPauseMenu(GameSession.gamePane,GameSession.gameTimer); });
        btnSettings.setOnMouseClicked(e -> { subMenuPause.hide(); subMenuSettings.show(); });
        btnReturn.setOnMouseClicked(e -> { GameSession.gameStage.hide();
            MenuSession manager = new MenuSession();
            primaryStage = manager.getMainStage();
            primaryStage.show(); });

        subMenuPause = new SubMenu(this);
        subMenuPause.getChildren().addAll(btnResume, btnSettings, btnReturn);
    }

    /**
     * Creates layout for options in the pause menu
     */
    private void createSubMenuSettings() {
        MenuSlider musicVolume = new MenuSlider("MUSIC", 100);
        MenuSlider sfxVolume = new MenuSlider("EFFECTS", 100);
        MenuButton btnBack = new MenuButton("BACK");

        musicVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));
        sfxVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setEffectsVolume(newVal.doubleValue()/100));
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

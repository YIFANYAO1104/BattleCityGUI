package com.bham.bd.view.menu;

import com.bham.bd.view.GameSession;
import com.bham.bd.view.tools.GameFlowEvent;
import com.bham.bd.view.model.MenuButton;
import com.bham.bd.view.model.MenuSlider;
import com.bham.bd.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.bham.bd.audio.AudioManager.audioManager;

/**
 * <h1>Pause Menu</h1>
 *
 * <p>Represents an in-game menu that is observed whenever a game session asks to pause the game.
 * The menu allows to resume, change the sound settings, and return to the main menu.</p>
 *
 * <p><b>Note:</b> neither state, nor the score of the game is saved when returning to the main menu.</p>
 */
public class PauseMenu extends AnchorPane {
    /** Event to be fired if "Resume" is clicked */
    private final GameFlowEvent PAUSE_GAME_EVENT;

    /** Event to be fired if "Return to Menu" is clicked */
    private final GameFlowEvent LEAVE_GAME_EVENT;

    /** {@link SubMenu} containing elements to represent the main layout of the pause menu */
    private SubMenu subMenuPause;

    /** {@link SubMenu} containing elements to represent the settings layout of the pause menu */
    public SubMenu subMenuSettings;

    /** Background dim to make game layout stand out less and pause menu layout more */
    public Rectangle dim;

    /**
     * Constructs an {@link AnchorPane} layout as the Pause Menu and initialize Pause Menu
     */
    public PauseMenu() {
        PAUSE_GAME_EVENT = new GameFlowEvent(GameFlowEvent.PAUSE_GAME);
        LEAVE_GAME_EVENT = new GameFlowEvent(GameFlowEvent.LEAVE_GAME);
        setMinSize(GameSession.WIDTH, GameSession.HEIGHT);
        initBgDim();
        createSubMenuPause();
        createSubMenuSettings();
    }


    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        dim = new Rectangle(GameSession.WIDTH, GameSession.HEIGHT);
        dim.setFill(Color.BLACK);
        dim.setOpacity(0.5);
        getChildren().add(dim);
    }

    /**
     * <p>Creates layout for primary view for pause menu.</p>
     * <p>use custom menu button ({@link MenuButton}).</p>
     * <p>Pause Menu has three buttons, e.g. resume button, setting button and return button. </p>
     */
    private void createSubMenuPause() {
        MenuButton btnResume = new MenuButton("RESUME");
        MenuButton btnSettings = new MenuButton("SETTINGS");
        MenuButton btnReturn = new MenuButton("RETURN TO MENU");

        btnResume.setOnMouseClicked(e -> btnResume.fireEvent(PAUSE_GAME_EVENT));
        btnSettings.setOnMouseClicked(e -> { subMenuPause.hide(); subMenuSettings.show(); });
        btnReturn.setOnMouseClicked(e -> btnReturn.fireEvent(LEAVE_GAME_EVENT));

        subMenuPause = new SubMenu(this);
        subMenuPause.getChildren().addAll(btnResume, btnSettings, btnReturn);
    }

    /**
     * <p>Creates layout for settings in the pause menu.</p>
     * <p>settings have  {@link MenuSlider} for control of volume.</p>
     */
    private void createSubMenuSettings() {
        MenuSlider musicVolume = new MenuSlider("MUSIC", (int) (audioManager.getMusicVolume() * 100));
        MenuSlider sfxVolume = new MenuSlider("EFFECTS", (int) (audioManager.getEffectsVolume() * 100));
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

        FadeTransition ft = new FadeTransition(Duration.millis(300), dim);
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
        FadeTransition ft = new FadeTransition(Duration.millis(300), dim);
        ft.setFromValue(0.7);
        ft.setToValue(0);

        ft.play();
        subMenuSettings.hide();
        subMenuPause.hide();

        ft.setOnFinished(e -> gamePane.getChildren().remove(this));
    }
}

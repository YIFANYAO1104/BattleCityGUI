package com.bham.bc.view.menu;

import com.bham.bc.view.model.MenuButton;
import javafx.scene.layout.AnchorPane;

/**
 * <h1>Pause Menu</h1>
 *
 * <p>Represents an in-game menu that is observed whenever a game session asks to pause the game.
 * The menu allows to resume, change the sound settings, and return to the main menu.</p>
 *
 * <b>Note:</b> neither state, nor the score of the game is saved when returning to the main menu.
 */
public class PauseMenu {

    private MenuButton btnResume;       // resumes the current game
    private MenuButton btnSettings;     // opens configuration menu
    private MenuButton btnEndGame;      // returns to the main menu

    private AnchorPane parent;          // Parent node to attach the pause menu

    public PauseMenu() {}
}

package com.bham.bc.view.menu;

import com.bham.bc.view.model.MenuButton;
import javafx.scene.layout.AnchorPane;

/**
 * <h1>End Menu</h1>
 *
 * <p>Represents the end screen for a finished game session. End menu is observed every time
 * a player finishes a game. It asks the player to enter their name and saves their score of
 * a particular game mode</p>
 *
 * <b>Note:</b> the Menu asks for a name and allows to submit the score <i>only</i> if it makes
 * to the leaderboard of up to 10 highest all-time scores
 */
public class EndMenu extends AnchorPane {

    private MenuButton btnSubmit;

    public EndMenu() {}

}

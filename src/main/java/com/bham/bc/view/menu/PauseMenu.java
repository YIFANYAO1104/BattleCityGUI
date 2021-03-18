package com.bham.bc.view.menu;

import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.SubMenu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

    private AnchorPane parent;          // Parent node to attach the pause menu

    private SubMenu subMenuPause;

    public PauseMenu(MenuSession menuSession) {

        this.menuSession = menuSession;
        setWidth(500);
        setHeight(300);
        this.setLayoutX(250);
        this.setLayoutY(300);

        createSubMenuPause();
        initBgDim();
        subMenuPause.show();

    }

    private void createSubMenuPause() {
        btnResume=new MenuButton("Resume");
        btnSettings=new MenuButton("Settings");
        btnEndGame=new MenuButton("Quit");

        btnSettings.setOnMouseClicked(e->{});
        btnEndGame.setOnMouseClicked(e->{});
        btnResume.setOnMouseClicked(e->{});
        
        subMenuPause=new SubMenu(this);
        subMenuPause.getChildren().addAll(btnResume,btnSettings,btnEndGame);
        subMenuPause.setOpacity(0.8);

    }
    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        Rectangle bg = new Rectangle(getWidth(), getHeight());
        bg.setFill(Color.BLACK);
        bg.setOpacity(0.5);
        getChildren().add(bg);
    }


}

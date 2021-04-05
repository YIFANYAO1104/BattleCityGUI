package com.bham.bc.view.menu;

import com.bham.bc.utils.Constants;
import com.bham.bc.view.CustomStage;
import com.bham.bc.view.GameSession;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuSlider;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
        MenuSlider musicVolume = new MenuSlider("MUSIC VOLUME", 100);
        MenuSlider sfxVolume = new MenuSlider("EFFECTS VOLUME", 100);
        MenuButton btnBack = new MenuButton("BACK");

        musicVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));
        sfxVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setEffectVolume(newVal.doubleValue()/100));
        btnBack.setOnMouseClicked(e -> { subMenuOptions.hide();subMenuPause.show(); });


        createSkinChoose();
        Label skin=new Label("Skin Choose: ");
        skin.setStyle(" -fx-font-size: 18px;\n" +
                "-fx-text-fill: white;\n"+
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;\n" +
                "\n" +
                "    -fx-stroke: gold;");
        HBox hBox=new HBox(skin,changeSkin);


        subMenuOptions = new SubMenu(this);
        subMenuOptions.getChildren().addAll(musicVolume, sfxVolume, hBox,btnBack);
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
                "Classic Black","Classic Grey","Classic Blue","Classic Orange","Classic Gold"
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

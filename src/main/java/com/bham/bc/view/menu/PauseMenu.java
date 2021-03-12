package com.bham.bc.view.menu;

import com.bham.bc.components.mode.MODE;
import com.bham.bc.view.GameSession;
import com.bham.bc.view.menuButtons.ButtonSubscene;
import com.bham.bc.view.menuButtons.MenuButtons;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.TankPicker;
import com.bham.bc.view.model.TankType;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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


    public static MenuButtons gameModesButton;
    public static MenuButtons chooseButton;
    public static List<TankPicker> TanksList;
    public static TankType choosenTank;
    public static MenuButtons helperButton;
    public static Text text;
    public static MenuButtons modesPlayButton;
    public static ButtonSubscene chooseSubScene;
    public static ButtonSubscene modeSelcetionSubscene;
    public static MenuButtons optionsButton;
    public static MenuButtons scoresButton;
    public static Text text2;

    public PauseMenu() {}

    public static void createGameModesButton(){

        gameModesButton=new MenuButtons();

        Button survival;
        Button challenge;
        Stage gameStage;
        final GameSession[] gameViewManager = new GameSession[1];
        modeSelcetionSubscene=gameModesButton.initialization(CustomMenuSubscene.root);
        CustomMenuSubscene.showSubScene(modeSelcetionSubscene);
        survival=new Button("Survival Mode");
        survival.setId("buttons");
        survival.setLayoutX(95);
        survival.setLayoutY(120);
        survival.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                gameViewManager[0] =new GameSession(MODE.SURVIVAL);

                gameViewManager[0].createNewGame(CustomMenuSubscene.stage);
                GameSession.isshown=false;




            }
        });
        challenge=new Button("Challenge Mode");
        challenge.setId("buttons");
        challenge.setLayoutX(87);
        challenge.setLayoutY(200);
        challenge.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                gameViewManager[0] =new GameSession(MODE.CHALLENGE);
                gameViewManager[0].createNewGame(CustomMenuSubscene.stage);
                GameSession.isshown=false;





            }
        });


        modeSelcetionSubscene.getPane().getChildren().add(survival);
        modeSelcetionSubscene.getPane().getChildren().add(challenge);


    }
    private static HBox createTanksToChoose() {
        HBox box = new HBox();

        box.setSpacing(60);
        TanksList = new ArrayList<>();
        for (TankType Tank : TankType.values()) {
            TankPicker TankToPick = new TankPicker(Tank);
            TanksList.add(TankToPick);
            box.getChildren().add(TankToPick);
            TankToPick.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    for (TankPicker Tank : TanksList) {
                        Tank.setIsCircleChoosen(false);
                    }
                    TankToPick.setIsCircleChoosen(true);
                    MenuButtons.clickeOnVoice();
                    choosenTank = TankToPick.getTank();

                }
            });
        }

        box.setLayoutX(340 - (118*2));
        box.setLayoutY(150);
        return box;

    }

    public static void createChooseButton(){
        chooseButton=new MenuButtons();
        chooseSubScene=chooseButton.initialization(CustomMenuSubscene.root);
        MenuButtons.showSubScene(chooseSubScene);
        chooseSubScene.getPane().getChildren().add(createTanksToChoose());

    }

    public static void createHelperButton(){
        helperButton=new MenuButtons();
        ButtonSubscene helpSubscene=helperButton.initialization(CustomMenuSubscene.root);
        MenuButtons.showSubScene(helpSubscene);
        text=new Text();
        text.setId("texts");
        Glow glow1=new Glow();
        text.setEffect(glow1);
        text.setText("Rule:");
        text.setLayoutX(75);
        text.setLayoutY(120);

        helpSubscene.getPane().getChildren().add(text);
    }

    public static void createModePlayButton(){
        modesPlayButton=new MenuButtons();
        Button playSolo;
        Button playCoop;
        ButtonSubscene modesPlaySubscene=modesPlayButton.initialization(CustomMenuSubscene.root);
        MenuButtons.showSubScene(modesPlaySubscene);
        playSolo=new Button("Play Solo Mode");
        playSolo.setId("buttons");
        playSolo.setLayoutX(90);
        playSolo.setLayoutX(90);
        playSolo.setLayoutY(130);
        playCoop=new Button("Play Coop Mode");
        playCoop.setId("buttons");
        playCoop.setLayoutX(85);
        playCoop.setLayoutY(220);

        playCoop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        playSolo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        modesPlaySubscene.getPane().getChildren().addAll(playSolo,playCoop);

    }

    public static void createOptionsButton(){
        optionsButton=new MenuButtons();
        ButtonSubscene optionsSubscene=optionsButton.initialization(CustomMenuSubscene.root);
        Button continueG;
        Button returnBack;
        Button resume;
        Button renderGraph;
        CustomMenuSubscene.showSubScene(optionsSubscene);
        continueG=new Button("Continue");
        continueG.setId("buttons");
        continueG.setLayoutX(110);
        continueG.setLayoutY(270);
        resume=new Button("Resume");
        resume.setId("buttons");
        resume.setLayoutX(115);
        resume.setLayoutY(157);

        returnBack=new Button(" Return ");
        returnBack.setId("buttons");
        returnBack.setLayoutX(115);
        returnBack.setLayoutY(215);

        renderGraph=new Button(" RenderGraph");
        renderGraph.setId("buttons");
        renderGraph.setLayoutX(93);
        renderGraph.setLayoutY(100);

        returnBack.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            }
        });

        continueG.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        resume.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        renderGraph.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    MenuButtons.clickVoice();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        optionsSubscene.getPane().getChildren().addAll(continueG,resume,returnBack,renderGraph);

    }

    public static void createScoresButton(){
        scoresButton=new MenuButtons();
        ButtonSubscene scoresSubscene=scoresButton.initialization(CustomMenuSubscene.root);
        CustomMenuSubscene.showSubScene(scoresSubscene);
        text2=new Text();
        text2.setId("texts");
        text2.setText("Scores:");
        Glow glow1=new Glow();
        text2.setEffect(glow1);
        glow1.setLevel(0.7);
        text2.setLayoutX(75);
        text2.setLayoutY(120);

        scoresSubscene.getPane().getChildren().add(text);
    }


}

package com.bham.bc.view.menu;

import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuScene;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * <h1>Main Menu</h1>
 *
 * <p>Represents the primary menu of the game. All the buttons, sub-scenes and their functionalities
 * are defined here. The class is created only once by {@link MenuSession} and
 * is observed whenever a game session is not active.</p>
 */
public class MainMenu extends AnchorPane {
    
    private MenuSession menuSession;
    
    private MenuButton btnSolo;
    private MenuButton btnCoop;
    private MenuButton btnScores;
    private MenuButton btnSettings;
    private MenuButton btnQuit;

    private MenuScene sceneMain;
    private MenuScene sceneMode;
    private MenuScene sceneScores;
    private MenuScene sceneSettings;

    public MainMenu(double width, double height) {
        setWidth(width);
        setHeight(height);

        initBgDim();
        initTitle();

        createSceneMain();
        createSceneMode();
        createSceneScores();
        createSceneSettings();

        getChildren().addAll(sceneMain);
        sceneMain.show();
    }

    private void initBgDim() {
        Rectangle bg = new Rectangle(getWidth(), getHeight());
        bg.setFill(Color.GRAY);
        bg.setOpacity(0.2);

        getChildren().add(bg);
    }

    private void initTitle() {
        Title title = new Title("T A N K 1 G A M E");
        title.setTranslateY(100);
        title.setTranslateX(getWidth()/2 - title.getWidth()/2);

        getChildren().add(title);
    }

    private void createSceneMain() {
        btnSolo = new MenuButton("SOLO");
        btnCoop = new MenuButton("CO-OP");
        btnScores = new MenuButton("HIGH-SCORES");
        btnSettings = new MenuButton("SETTINGS");
        btnQuit = new MenuButton("QUIT");

        btnSolo.setOnMouseClicked(e -> { sceneMain.hide(); sceneMode.show(); });
        btnCoop.setOnMouseClicked(e -> { sceneMain.hide(); sceneMode.show(); });
        btnScores.setOnMouseClicked(e -> { sceneMain.hide(); sceneScores.show(); });
        btnSettings.setOnMouseClicked(e -> { sceneMain.hide(); sceneSettings.show(); });
        btnQuit.setOnMouseClicked(e -> System.exit(0));

        sceneMain = new MenuScene(this);
        sceneMain.setWidth1(btnSolo.getWidth());
        sceneMain.getChildren().addAll(btnSolo, btnCoop, btnScores, btnSettings, btnQuit);
        System.out.println();
    }

    private void createSceneMode() {
        sceneMode = new MenuScene(this);
    }
    private void createSceneScores() {
        sceneScores = new MenuScene(this);
    }
    private void createSceneSettings() {
        MenuButton btnBack = new MenuButton("BACK");
        MenuButton btnSound = new MenuButton("SOUND");
        MenuButton btnVideo = new MenuButton("VIDEO");

        btnBack.setOnMouseClicked(e -> { sceneSettings.hide(); sceneMain.show(); });

        sceneSettings = new MenuScene(this);
        sceneSettings.setTranslateX(400);
        sceneSettings.getChildren().addAll(btnBack, btnSound, btnVideo);
    }


    /**
     *  <h1>Title of the game</h1>
     *
     *  <p>This is a unique component provided by a parent {@link com.bham.bc.view.menu.MainMenu}.
     *  This is because the Title should always be part of the Main Menu node.
     */
    private static class Title extends StackPane {

        private static final double WIDTH = 475;
        private static final double HEIGHT = 60;

        /**
         * Constructs a title used in the Main Menu layout
         * @param name the title of the game
         * TODO: look for fancier/more game-like fonts
         */
        public Title(String name) {
            Rectangle bg = new Rectangle(WIDTH, HEIGHT);
            setWidth(WIDTH);
            setHeight(HEIGHT);
            bg.setStroke(Color.WHITE);
            bg.setStrokeWidth(3);
            bg.setFill(null);


            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.TOP_CENTER);
            getChildren().addAll(bg,text);
        }
    }
}

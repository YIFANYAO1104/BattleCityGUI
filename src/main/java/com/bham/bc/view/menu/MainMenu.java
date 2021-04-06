package com.bham.bc.view.menu;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * <h1>Main Menu</h1>
 *
 * <p>Represents the primary menu of the game. All the buttons, sub-menus and their functionalities
 * are defined here. The class is created only once by {@link MenuSession} and is observed whenever
 * a game session is not active.</p>
 */
public class MainMenu extends AnchorPane {

    private SubMenu subMenuMain;
    private SubMenu subMenuMode;
    private SubMenu subMenuScores;
    private SubMenu subMenuSettings;

    private final NewGameEvent NEW_GAME_EVENT;
    /**
     * Constructs an AnchorPane layout as the Main Menu
     */
    public MainMenu() {
        NEW_GAME_EVENT = new NewGameEvent(NewGameEvent.START_GAME);
        setMinWidth(MenuSession.WIDTH);
        setMinHeight(MenuSession.HEIGHT);

        initBgDim();

        createSubMenuMain();
        createSubMenuMode();
        createSubMenuScores();
        createSubMenuSettings();

        subMenuMain.show();
    }

    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        Rectangle dim = new Rectangle(getWidth(), getHeight());
        dim.setFill(Color.NAVY);
        dim.setOpacity(0.3);
        getChildren().add(dim);
    }

    /**
     * Creates the primary sub-menu for the main menu. This defines the behavior of all the
     * necessary buttons to control the GUI actions and create corresponding sub-menus.
     */
    private void createSubMenuMain() {
        MenuButton btnSolo = new MenuButton("SOLO");
        MenuButton btnCoop = new MenuButton("CO-OP");
        MenuButton btnScores = new MenuButton("HIGH-SCORES");
        MenuButton btnSettings = new MenuButton("SETTINGS");
        MenuButton btnQuit = new MenuButton("QUIT");

        btnSolo.setOnMouseClicked(e -> { NEW_GAME_EVENT.setNumPlayers(1); subMenuMain.hide(); subMenuMode.show(); });
        btnCoop.setOnMouseClicked(e -> { NEW_GAME_EVENT.setNumPlayers(2); subMenuMain.hide(); subMenuMode.show(); });
        btnScores.setOnMouseClicked(e -> { subMenuMain.hide(); subMenuScores.show(); });
        btnSettings.setOnMouseClicked(e -> { subMenuMain.hide(); subMenuSettings.show(); });
        btnQuit.setOnMouseClicked(e -> System.exit(0));

        subMenuMain = new SubMenu(this);
        subMenuMain.getChildren().addAll(btnSolo, btnCoop, btnScores, btnSettings, btnQuit);
    }

    /**
     * Creates the sub-menu for mode selection. This menu is observed whenever "SOLO" or
     * "CO-OP" is clicked and asks {@link com.bham.bc.view.MenuSession} to initiate a
     * single {@link com.bham.bc.view.GameSession} based on the selected parameters
     */
    private void createSubMenuMode() {
        MenuButton btnSurvival = new MenuButton("SURVIVAL");
        MenuButton btnChallenge = new MenuButton("CHALLENGE");
        MenuButton btnBack = new MenuButton("BACK");

        btnSurvival.setOnMouseClicked(e -> { NEW_GAME_EVENT.setMode(MODE.SURVIVAL); NEW_GAME_EVENT.setMapType(MapType.Map1); btnSurvival.fireEvent(NEW_GAME_EVENT); });
        btnChallenge.setOnMouseClicked(e -> { NEW_GAME_EVENT.setMode(MODE.CHALLENGE); NEW_GAME_EVENT.setMapType(MapType.EmptyMap); btnChallenge.fireEvent(NEW_GAME_EVENT);});
        btnBack.setOnMouseClicked(e -> { subMenuMode.hide(); subMenuMain.show(); });

        subMenuMode = new SubMenu(this);
        subMenuMode.getChildren().addAll(btnSurvival, btnChallenge, btnBack);
    }

    /**
     * Creates a sub-menu to view high-scores of both modes. This menu is observed whenever
     * "HIGH-SCORES" is clicked and shows top 10 scores.
     */
    private void createSubMenuScores() {
        subMenuScores = new SubMenu(this);

        // Increase leaderboard size
        subMenuScores.setMinWidth(680);
        subMenuScores.setMinHeight(500);
        subMenuScores.alignCenter();

        // We want a different style from a regular sub-menu
        subMenuScores.getStyleClass().clear();
        subMenuScores.setId("sub-menu-scores");

        // Set up leaderboard label
        Label leaderboardLabel = new Label("SURVIVAL");
        leaderboardLabel.getStyleClass().add("leaderboard-label");

        // Create columns for leaderboard table
        TableColumn<RecordsHandler.Records, String> rank, name, score, date;
        rank = new TableColumn<>("Rank");
        name = new TableColumn<>("Name");
        score = new TableColumn<>("Score");
        date = new TableColumn<>("Date");

        rank.setMinWidth(160);
        name.setMinWidth(160);
        score.setMinWidth(160);
        date.setMinWidth(160);

        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        score.setCellValueFactory(new PropertyValueFactory<>("score"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Get the saved data from record handler
        RecordsHandler recordsHandler = new RecordsHandler();
        ObservableList<RecordsHandler.Records> survivalData= recordsHandler.createSampleRecords();

        TableView<RecordsHandler.Records> tableView = new TableView<>();
        tableView.setMaxSize(subMenuScores.getMinWidth(), subMenuScores.getMinHeight());
        tableView.getColumns().addAll(rank, name, score, date);
        tableView.setItems(survivalData);
        tableView.setId("scores-table");

        subMenuScores.setOnMouseClicked(e -> { subMenuScores.hide(); subMenuMain.show(); });
        tableView.setOnMouseClicked(e -> { subMenuScores.hide(); subMenuMain.show(); });

        subMenuScores.getChildren().addAll(leaderboardLabel, tableView);
    }

    /**
     * Creates a sub-menu for settings. This menu is observed whenever "SETTINGS" is clicked
     * and allows the user to configure UI parameters, such as SFX or MUSIC volume
     */
    private void createSubMenuSettings() {
        MenuSlider musicVolume = new MenuSlider("MUSIC", 100);
        MenuSlider sfxVolume = new MenuSlider("EFFECTS", 100);

        MenuButton btnBack = new MenuButton("BACK");

        musicVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));
        sfxVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setEffectVolume(newVal.doubleValue()/100));
        btnBack.setOnMouseClicked(e -> { subMenuSettings.hide(); subMenuMain.show(); });

        subMenuSettings = new SubMenu(this);
        subMenuSettings.getChildren().addAll(musicVolume, sfxVolume, btnBack);
    }
}

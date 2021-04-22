package com.bham.bc.view.menu;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

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
    private SubMenu subMenuScores;
    private SubMenu subMenuSettings;

    private final GameFlowEvent NEW_GAME_EVENT;

    /**
     * Constructs an {@link AnchorPane} layout as the Main Menu
     */
    public MainMenu() {
        NEW_GAME_EVENT = new GameFlowEvent(GameFlowEvent.START_GAME);
        setMinSize(MenuSession.WIDTH, MenuSession.HEIGHT);

        initBgDim();
        createSubMenuMain();
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
        dim.setOpacity(0.4);
        getChildren().add(dim);
    }

    /**
     * Creates the primary sub-menu for the main menu. This defines the behavior of all the
     * necessary buttons to control the GUI actions and create corresponding sub-menus.
     */
    private void createSubMenuMain() {
        MenuButton btnStart = new MenuButton("START GAME");
        MenuButton btnScores = new MenuButton("HIGH-SCORES");
        MenuButton btnSettings = new MenuButton("SETTINGS");
        MenuButton btnQuit = new MenuButton("QUIT");


        btnStart.setOnMouseClicked(e -> { NEW_GAME_EVENT.setMapType(MapType.Map1); btnStart.fireEvent(NEW_GAME_EVENT); });
        btnScores.setOnMouseClicked(e -> { subMenuMain.hide(); subMenuScores.show(); });
        btnSettings.setOnMouseClicked(e -> { subMenuMain.hide(); subMenuSettings.show(); });
        btnQuit.setOnMouseClicked(e -> System.exit(0));

        subMenuMain = new SubMenu(this);
        subMenuMain.getChildren().addAll(btnStart, btnScores, btnSettings, btnQuit);
    }


    /**
     * Creates a sub-menu to view high-scores of both modes. This menu is observed whenever
     * "HIGH-SCORES" is clicked and shows top 10 scores.
     */
    private void createSubMenuScores() {
        subMenuScores = new SubMenu(this);

        // Increase leaderboard size and set up a different style from a regular sub-menu
        subMenuScores.setMinSize(680, 500);
        subMenuScores.alignCenter();
        subMenuScores.getStyleClass().clear();
        subMenuScores.setId("sub-menu-scores");

        // Set up leaderboard label
        Label leaderboardLabel = new Label("Scores");
        leaderboardLabel.getStyleClass().add("leaderboard-label");

        // Create the leaderboard table
        TableView<RecordsHandler.Records> tableView = new TableView<>();
        tableView.setId("scores-table");

        // Add 5 columns to the table
        Arrays.stream(new String[]{"Rank", "Name", "Score", "Date"}).forEach(columnName -> {
            TableColumn<RecordsHandler.Records, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<>(columnName.toLowerCase()));
            tableView.getColumns().add(column);
        });

        // Initialize the current records and listen for new ones
        tableView.setItems(RecordsHandler.initTable());
        addEventFilter(GameFlowEvent.LEAVE_GAME, e -> {
            if(e.getScore() >= 0) {
                RecordsHandler recordsHandler = new RecordsHandler();
                recordsHandler.createRecord(new RecordsHandler.Records(e.getName(), e.getScore()));
                tableView.setItems(recordsHandler.sortAndGetData());
            }
        });

        // Event handler to go back to the main menu
        subMenuScores.setOnMouseClicked(e -> { subMenuScores.hide(); subMenuMain.show(); });
        tableView.setOnMouseClicked(e -> { subMenuScores.hide(); subMenuMain.show(); });

        // Add the table to the scores sub-menu
        subMenuScores.getChildren().addAll(leaderboardLabel, tableView);
    }

    /**
     * Creates a sub-menu for settings. This menu is observed whenever "SETTINGS" is clicked
     * and allows the user to configure UI parameters, such as SFX or MUSIC volume
     */
    private void createSubMenuSettings() {
        MenuSlider musicVolume = new MenuSlider("MUSIC", (int) (audioManager.getMusicVolume() * 100));
        MenuSlider sfxVolume = new MenuSlider("EFFECTS", (int) (audioManager.getEffectsVolume() * 100));
        MenuButton btnBack = new MenuButton("BACK");

        musicVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));
        sfxVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setEffectsVolume(newVal.doubleValue()/100));
        btnBack.setOnMouseClicked(e -> { subMenuSettings.hide(); subMenuMain.show(); });

        subMenuSettings = new SubMenu(this);
        subMenuSettings.getChildren().addAll(musicVolume, sfxVolume, btnBack);
    }
}

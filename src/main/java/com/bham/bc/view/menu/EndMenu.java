package com.bham.bc.view.menu;

import com.bham.bc.view.GameSession;
import com.bham.bc.view.tools.GameFlowEvent;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.tools.RecordsHandler;
import com.bham.bc.view.model.SubMenu;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONObject;

/**
 * <h1>End Menu</h1>
 *
 * <p>Represents the end screen for a finished game session. End menu is observed every time
 * a player finishes a game. It asks the player to enter their name and saves their score to
 * a JSON file to be loaded to the leaderboard.</p>
 *
 * <b>Note:</b> the Menu asks for a name and allows to submit the score <i>only</i> if it makes
 * to the leaderboard of up to 10 highest all-time scores
 *
 */
public class EndMenu extends AnchorPane {
    /** Event to be fired if "Submit" is clicked */
    private final GameFlowEvent LEAVE_GAME_EVENT;

    /** {@link SubMenu} containing elements to represent the main layout of the end menu */
    private SubMenu subMenuEnd;

    /** Background dim to make game layout stand out less and end menu layout more */
    private Rectangle dim;

    /**
     * Constructs an {@link AnchorPane} layout as the End Menu and initialize End Menu
     */
    public EndMenu() {
        LEAVE_GAME_EVENT = new GameFlowEvent(GameFlowEvent.LEAVE_GAME);
        setMinSize(GameSession.WIDTH, GameSession.HEIGHT);
        initBgDim();
    }

    /**
     * Adds background dim to the menu.
     */
    private void initBgDim() {
        dim = new Rectangle(GameSession.WIDTH, GameSession.HEIGHT);
        dim.setFill(Color.BLACK);
        dim.setOpacity(0.5);
        getChildren().add(dim);
    }

    /**
     * <p>Layout to be shown if the score makes it to the top 10 of all time scores.</p>
     * <p>use custom menu button ({@link MenuButton}) as submit button.</p>
     * @param score score to be shown on the layout and saved to the JSON file
     */
    private void showWhenHigh(double score) {
        // Main label to show on the end menu
        Label endMenuLabel = new Label("Congratulations!\nYou made it to the top!");
        endMenuLabel.getStyleClass().add("leaderboard-label");

        // Main label to show the score
        Label scoreLabel = new Label("Your score: " + score);
        scoreLabel.getStyleClass().add("slider-label");

        // Label to ask user's name
        Label nameLabel = new Label("Enter your name:");
        nameLabel.getStyleClass().add("slider-label");

        // Input field to get user's name
        TextField nameInput = new TextField();
        nameInput.setMaxWidth(GameSession.WIDTH/3.0);
        nameInput.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().length() <= 10 ? change : null));

        // Label and input field for user's name
        VBox labelAndInput = new VBox();
        labelAndInput.setSpacing(15);
        labelAndInput.setAlignment(Pos.CENTER);
        labelAndInput.getChildren().addAll(nameLabel, nameInput);

        // Submit button to push the record to the leaderboard
        MenuButton btnSubmit = new MenuButton("SUBMIT");
        btnSubmit.setOnMouseClicked(e -> {
            LEAVE_GAME_EVENT.setScore(score);
            if(!nameInput.getText().equals("")){

            LEAVE_GAME_EVENT.setName(nameInput.getText());
            btnSubmit.fireEvent(LEAVE_GAME_EVENT);
            }
        });

        // Add all elements to the layout (with a bigger than default spacing)
        subMenuEnd = new SubMenu(this);
        subMenuEnd.setSpacing(25);
        subMenuEnd.getChildren().addAll(endMenuLabel, scoreLabel, labelAndInput, btnSubmit);
    }

    /**
     * <p>Layout to be shown if the score is too low to make it to the leaderboard.</p>
     * <p>use custom menu button ({@link MenuButton}) as return button.</p>
     * @param score score to be shown on the layout
     */
    private void showWhenLow(double score) {
        // Main label to show on the end menu
        Label endMenuLabel = new Label("Your score didn't make it\nto the leaderboard this time...");
        endMenuLabel.getStyleClass().add("leaderboard-label");

        // Main label to show the score
        Label scoreLabel = new Label("Your score: " + score);
        scoreLabel.getStyleClass().add("slider-label");

        // Button to get back
        MenuButton btnReturn = new MenuButton("RETURN TO MENU");
        btnReturn.setOnMouseClicked(e -> btnReturn.fireEvent(LEAVE_GAME_EVENT));

        // Add all elements to the layout (with a bigger than default spacing)
        subMenuEnd = new SubMenu(this);
        subMenuEnd.setSpacing(50);
        subMenuEnd.getChildren().addAll(endMenuLabel, scoreLabel, btnReturn);
    }

    /**
     * <p>Shows end menu with fade in transition</p>
     * <p>if the number of records in record array ({@link RecordsHandler#jsonArrayToFile}) is less than 10
     * or if the score the user gets is larger than the last score on the leaderboard,
     * the score and name are shown on the leaderboard by function {@link #showWhenHigh(double)}.</p>
     * <p>end menu is shown by animation of {@link FadeTransition}.</p>
     * @param gamePane game pane menu will be attached to
     * @param score the score user gets
     */
    public void show(AnchorPane gamePane, double score) {
        gamePane.getChildren().add(this);

        if (RecordsHandler.jsonArrayToFile.length() < 10 || Double.parseDouble(((JSONObject) RecordsHandler.jsonArrayToFile.get(RecordsHandler.jsonArrayToFile.length()-1)).getString("score")) <= score) {
            showWhenHigh(score);
        } else {
            showWhenLow(score);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(300), dim);
        ft.setFromValue(0);
        ft.setToValue(0.7);
        ft.play();

        subMenuEnd.show();
    }
}

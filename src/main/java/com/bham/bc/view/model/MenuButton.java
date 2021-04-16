package com.bham.bc.view.model;

import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.audio.SoundEffect.SELECT;

/**
 * <h1>Menu Button</h1>
 *
 * <p>Custom menu button which is represented through <i>StackPane</i> node provided by JavaFX. This button contains
 * background, side bar and text. It has custom fill and scale animation upon hovering it.</p>
 */
public class MenuButton extends StackPane {

    public static final double WIDTH = 300;
    public static final double HEIGHT = 50;

    private final String FG_1 = "#135ADD";  // -fx-primary-color (foreground primary)
    private final String FG_2 = "#649ACC";  // -fx-secondary-color (foreground secondary)
    private final String BG_1 = "#080A1E";  // -fx-bg-color (background primary)
    private final String BG_2 = "#00030A";  // -fx-darken-color (background secondary)
    private final String HIGH = "#B0CAFF";  // -fx-lighten-color (highlight)

    /**
     * Constructs a custom animated menu button
     * @param name text the button should contain
     */
    public MenuButton(String name) {
        // Configure the whole button
        setMaxWidth(WIDTH);
        setMaxHeight(HEIGHT);
        setEffect(new Glow(1));

        // Color gradient when the button is not hovered
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(BG_2, .9)),
                new Stop(1, Color.web(BG_1, .4))
        );

        // Initialize button, its background and border colors
        Rectangle bg = new Rectangle(WIDTH, HEIGHT, gradient);
        bg.setStroke(Color.web(BG_2));

        // Set up side bar
        Rectangle sideBar = new Rectangle(9, HEIGHT);
        sideBar.widthProperty().bind(Bindings.when(hoverProperty()).then(12).otherwise(9));
        sideBar.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.web(FG_2)).otherwise(Color.web(FG_1)));
        sideBar.strokeProperty().bind(Bindings.when(hoverProperty()).then(Color.web(FG_2)).otherwise(Color.web(FG_1)));

        // Set up button text
        Text text = new Text(name);
        Font font = Font.loadFont("file:src/main/resources/Ubuntu-Medium.ttf", 26);
        text.setFont(font);
        text.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.web(FG_2)).otherwise(Color.web(FG_1)));

        // Container for the side bar and text
        HBox barAndText = new HBox(15, sideBar, text);
        barAndText.setAlignment(Pos.CENTER_LEFT);

        // Color animation when the button is hovered
        FillTransition ft = new FillTransition(Duration.seconds(0.6), bg, Color.web(BG_1, 0.6), Color.web(FG_1, 0.4));
        ft.setAutoReverse(true);
        ft.setCycleCount(Transition.INDEFINITE);

        // Scale animation when the button is hovered
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), this);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), this);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        // Hover animation
        setOnMouseEntered((e) -> {
            ft.playFromStart();
            bg.setStrokeWidth(0);
            sideBar.setStrokeWidth(0);
            scaleUp.play();
        });
        setOnMouseExited(e -> {
            ft.stop();
            bg.setFill(gradient);
            bg.setStrokeWidth(1);
            sideBar.setStrokeWidth(1);
            scaleDown.play();
        });

        // Click animations
        setOnMousePressed(e -> { ft.stop(); bg.setFill(Color.web(HIGH, .3)); audioManager.playEffect(SELECT); });
        setOnMouseReleased(e -> bg.setFill(gradient));

        // Add parts that make up the button
        getChildren().addAll(bg, barAndText);
    }
}

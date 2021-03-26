package com.bham.bc.view.model;

import javafx.animation.FillTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
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

import static com.bham.bc.audio.SFX.SELECT;

/**
 *The Class MenuButton imports StackPane which contains a single property named alignment.
 * This property represents the alignment of the nodes within the stack pane.
 * This class represents the the single menu buttons.
 * TODO: check if the cass could be extended with JavaFX Button
 */
public class MenuButton extends StackPane {

    public static final double WIDTH = 250;
    public static final double HEIGHT = 30;




    /**
     * @param name The name of each button.
     */
    public MenuButton(String name) {
        LinearGradient gradient = new LinearGradient(
                0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.1, Color.web("black", 0.75)),
                new Stop(1.0, Color.web("black", 0.15))
        );
        Rectangle bg0 = new Rectangle(WIDTH, HEIGHT, gradient);
        Rectangle bg1 = new Rectangle(WIDTH, HEIGHT, Color.web("black", 0.2));

        FillTransition ft = new FillTransition(Duration.seconds(0.6),
                bg1, Color.web("black", 0.2), Color.web("white", 0.3));

        ft.setAutoReverse(true);
        ft.setCycleCount(Transition.INDEFINITE);

        hoverProperty().addListener((o, oldValue, isHovering) -> {
            if (isHovering) {
                ft.playFromStart();
            } else {
                ft.stop();
                bg1.setFill(Color.web("black", 0.2));
            }
        });

        Rectangle line = new Rectangle(5, HEIGHT);
        line.widthProperty().bind(
                Bindings.when(hoverProperty())
                        .then(8).otherwise(5)
        );
        line.fillProperty().bind(
                Bindings.when(hoverProperty())
                        .then(Color.YELLOW).otherwise(Color.GRAY)
        );

        Text text = new Text(name);
        text.setFont(Font.font(21.0));
        text.fillProperty().bind(
                Bindings.when(hoverProperty())
                        .then(Color.WHITE).otherwise(Color.GRAY)
        );


        setOnMousePressed(e -> {
            bg0.setFill(Color.LIGHTBLUE);
            SELECT.play();
        });

        setOnMouseReleased(e -> bg0.setFill(gradient));

        setAlignment(Pos.CENTER_LEFT);

        HBox box = new HBox(15, line, text);
        box.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(bg0, bg1, box);
    }
}

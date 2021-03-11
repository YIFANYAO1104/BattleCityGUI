package com.bham.bc.view.model;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *The Class MenuButton imports StackPane which contains a single property named alignment.
 * This property represents the alignment of the nodes within the stack pane.
 * This class represents the the single menu buttons.
 * TODO: check if the cass could be extended with JavaFX Button
 */
public class MenuButton extends StackPane {

    public static final double WIDTH = 250;
    public static final double HEIGHT = 30;
    private Text text;

    /**
     * @param name The name of each button.
     */
    public MenuButton(String name) {
        setWidth(WIDTH);
        setHeight(HEIGHT);

        text = new Text(name);
        text.setFont(text.getFont().font(20));
        text.setFill(Color.WHITE);

        Rectangle bg = new Rectangle(WIDTH, HEIGHT);
        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect(new GaussianBlur(3.5));

        setAlignment(Pos.CENTER_LEFT);
        setRotate(-0.5);
        getChildren().addAll(bg, text);

        setOnMouseEntered(event -> {
            bg.setFill(Color.WHITE);
            text.setFill(Color.BLACK);
            bg.setTranslateX(10);
            text.setTranslateX(10);
        });

        setOnMouseExited(event -> {
            bg.setFill(Color.BLACK);
            text.setFill(Color.WHITE);
            bg.setTranslateX(0);
            text.setTranslateX(0);
        });

        DropShadow drop = new DropShadow(50, Color.WHITE);
        drop.setInput(new Glow());

        setOnMousePressed(event -> setEffect(drop));
        setOnMouseReleased(event -> setEffect(null));
    }
}

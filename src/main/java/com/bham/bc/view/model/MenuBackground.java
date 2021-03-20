package com.bham.bc.view.model;

import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/**
 * <h1>Background for the Main Menu</h1>
 *
 * <p>By default non of the menus have backgrounds, they only have a darker dim over the scene.
 * This class, however, is used as a separate JavaFX node as a background for the Main Menu.</p>
 */
public class MenuBackground extends AnchorPane {

    private static final String BG_PATH = "file:src/main/resources/img/menu/menuBG.gif";

    /**
     * Constructs a Stack Pane node with an animated background and particles
     *
     * @param width  the size of the menu's width
     * @param height the size of the menu's height
     */
    public MenuBackground(double width, double height) {
        setWidth(width);
        setHeight(height);

        initBackgroundAnimation();
        initParticleAnimation();
    }

    /**
     * Creates a background image animation and adds it to the background layout
     * TODO: surround with try-catch
     */
    private void initBackgroundAnimation() {
        ImageView bgImage = new ImageView(new Image(BG_PATH));
        bgImage.setFitWidth(getWidth());
        bgImage.setFitHeight(getHeight());

        getChildren().add(bgImage);
    }

    /**
     * Creates particle animation and adds it to the background layout
     * <b>NOTE:</b> to be implemented
     */
    private void initParticleAnimation() {}

    /**
     * Represents a particle used for background animation.
     * A bunch of respawning particles will be used to create fancy visual effects.
     *
     * <b>NOTE:</b> to be implemented
     */
    private static class Particle extends Rectangle {

    }
}
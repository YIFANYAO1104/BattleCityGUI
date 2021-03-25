package com.bham.bc.view.model;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * <h1>Sub-menu</h1>
 *
 * <p>Represents a layout of a sub-menu</p>
 */
public class SubMenu extends VBox {
    private AnchorPane parent;

    /**
     * Constructs an AnchorPane as layout for the sub-menu
     * @param parent node to which the sub-menu will be attached (detached)
     */
    public SubMenu(AnchorPane parent) {
        super(15);
        this.parent = parent;
        setWidth(MenuButton.WIDTH);
        setTranslateX(parent.getWidth());
        setTranslateY(parent.getHeight()/3);
    }

    /**
     * Animates the appearance of the sub-menu
     */
    public void show() {
        if(!parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), this);
            tt.setToX(parent.getWidth() / 2 - getWidth() / 2);
            tt.play();
        }
    }

    /**
     * Animates the disappearance of the sub-menu
     */
    public void hide() {
        if(parent.getChildren().contains(this)) {
            TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), this);
            tt1.setToX(parent.getWidth());
            tt1.play();
            tt1.setOnFinished(e -> parent.getChildren().remove(this));
        }
    }
}

package com.bham.bc.view.model;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * <h1>Sub-menu</h1>
 *
 * <p>Represents a layout of a sub-menu</p>
 */
public class SubMenu extends VBox {

    // Note: must be lower than MenuSession and GameSession size
    public static final int WIDTH = 700;
    public static final int HEIGHT = 384;

    private final AnchorPane PARENT;

    /**
     * Constructs an AnchorPane as layout for the sub-menu
     * @param parent node to which the sub-menu will be attached (detached)
     */
    public SubMenu(AnchorPane parent) {
        super(15);
        PARENT = parent;
        setMinWidth(WIDTH);
        setMinHeight(HEIGHT);
        setAlignment(Pos.CENTER);
        alignCenter();
        getStyleClass().add("sub-menu");

        // Initialize as hidden
        setScaleX(.1);
        setScaleY(.1);
    }

    /**
     * Aligns itself at the center of the parent node
     */
    public void alignCenter() {
        setTranslateX(PARENT.getMinWidth()*.5 - getMinWidth()*.5);
        setTranslateY(PARENT.getMinHeight()*.5 - getMinHeight()*.5);
    }

    /**
     * Animates the appearance of the sub-menu
     */
    public void show() {
        if(!PARENT.getChildren().contains(this)) {
            PARENT.getChildren().add(this);

            // JavaFX doesn't support bezier curves with values out of range [0, 1]
            // so we must concatenate 2 scale animations to achieve the wanted effect
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(160), this);
            scaleUp.setInterpolator(Interpolator.SPLINE(.3,.73,.35,.99));
            scaleUp.setToX(1.15);
            scaleUp.setToY(1.15);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(90), this);
            scaleDown.setInterpolator(Interpolator.SPLINE(.76,.01,.96,.44));
            scaleDown.setToX(1);
            scaleDown.setToY(1);

            scaleUp.setOnFinished(e -> scaleDown.play());
            scaleUp.play();
        }
    }

    /**
     * Animates the disappearance of the sub-menu
     */
    public void hide() {
        if(PARENT.getChildren().contains(this)) {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.setToX(.1);
            st.setToY(.1);

            st.setOnFinished(e -> PARENT.getChildren().remove(this));
            st.play();
        }
    }
}

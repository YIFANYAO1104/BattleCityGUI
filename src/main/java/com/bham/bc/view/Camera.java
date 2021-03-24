package com.bham.bc.view;

import com.bham.bc.components.characters.Player;
import com.bham.bc.utils.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Translate;

public class Camera {
    private final Translate translate;

    private final DoubleProperty x;
    private final DoubleProperty y;
    private final GraphicsContext gc;

    /**
     * Constructs camera which tracks 1 character
     */
    public Camera(GraphicsContext gc) {
        this.gc = gc;
        translate = new Translate();
        x = new SimpleDoubleProperty();
        y = new SimpleDoubleProperty();

        x.bind(Bindings.subtract(GameSession.GAME_WIDTH/2.0, Player.TRACKABLE_X));
        y.bind(Bindings.subtract(GameSession.GAME_HEIGHT/2.0, Player.TRACKABLE_Y));
    }

    /**
     * updates camera position
     */
    public void update() {
        if(!isCloseToBorderX(0)) translate.setX(x.get());
        if(!isCloseToBorderY(0)) translate.setY(y.get());

        gc.setTransform(translate.getMxx(), translate.getMyx(), translate.getMxy(), translate.getMyy(), translate.getTx(), translate.getTy());
    }

    /**
     * checks if camera's view is close to the border horizontally
     * @param offset additional width constrain
     * @return true if camera's view is close and false otherwise
     */
    private boolean isCloseToBorderX(double offset) {
        return x.get() >= 0 - offset || x.get() <= Constants.WINDOW_WIDTH - Constants.MAP_WIDTH + offset;
    }

    /**
     * checks if camera's view is close to the border vertically
     * @param offset additional height constrain
     * @return true if camera's view is close and false otherwise
     */
    private boolean isCloseToBorderY(double offset) {
        return y.get() >= 0 - offset || y.get() <=  Constants.WINDOW_HEIGHT - Constants.MAP_HEIGHT + offset;
    }
}

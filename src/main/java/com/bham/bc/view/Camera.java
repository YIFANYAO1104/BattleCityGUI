package com.bham.bc.view;

import com.bham.bc.components.characters.Player;
import com.bham.bc.utils.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.PerspectiveCamera;

public class Camera extends PerspectiveCamera {
    //must be strictly less than 0. Or we'll se nothing.
    private static final double INITIAL_PERSPECTIVE = -500;    // Between -1 and -1000

    private DoubleProperty x;
    private DoubleProperty y;
    private DoubleProperty z;

    /**
     * Constructs camera which tracks 1 character
     */
    public Camera() {
        initCameraProperties();

        x.bind(Bindings.subtract(Player.TRACKABLE_X, Constants.WINDOW_WIDTH/2));
        y.bind(Bindings.subtract(Player.TRACKABLE_Y, Constants.WINDOW_HEIGHT/2));
    }

    /**
     * initializes trackable coordinate and perspective values
     */
    private void initCameraProperties() {
        x = new SimpleDoubleProperty();
        y = new SimpleDoubleProperty();
        z = new SimpleDoubleProperty(INITIAL_PERSPECTIVE);

        this.setTranslateZ(z.get());
        this.setNearClip(1);
        this.setFarClip(1000);
    }

    /**
     * updates camera position
     */
    public void update() {
        if(!isCloseToBorderX(0)) this.setTranslateX(x.get());
        if(!isCloseToBorderY(0)) this.setTranslateY(y.get());
    }

    /**
     * checks if camera's view is close to the border horizontally
     * @param offset additional width constrain
     * @return true if camera's view is close and false otherwise
     */
    private boolean isCloseToBorderX(double offset) {
        if(x.get() <= 0 + offset || x.get() >= Constants.MAP_WIDTH - Constants.WINDOW_WIDTH - offset) {
            return true;
        }
        return false;
    }

    /**
     * checks if camera's view is close to the border vertically
     * @param offset additional height constrain
     * @return true if camera's view is close and false otherwise
     */
    private boolean isCloseToBorderY(double offset) {
        if(y.get() <= 0 + offset || y.get() >= Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT - offset) {
            return true;
        }
        return false;
    }
}

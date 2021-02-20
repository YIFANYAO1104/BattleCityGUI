package com.bham.bc.view;

import com.bham.bc.components.characters.TrackableCharacter;
import com.bham.bc.utils.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.PerspectiveCamera;

public class Camera extends PerspectiveCamera {
    private DoubleProperty x;
    private DoubleProperty y;
    private DoubleProperty z;
    private DoubleProperty w;
    private DoubleProperty v;

    private boolean lastTrue = true;

    private BooleanProperty requiresZoom;
    private BooleanProperty isTooZoomed;

    /**
     * Constructs camera which tracks 1 character
     * @param c character to be followed
     */
    public Camera(TrackableCharacter c) {
        initCameraProperties();

        x.bind(Bindings.subtract(c.getTrackableCoordinateX(), Constants.WINDOW_WIDTH/2));
        y.bind(Bindings.subtract(c.getTrackableCoordinateY(), Constants.WINDOW_HEIGHT/2));
    }

    /**
     * Constructs camera which tracks 2 characters
     * @param c1 first character to be followed
     * @param c2 second character to be followed
     */
    public Camera(TrackableCharacter c1, TrackableCharacter c2) {
        initCameraProperties();

        x.bind(Bindings.subtract(Bindings.divide(Bindings.add(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX()), 2), Constants.WINDOW_WIDTH/2));
        y.bind(Bindings.subtract(Bindings.divide(Bindings.add(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY()), 2), Constants.WINDOW_HEIGHT/2));

        double zoomOutPerArea = -1;
        double maxZoomOutProp = -500;
        DoubleProperty mzp = new SimpleDoubleProperty(-500);


        w.bind(Bindings.when(Bindings.and( Bindings.not( isTooZoomed
        ),Bindings.or(
                                Bindings.greaterThan(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX()), Bindings.min(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX())), Constants.WINDOW_WIDTH*0.85),
                                Bindings.greaterThan(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY()), Bindings.min(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY())), Constants.WINDOW_HEIGHT*0.85)
                        ))).then(Bindings.multiply(Bindings.max(
                Bindings.subtract(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX()), Bindings.min(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX())), Constants.WINDOW_WIDTH*0.85),
                Bindings.subtract(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY()), Bindings.min(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY())), Constants.WINDOW_HEIGHT*0.85)
                ), -2)
        ).otherwise(0));

        z.bind(Bindings.when(

                Bindings.and( Bindings.not( isTooZoomed
                ),
                        Bindings.or(
                Bindings.greaterThan(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX()), Bindings.min(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX())), Constants.WINDOW_WIDTH*0.85),
                Bindings.greaterThan(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY()), Bindings.min(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY())), Constants.WINDOW_HEIGHT*0.85)
                ))).then(Bindings.multiply(Bindings.max(
                        Bindings.subtract(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX()), Bindings.min(c1.getTrackableCoordinateX(), c2.getTrackableCoordinateX())), Constants.WINDOW_WIDTH*0.85),
                Bindings.subtract(Bindings.subtract(Bindings.max(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY()), Bindings.min(c1.getTrackableCoordinateY(), c2.getTrackableCoordinateY())), Constants.WINDOW_HEIGHT*0.85)
                ), -2)
        ).otherwise(

                v
                /*Bindings.when(Bindings.not(Bindings.lessThan(w, maxZoomOutProp))).then(w).otherwise(v)*/

        ));
    }

    /**
     * initializes trackable coordinate and perspective values
     */
    private void initCameraProperties() {
        x = new SimpleDoubleProperty();
        y = new SimpleDoubleProperty();
        z = new SimpleDoubleProperty(0);
        w = new SimpleDoubleProperty(0);
        v = new SimpleDoubleProperty(0);

        requiresZoom = new SimpleBooleanProperty(false);
        isTooZoomed = new SimpleBooleanProperty(false);

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
        isTooZoomed.set(isTooZoomedOut());
        this.setTranslateZ(z.get());
    }

    /**
     * updates perspective based on 2 characters
     * @return 0 if no change in perspective is required and 1 otherwise or -1 if change could not happen
     */
    public int updatePerspective() {
        try {

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * checks if camera's view is close to the border horizontally
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
     * @return true if camera's view is close and false otherwise
     */
    private boolean isCloseToBorderY(double offset) {
        if(y.get() <= 0 + offset || y.get() >= Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT - offset) {
            return true;
        }
        return false;
    }

    /**
     * checks if 2 characters are too far away from each other to change the perspective of the camera
     * @param x1 coordinate of the first character in x axis
     * @param y1 coordinate of the first character in y axis
     * @param x2 coordinate of the second character in x axis
     * @param y2 coordinate of the second character in y axis
     * @return true if zoom out should occur
     */
    private boolean requiresZoomOut(DoubleProperty x1, DoubleProperty y1, DoubleProperty x2, DoubleProperty y2) {
        double dx = Math.abs(x1.get() - x2.get());
        double dy = Math.abs(y1.get() - y2.get());

        if(dx > Constants.WINDOW_WIDTH || dy > Constants.WINDOW_HEIGHT) {
            return true;
        }
        return false;
    }

    private double setZoomOut(DoubleProperty x1, DoubleProperty y1, DoubleProperty x2, DoubleProperty y2) {
        double dx = Math.abs(x1.get() - x2.get()) - Constants.WINDOW_WIDTH;
        double dy = Math.abs(y1.get() - y2.get()) - Constants.WINDOW_HEIGHT;

        return Math.max(dx, dy) * -1;
    }

    /**
     * checks if zooming out would go pass borders or if the max zoom out value is reached
     * @return true is zoom out is too much and false otherwise
     */
    private boolean isTooZoomedOut() {
        double zoomOutPerArea = -0.8; // Per one change in z, you must add offset so that camera wouldn't go over the boarder
        double maxZoomOutProp = -500;

        System.out.println("Passed: " + w.get() * zoomOutPerArea);

        if(isCloseToBorderX(z.get() * zoomOutPerArea) || isCloseToBorderY(z.get() * zoomOutPerArea) || z.get() < maxZoomOutProp) {
            System.out.println("TRUE");

            if(lastTrue) {
                v.setValue(z.get());
                lastTrue = false;
            }
            return true;
        }
        lastTrue = true;
        return false;
    }

}

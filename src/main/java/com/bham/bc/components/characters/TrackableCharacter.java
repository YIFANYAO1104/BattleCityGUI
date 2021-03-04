package com.bham.bc.components.characters;

import javafx.beans.property.SimpleDoubleProperty;

public interface TrackableCharacter {
    /**
     * initializes coordinates for tracking a Character object
     */
    void initTrackableCoordinates();

    /**
     * gets the x coordinate as a DoubleProperty of a Character object
     * @return SimpleDoubleProperty representing position in x axis
     */
    SimpleDoubleProperty getTrackableCoordinateX();

    /**
     * gets the y coordinate as a DoubleProperty of a Character object
     * @return SimpleDoubleProperty representing position in y axis
     */
    SimpleDoubleProperty getTrackableCoordinateY();
}

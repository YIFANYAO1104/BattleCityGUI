package com.bham.bc.components.characters;

import javafx.beans.property.SimpleDoubleProperty;

public interface TrackableCharacter {
    /**
     * initializes coordinate for tracking
     */
    void initTrackableCoordinate();
    SimpleDoubleProperty getTrackableCoordinateX();
    SimpleDoubleProperty getTrackableCoordinateY();
}

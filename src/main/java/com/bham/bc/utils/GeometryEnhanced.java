package com.bham.bc.utils;

import javafx.geometry.Point2D;

public class GeometryEnhanced {

    static public final double MaxDouble = Double.MAX_VALUE;
    static public final double MinDouble = Double.MIN_VALUE;

    static public boolean isZero(Point2D p) {
        return p.dotProduct(p) < MinDouble;
    }
}

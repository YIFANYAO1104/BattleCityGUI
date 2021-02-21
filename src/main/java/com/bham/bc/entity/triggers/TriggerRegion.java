/**
 * Desc: class to define a region of influence for a trigger. A TriggerRegion
 * has one method, isTouching, which returns true if a given position is inside
 * the region
 */
package com.bham.bc.entity.triggers;


import javafx.geometry.Point2D;

abstract class TriggerRegion {
    /**
     * returns true if an entity of the given size and position is intersecting
     * the trigger region.
     */
    abstract boolean isTouching(Point2D EntityPos, Point2D EntityRadius);
}
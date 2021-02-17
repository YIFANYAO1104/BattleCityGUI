/**
 *  class to define a rectangular region of influence
 */
package com.bham.bc.entity.triggers;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class TriggerRegionRectangle extends TriggerRegion {

    private Rectangle rectangle;

    public TriggerRegionRectangle(Point2D pos, Point2D radius) {
        rectangle = new Rectangle(pos.getX(), pos.getY(), radius.getX(), radius.getY());
    }

    /**
     * there's no need to do an accurate (and expensive) circle v rectangle
     * intersection test. Instead we'll just test the bounding box of the given
     * circle with the rectangle.
     */
    public boolean isTouching(Point2D pos, Point2D EntityRadius) {
        Rectangle r = new Rectangle(pos.getX(), pos.getY(), EntityRadius.getX(), EntityRadius.getY());
        return r.intersects(this.rectangle.getBoundsInLocal());
    }
}
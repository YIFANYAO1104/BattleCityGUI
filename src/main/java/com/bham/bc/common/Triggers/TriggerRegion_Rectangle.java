/*
 *  class to define a circular region of influence
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.common.Triggers;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class TriggerRegion_Rectangle extends TriggerRegion {

    private Rectangle rectangle;

    public TriggerRegion_Rectangle(Point2D pos, Point2D radius) {
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
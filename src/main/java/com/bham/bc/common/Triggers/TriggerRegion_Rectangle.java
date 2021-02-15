/*
 *  class to define a circular region of influence
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.common.Triggers;

import java.awt.*;

public class TriggerRegion_Rectangle extends TriggerRegion {

    private Rectangle rectangle;

    public TriggerRegion_Rectangle(Vector2D pos, Vector2D radius) {
        rectangle = new Rectangle(pos.x, pos.y, radius.x, radius.y);
    }

    /**
     * there's no need to do an accurate (and expensive) circle v rectangle
     * intersection test. Instead we'll just test the bounding box of the given
     * circle with the rectangle.
     */
    public boolean isTouching(Vector2D pos, Vector2D EntityRadius) {
        Rectangle r = new Rectangle(pos.x, pos.y, EntityRadius.x, EntityRadius.y);
        return r.intersects(this.rectangle);
    }
}
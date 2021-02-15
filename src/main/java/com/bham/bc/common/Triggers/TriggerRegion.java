/**
 * Desc: class to define a region of influence for a trigger. A TriggerRegion
 * has one method, isTouching, which returns true if a given position is inside
 * the region
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.common.Triggers;


abstract class TriggerRegion {
    /**
     * returns true if an entity of the given size and position is intersecting
     * the trigger region.
     */
    abstract boolean isTouching(Vector2D EntityPos, Vector2D EntityRadius);
}

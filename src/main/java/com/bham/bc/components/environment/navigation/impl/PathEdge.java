/**
 * Desc: class to represent a path edge. This path can be used by a path planner
 * in the creation of paths.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.components.environment.navigation.impl;

import javafx.geometry.Point2D;

public class PathEdge {
    //positions of the source and destination nodes this edge connects

    private Point2D source;
    private Point2D destination;
    /**
     * the behavior associated with traversing this edge
     */

    public PathEdge(Point2D source, Point2D destination) {
        this.source = new Point2D(source.getX(),source.getY());
        this.destination = new Point2D(destination.getX(),destination.getY());
    }

    public PathEdge(PathEdge edge) {
        this(edge.source, edge.destination);
    }

    public Point2D getDestination() {
        return new Point2D(destination.getX(),destination.getY());
    }

    public void setDestination(Point2D newDest) {
        destination = new Point2D(newDest.getX(),newDest.getY());
    }

    public Point2D source() {
        return new Point2D(source.getX(),source.getY());
    }

    public void setSource(Point2D newSource) {
        source = new Point2D(newSource.getX(),newSource.getY());
    }
}
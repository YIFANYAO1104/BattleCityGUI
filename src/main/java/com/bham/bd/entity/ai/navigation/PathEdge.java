/**
 * Desc: class to represent a path edge. This path can be used by a path planner
 * in the creation of paths.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bd.entity.ai.navigation;

import com.bham.bd.entity.graph.edge.GraphEdge;
import javafx.geometry.Point2D;

/**
 * Contains information of a path edge
 * Will be used when an agent is following a path
 */
public class PathEdge {
    //positions of the source and destination nodes this edge connects

    private Point2D source;
    private Point2D destination;

    public void setBehavior(int behavior) {
        this.behavior = behavior;
    }

    /**
     * The behavior might be followed by an agent when passing through this path edge
     * see {@link GraphEdge}
     */
    private int behavior;

    /**
     * Constrcut a path Edge, sets behavior to GraphEdge.normal by default
     * @param source the start point of this edge
     * @param destination the end point of this edge
     */
    public PathEdge(Point2D source, Point2D destination) {
        this.source = new Point2D(source.getX(),source.getY());
        this.destination = new Point2D(destination.getX(),destination.getY());
        behavior = GraphEdge.normal;
    }

    /**
     * Constrcut a path edge
     * @param source
     * @param destination
     * @param behavior
     */
    public PathEdge(Point2D source, Point2D destination, int behavior) {
        this.source = new Point2D(source.getX(),source.getY());
        this.destination = new Point2D(destination.getX(),destination.getY());
        this.behavior = behavior;
    }

    public PathEdge(PathEdge edge) {
        this(edge.source, edge.destination, edge.behavior);
    }

    public Point2D getDestination() {
        return new Point2D(destination.getX(),destination.getY());
    }

    public void setDestination(Point2D newDest) {
        destination = new Point2D(newDest.getX(),newDest.getY());
    }

    public Point2D getSource() {
        return new Point2D(source.getX(),source.getY());
    }

    public void setSource(Point2D newSource) {
        source = new Point2D(newSource.getX(),newSource.getY());
    }

    @Override
    public String toString() {
        return "[" + source + ", " + destination + ']';
    }

    public int getBehavior() {
        return behavior;
    }
}
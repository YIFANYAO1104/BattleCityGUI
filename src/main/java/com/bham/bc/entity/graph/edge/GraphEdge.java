package com.bham.bc.entity.graph.edge;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.bham.bc.entity.graph.NodeTypeEnum.invalid_node_index;

/**
 * Class represents an edge between 2 nodes in a SparseGraph
 * This Edge has direction
 */
public class GraphEdge {

    /**
     * The index{@link Integer} of the start node
     */
    protected int from;

    /**
     * The index{@link Integer} of the destination node
     */
    protected int to;
    /**
     * the cost{@link Double} of traveling the edge
     */
    protected double cost;

    final public static int normal = 0;
    final public static int shoot = 1;
    protected int behavior;

    /**
     * Creates a graph edge
     * set behavior to GraphEdge.normal by default
     * @param from the index of the start node
     * @param to the index of the end node
     * @param cost the cost between 2 nodes
     */
    public GraphEdge(int from, int to, double cost) {
        this.cost = cost;
        this.from = from;
        this.to = to;
        behavior = normal;
    }

    /**
     * Creates a graph edge
     * set behavior to GraphEdge.normal by default
     * set cost to 1.0 by default
     * @param from the index of the start node
     * @param to the index of the end node
     */
    public GraphEdge(int from, int to) {
        cost = 1.0;
        this.from = from;
        this.to = to;
        behavior = normal;
    }

    /**
     * getter of start node
     * @return the start node in an edge
     */
    public int getFrom() {
        return from;
    }

    /**
     * setter of start node
     * @param newIndex the new node
     */
    public void setFrom(int newIndex) {
        from = newIndex;
    }

    /**
     * getter of end node
     * @return the end node in an edge
     */
    public int getTo() {
        return to;
    }

    /**
     * setter of end node
     * @param newIndex the new node
     */
    public void setTo(int newIndex) {
        to = newIndex;
    }

    /**
     * getter of cost
     * @return the cost in an edge
     */
    public double getCost() {
        return cost;
    }

    /**
     * setter of cost
     * @param newCost the new cost
     */
    public void setCost(double newCost) {
        cost = newCost;
    }

    /**
     * setter of behavior
     * @param behavior the behavior to be set
     */
    public void setBehavior(int behavior) {
        this.behavior = behavior;
    }

    /**
     * the getter of a behavior
     * @return the behavior on the edge
     */
    public int getBehavior() {
        return behavior;
    }

    //these two operators are required
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GraphEdge)) {
            return false;
        }
        GraphEdge rhs = (GraphEdge) o;
        return rhs.from == this.from
                && rhs.to == this.to
                && rhs.cost == this.cost;
    }
}

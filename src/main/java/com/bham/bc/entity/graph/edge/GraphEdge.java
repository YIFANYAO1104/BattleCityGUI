package com.bham.bc.entity.graph.edge;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.bham.bc.entity.graph.NodeTypeEnum.invalid_node_index;

/**
 * Class represents an edge between 2 nodes in a SparseGraph
 * This Edge has direction
 */
public class GraphEdge {

    //An edge connects two nodes. Valid node indices are always positive.
    protected int from;
    protected int to;
    //the cost of traversing the edge
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

    public int getFrom() {
        return from;
    }

    public void setFrom(int NewIndex) {
        from = NewIndex;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int NewIndex) {
        to = NewIndex;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double NewCost) {
        cost = NewCost;
    }

    public void setBehavior(int behavior) {
        this.behavior = behavior;
    }

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

    //for reading and writing to streams.
    public OutputStream print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.print("m_iFrom: ");
        ps.print(from);
        ps.print(" m_iTo: ");
        ps.print(to);
        ps.print(" m_dCost: ");
        ps.print(cost);
        ps.println();
        return os;
    }
}

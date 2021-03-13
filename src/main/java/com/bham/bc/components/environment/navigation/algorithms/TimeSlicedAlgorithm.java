package com.bham.bc.components.environment.navigation.algorithms;

import com.bham.bc.components.environment.navigation.impl.PathEdge;
import com.bham.bc.utils.graph.edge.GraphEdge;

import java.util.List;

/**
 * base class to define a common interface for graph search algorithms
 */
abstract public class TimeSlicedAlgorithm {
    /**
     * When called, this method runs the algorithm through one search cycle.
     * The method returns an enumerated value (target_found,
     * target_not_found, search_incomplete) indicating the status of the
     * search
     */

    public abstract int cycleOnce();

    /**
     * returns the vector of edges that the algorithm has examined
     */
    public abstract List<GraphEdge> getSPT();

    /**
     * returns the total cost to the target
     */
    public abstract double getCostToTarget();

    /**
     * returns a list of node indexes that comprise the shortest path from
     * the source to the target
     */
    public abstract List<Integer> getPathToTarget();

    /**
     * returns the path as a list of PathEdges
     */
    public abstract List<PathEdge> getPathAsPathEdges();
}

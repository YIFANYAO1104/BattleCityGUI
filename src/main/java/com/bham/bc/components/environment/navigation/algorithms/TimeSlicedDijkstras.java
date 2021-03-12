package com.bham.bc.components.environment.navigation.algorithms;

import com.bham.bc.components.environment.navigation.impl.PathEdge;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Dijkstra's algorithm class modified to spread a search over multiple
 * update-steps
 */
public class TimeSlicedDijkstras//<termination_condition extends TerminationCondition>
        extends TimeSlicedAlgorithm {

    public TimeSlicedDijkstras(final SparseGraph G,
                                    int source,
                                    int target
                                    /*termination_condition TerminationCondition*/) {
    }

    @Override
    public int cycleOnce() {
        return 0;
    }

    @Override
    public List<GraphEdge> getSPT() {
        return null;
    }

    @Override
    public double getCostToTarget() {
        return 0;
    }

    @Override
    public List<Integer> getPathToTarget() {
        return null;
    }

    @Override
    public List<PathEdge> getPathAsPathEdges() {
        return null;
    }
}

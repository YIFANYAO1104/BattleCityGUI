package com.bham.bc.entity.ai.navigation.algorithms;

import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bc.entity.ai.navigation.PathEdge;
import com.bham.bc.entity.graph.edge.GraphEdge;

import java.util.List;

/**
 * Base class to define a common interface for graph search algorithms
 */
abstract public class TimeSlicedAlgorithm {
    /**
     * When called, this method runs the algorithm through one search cycle.
     * The method returns an enumerated value (target_found,
     * target_not_found, search_incomplete) indicating the status of the
     * search
     * @return {@link SearchStatus} to indicate the searching status after a run
     */
    public abstract SearchStatus cycleOnce();

    /**
     * called by PathPlanner when a searching task was ended
     * @return a lst of {@link PathEdge}
     */
    public abstract List<PathEdge> getPathAsPathEdges();

    /**
     * set expand condition for algorithm.
     * It will also reset the one inside algorithm.
     * @param expandCondition see{@link ExpandPolicies}
     */
    public abstract void setExpandCondition(ExpandPolicies.ExpandCondition expandCondition);

    public abstract boolean isTriggerActive();
}

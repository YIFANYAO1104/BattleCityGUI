package com.bham.bc.entity.ai.navigation.algorithms.policies;

import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;

/**
 * A class contains many sub classes for EdgeIterator in SparseGraph
 * Sub classes define conditions that an EdgeIterator should follow
 * when iterating through the adjacency edges of the given node
 */
public class ExpandPolicies {

    public static interface ExpandCondition {
        /**
         * The basic method for condition
         * @param G the SparseGraph where EdgeIterator is in
         * @param target the GraphEdge to be explored
         * @return true if this condition is being satisfied
         */
        public  boolean isSatisfied(final SparseGraph G, GraphEdge target);
    }

    /**
     * a condition class to exclude edges whose destination node has -1 as index
     * Should be the default condition for EdgeIterator when the game is running
     */
    public static class NoInvalid implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            return G.getNode(target.getTo()).getIndex()!=-1;
        }
    }

    /**
     * a condition class to exclude edges whose destination node has -1 as index
     * or behavior is GraphEdge.shoot
     */
    public static class NoShoot implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            return (G.getNode(target.getTo()).getIndex()!=-1) &&
                    target.getBehavior()!=GraphEdge.shoot;
        }
    }

    /**
     * a condition class to expand all the nodes even when the index is -1
     */
    @Deprecated
    public static class ExpandAll implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            return true;
        }
    }
}

package com.bham.bc.entity.ai.navigation.algorithms.policies;

import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.node.NavNode;

/**
 * A class contains many sub classes for Dijkstra Algorithm
 * Sub classes define conditions that when will Dijkstra Algorithm end
 */
public class TerminationPolices {

    public static interface TerminationCondition {
        /**
         * The basic method for condition
         * Test whether the node we are going to explore satisfies the condition
         * @param G SparseGraph where the Dijkstra Algorithm is exploring
         * @param target the item that Dijkstra Algorithm want to find
         * @param curNodeIndex the node that Dijkstra Algorithm want to explore
         * @return the value representing whether the condition is satisfied
         */
        public boolean isSatisfied(final SparseGraph G, ItemType target, int curNodeIndex);
    }

    /**
     * a condition class to check whether a node contains the desired active trigger
     */
    public static class FindActiveTrigger implements TerminationCondition {
        @Override
        public boolean isSatisfied(SparseGraph navGraph, ItemType target, int curNodeIndex) {
            //get a reference to the node at the given node index
            final NavNode node = navGraph.getNode(curNodeIndex);

            //if the extrainfo field is pointing to a giver-trigger, test to make sure
            //it is active and that it is of the correct type.
            if ((node.getExtraInfo() != null)
                    && node.getExtraInfo().active()
                    && (node.getExtraInfo().getItemType() == target)) {
                return true;
            }

            return false;
        }
    }
}

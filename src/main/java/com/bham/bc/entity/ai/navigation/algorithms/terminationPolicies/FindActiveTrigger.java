package com.bham.bc.entity.ai.navigation.algorithms.terminationPolicies;

import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.node.NavNode;

public class FindActiveTrigger extends TerminationCondition{
    @Override
    public boolean isSatisfied(SparseGraph navGraph, ItemType target, int curNodeIndex) {
        //get a reference to the node at the given node index
        final NavNode node = navGraph.getNode(curNodeIndex);

        //if the extrainfo field is pointing to a giver-trigger, test to make sure
        //it is active and that it is of the correct type.
        if ((node.getExtraInfo() != null)
                && node.getExtraInfo().isActive()
                && (node.getExtraInfo().getItemType() == target)) {
            return true;
        }

        return false;
    }
}

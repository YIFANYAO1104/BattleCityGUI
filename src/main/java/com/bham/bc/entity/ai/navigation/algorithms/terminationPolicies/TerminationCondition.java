package com.bham.bc.entity.ai.navigation.algorithms.terminationPolicies;

import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.SparseGraph;

public abstract class TerminationCondition {
    public abstract boolean isSatisfied(final SparseGraph G, ItemType target, int CurrentNodeIdx);
}

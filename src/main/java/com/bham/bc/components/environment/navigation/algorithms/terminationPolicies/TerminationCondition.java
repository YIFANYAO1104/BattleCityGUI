package com.bham.bc.components.environment.navigation.algorithms.terminationPolicies;

import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.utils.graph.SparseGraph;

public abstract class TerminationCondition {
    public abstract boolean isSatisfied(final SparseGraph G, ItemType target, int CurrentNodeIdx);
}

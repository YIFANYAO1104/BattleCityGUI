package com.bham.bc.entity.ai.navigation.algorithms.policies;

import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.ExtraInfo;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;

public class ExpandPolicies {

    public static interface ExpandCondition {
        public  boolean isSatisfied(final SparseGraph G, GraphEdge target);
    }

    public static class ExpandAll implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            return true;
        }
    }

    public static class NoSoft implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            ExtraInfo x = G.getNode(target.To()).getExtraInfo();
            return x==null || x.getItemType()!=ItemType.SOFT;
        }
    }
}

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

    public static class NoShoot implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            return target.getBehavior()!=GraphEdge.shoot;
        }
    }

    public static class NoInvalid implements ExpandCondition {
        @Override
        public boolean isSatisfied(final SparseGraph G, GraphEdge target) {
            return G.getNode(target.To()).Index()!=-1;
        }
    }
}

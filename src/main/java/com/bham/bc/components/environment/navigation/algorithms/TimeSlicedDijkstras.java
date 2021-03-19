package com.bham.bc.components.environment.navigation.algorithms;

import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.components.environment.navigation.SearchStatus;
import com.bham.bc.components.environment.navigation.algorithms.terminationPolicies.TerminationCondition;
import com.bham.bc.components.environment.navigation.impl.PathEdge;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;

import java.math.BigDecimal;
import java.util.*;

/**
 * Dijkstra's algorithm class modified to spread a search over multiple
 * update-steps
 */
public class TimeSlicedDijkstras<termination_condition extends TerminationCondition>
        extends TimeSlicedAlgorithm {

    private final SparseGraph navGraph;
    private int source;
    private int target;
    private ItemType targetType;
    ArrayList<Integer> parent;
    ArrayList<Double> distance;
    Set<Integer> seen;
    Queue<MyPairs> pq;

    termination_condition tm;


    public TimeSlicedDijkstras(final SparseGraph G,
                               int source,
                               ItemType targetType,
                                termination_condition terminationCondition) {
        this.navGraph = G;
        this.source = source;
        this.target = -1;
        this.targetType = targetType;
        this.tm = terminationCondition;


        int number = this.navGraph.numNodes();
        this.parent = new ArrayList<Integer>(Collections.nCopies(number, null));
        this.distance = new ArrayList<Double>(Collections.nCopies(number, Double.MAX_VALUE));
        this.seen = new TreeSet<Integer>();
        distance.set(source,0d);

        Comparator<MyPairs> comparator = new Comparator<MyPairs>() {
            @Override
            public int compare(MyPairs a, MyPairs b) {
                BigDecimal costA = new BigDecimal(a.getValue());
                BigDecimal costB = new BigDecimal(b.getValue());
                return costA.compareTo(costB);
            }
        };
        this.pq = new PriorityQueue<MyPairs>(comparator);
        pq.add(new MyPairs(source,0d));
    }

    @Override
    public SearchStatus cycleOnce() {
        while (!pq.isEmpty()){
            MyPairs pair = pq.poll();
            double dist = pair.getValue();
            int node = pair.getKey();
            seen.add(node);

            if (tm.isSatisfied(navGraph, targetType, node)) {
                target = node;
                return SearchStatus.target_found;
            }

            SparseGraph.EdgeIterator ConstEdgeItr = new SparseGraph.EdgeIterator(this.navGraph, node);

            //for each edge connected to the next closest node
            for (GraphEdge pE = ConstEdgeItr.begin(); !ConstEdgeItr.end(); pE = ConstEdgeItr.next()){
                int tempNode = pE.To();
                if (!navGraph.getNode(tempNode).isValid()) continue;
                if (!seen.contains(tempNode)){
                    double newCost = dist + pE.Cost();
                    if (newCost < distance.get(tempNode)){
                        pq.add(new MyPairs(tempNode,newCost));
                        parent.set(tempNode,node);
                        distance.set(tempNode,newCost);
                    }
                }
            }
        }
        return SearchStatus.target_not_found;
    }

    public double getDistance(int target) {
        return distance.get(target);
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
        List<PathEdge> path = new LinkedList<PathEdge>();

        //just return an empty path if no target or no path found
        if (target < 0) {
            return path;
        }

        int nd = target;

        while ((nd != source) && (parent.get(nd) != null)) {
            path.add(0, 
                    new PathEdge(navGraph.getNode(parent.get(nd)).Pos(), navGraph.getNode(nd).Pos()));

            nd = parent.get(nd);
        }

        return path;
    }

    public static void main(String[] args) {
        class MyPairs extends AbstractMap.SimpleEntry<Integer, Double>{
            MyPairs(Integer i, Double d){
                super(i,d);
            }
        }
        Comparator<MyPairs> comparator = new Comparator<MyPairs>() {
            @Override
            public int compare(MyPairs a, MyPairs b) {
                BigDecimal costA = new BigDecimal(a.getValue());
                BigDecimal costB = new BigDecimal(b.getValue());
                return costA.compareTo(costB);
            }
        };

        Queue<MyPairs> queue1 = new PriorityQueue<MyPairs>(comparator);
        queue1.add(new MyPairs(1,1.0));
        queue1.add(new MyPairs(3,3.0));
        queue1.add(new MyPairs(1,1.0));
        queue1.add(new MyPairs(2,3.0));
        queue1.add(new MyPairs(1,6.0));
        queue1.add(new MyPairs(5,5.0));
        queue1.add(new MyPairs(1,1.0));
        while (!queue1.isEmpty()){
            System.out.println(queue1.poll());
        }
    }

    class MyPairs extends AbstractMap.SimpleEntry<Integer, Double>{
        MyPairs(Integer node, Double cost){
            super(node,cost);
        }
    }
}

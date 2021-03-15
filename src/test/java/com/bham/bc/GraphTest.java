package com.bham.bc;


import com.bham.bc.components.environment.navigation.algorithms.TimeSlicedDijkstras;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class GraphTest {
    private Object[] dijkstraParams1()
    {
        Object[][] typeListList = { //
                { 0, 1, 3 }, //
                { 0, 2, 8 }, //
                { 0, 3, 2 }, //
                { 0, 4, 9 }, //
                { 0, 5, 12 } //
        };
        return typeListList;
    }

    private Object[] dijkstraParams2()
    {
        Object[][] typeListList = { //
                { 0, 1, 300 }, //
                { 0, 2, 800 }, //
                { 0, 3, 200 }, //
                { 0, 4, 900 }, //
                { 0, 5, 1200 } //
        };
        return typeListList;
    }

    @Test
    @Parameters(method = "dijkstraParams1")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void test1(int source, int target, double expectedDistance) throws Exception {
        int[][] input = new int[][] { //
                { 0, 1, 4 }, //
                { 0, 3, 2 }, //
                { 1, 2, 5 }, //
                { 1, 3, 1 }, //
                { 2, 3, 8 }, //
                { 2, 4, 1 }, //
                { 2, 5, 6 }, //
                { 3, 4, 9 }, //
                { 4, 5, 3 } //
        };
        SparseGraph graph = new SparseGraph<NavNode, GraphEdge>(false);
        addNodes(graph,input);
        addEdges(graph,input);

        TimeSlicedDijkstras tsdj = new TimeSlicedDijkstras(graph, source, target);
        tsdj.cycleOnce();

        assertThat(tsdj.getDistance(target), is(expectedDistance));
    }

    @Test
    @Parameters(method = "dijkstraParams2")
    @TestCaseName("{method}({params}) [{index}]") //can use {0} {1}... for params from current param set
    public void test2(int source, int target, double expectedDistance) throws Exception {
        int[][] input = new int[][] { //
                { 0, 1, 400 }, //
                { 0, 3, 200 }, //
                { 1, 2, 500 }, //
                { 1, 3, 100 }, //
                { 2, 3, 800 }, //
                { 2, 4, 100 }, //
                { 2, 5, 600 }, //
                { 3, 4, 900 }, //
                { 4, 5, 300 } //
        };
        SparseGraph graph = new SparseGraph<NavNode, GraphEdge>(false);
        addNodes(graph,input);
        addEdges(graph,input);

        TimeSlicedDijkstras tsdj = new TimeSlicedDijkstras(graph, source, target);
        tsdj.cycleOnce();

        assertThat(tsdj.getDistance(target), is(expectedDistance));
    }

    @Test
    public void test3() throws Exception {
        ArrayList<Double> cost = new ArrayList<Double>();
        cost.add(1.0);
        cost.add(2.0);
        cost.add(3.0);
        cost.add(4.0);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                BigDecimal costA = new BigDecimal(cost.get(a));
                BigDecimal costB = new BigDecimal(cost.get(b));
                return costA.compareTo(costB);
            }
        };
        Queue<Integer> queue1 = new PriorityQueue<Integer>(comparator);
        queue1.add(2);
        queue1.add(1);
        queue1.add(3);
        queue1.add(3);
        queue1.add(2);
        queue1.add(1);
        while (!queue1.isEmpty()){
            System.out.println(queue1.poll());
        }
    }

    private void addNodes(SparseGraph graph, int[][] edges){
        Set<Integer> set = new HashSet<>();
        int maxIndex= -1;
        for (int[] edge : edges)
        {
            if (edge[0]>maxIndex) maxIndex = edge[0];
            if (edge[1]>maxIndex) maxIndex = edge[1];
        }
        for (int i = 0; i <= maxIndex; i++) {
            graph.addNode(new NavNode(i,new Vector2D(0,0)));
        }
    }
    private void addEdges(SparseGraph graph, int[][] edges) throws Exception {
        for (int[] edge : edges)
        {
            if (edge.length != 3) throw new Exception();
            graph.addEdge(new GraphEdge(edge[0],edge[1],edge[2]));
        }
    }
}
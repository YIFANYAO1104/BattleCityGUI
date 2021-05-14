package com.bham.bd.entity.ai.navigation.algorithms.astar;


import com.bham.bd.entity.ai.navigation.SearchStatus;
import com.bham.bd.entity.ai.navigation.algorithms.TimeSlicedAlgorithm;
import com.bham.bd.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bd.entity.ai.navigation.PathEdge;
import com.bham.bd.entity.graph.SparseGraph;
import com.bham.bd.entity.graph.edge.GraphEdge;
import com.bham.bd.entity.graph.node.GraphNode;
import com.bham.bd.entity.graph.node.NavNode;
import javafx.geometry.Point2D;

import java.util.*;

/**
 * a A* class that enables a search to be completed over multiple
 * update-steps
 */
public class TimeSlicedAStar
        extends TimeSlicedAlgorithm {

    private Queue<Node> openList = new PriorityQueue<>();
    private HashSet<GraphNode> register = new HashSet<>();

    private SparseGraph sg;
    private Node root;
    private Node goal;
    private ArrayList<NavNode> routine;
    private ExpandPolicies.ExpandCondition expandCondition;

    public TimeSlicedAStar(final SparseGraph sg1,
                                int source,
                                int target,
                                ExpandPolicies.ExpandCondition expandCondition
                                /*heuristic h*/) {
        this.root =  new Node(sg1.getNode(source));
        this.goal = new Node(sg1.getNode(target));
        this.sg = sg1;
        this.root =  new Node(root,null,0,getDistance(root));
        openList.add(root);
        this.expandCondition = expandCondition;
    }

    @Override
    public SearchStatus cycleOnce() {
        if (openList.isEmpty()) return SearchStatus.target_not_found;
        if (!openList.isEmpty()){
            expandNode(openList.poll());

            if (register.contains(goal.getNode())) {
//                System.out.println("SearchStatus.target_found");
                return SearchStatus.target_found;
            }
        }
        return SearchStatus.search_incomplete;
//        while (!openList.isEmpty()){
//            expandNode(openList.poll());
//
//            if (register.contains(goal.getNode())) {
//                return SearchStatus.target_found;
//            }
//        }
//        return SearchStatus.target_not_found;
    }

    private void expandNode(Node n1){
        SparseGraph.EdgeIterator ConstEdgeItr = new SparseGraph.EdgeIterator(
                this.sg,
                n1.getNode().getIndex(),
                expandCondition);
        while (ConstEdgeItr.hasNext()) {
            GraphEdge gn1 = ConstEdgeItr.next();

            Node nn1 = new Node(sg.getNode(gn1.getTo()));
            double edgecost = getCost(n1,nn1);
            double diagonalDis = getDiagonalDis(n1);
            Node nn2 = new Node(nn1, n1, edgecost+n1.getCost(), diagonalDis);
            if(!register.contains(nn2.getNode())){
                openList.add(nn2);
                register.add(nn2.getNode());
            }
        }
    }


    @Override
    public List<PathEdge> getPathAsPathEdges() {
        Node nn1 = null;
        while (!openList.isEmpty()){
            if (openList.peek().getNode() == goal.getNode()){
                nn1 = openList.peek();
                break;
            } else{
                openList.poll();
            }
        }

        ArrayList<NavNode> temp = new ArrayList<>();
        temp.add((NavNode) goal.getNode());
        while (nn1.getParentNode() != null){
            temp.add((NavNode) nn1.getParentNode().getNode());
            nn1 = nn1.getParentNode();
        }
        routine = temp;



        List<PathEdge> path = new LinkedList<PathEdge>();

        for(int i = routine.size()-1;i>0;i--){
            NavNode n1 = routine.get(i);
            NavNode n2 = routine.get(i-1);
            path.add(new PathEdge(n1.getPosition(),n2.getPosition(),sg.getEdge(n1.getIndex(),n2.getIndex()).getBehavior()));
        }

        return path;
    }


//    public ArrayList<NavNode> search(){
//        Node n1 = start();
//        if (n1==null) return null;
//
//
//        ArrayList<NavNode> temp = new ArrayList<>();
//
//        temp.add((NavNode) goal.getNode());
//        while (n1.getParentNode() != null){
//            temp.add((NavNode) n1.getParentNode().getNode());
//            n1 = n1.getParentNode();
//        }
//
//        return temp;
//    }

//    public Node start(){
////        expandNode(root);
//
//        while (!openList.isEmpty()){
//            expandNode(openList.poll());
//            if (register.contains(goal.getNode())) return SearchStatus.target_found;
////            System.out.println("finding");
//        }
////        System.out.println("find that !");
//        while (!openList.isEmpty()){
//            if (openList.peek().getNode() == goal.getNode())
//                return openList.peek();
//            else
//                openList.poll();
//        }
//
//        return null;
//    }




    private double getDiagonalDis(Node n1){
        NavNode node1 = (NavNode)n1.getNode();
        NavNode node2 = (NavNode)goal.getNode();
        Point2D dis = node1.getPosition().subtract(node2.getPosition());
        double x = Math.abs(dis.getX());
        double y = Math.abs(dis.getY());
        return x + 0.41 * y;                    // According the book Game AI. It states that the good way to calculate Dis.
    }
    private double getDistance(Node n1){
        NavNode n2 = (NavNode)n1.getNode();
        NavNode n3 = (NavNode)goal.getNode();
        Point2D x = n2.getPosition().subtract(n3.getPosition());
        return Math.sqrt(x.dotProduct(x));
    }

    private double getCost(Node n1 , Node n2){
        GraphEdge e1 = sg.getEdge(n1.getNode().getIndex(), n2.getNode().getIndex());
        return e1.getCost();
    }

    public void setExpandCondition(ExpandPolicies.ExpandCondition expandCondition){
        this.expandCondition = expandCondition;
    }

    public boolean isTriggerActive(){
        return false;
    }


    public class Node implements Comparable<Node> {

        private GraphNode node;
        private Node parentNode;
        private double cost;
        private double diagonalDis;

        public Node(GraphNode node1){
            this.node = node1;
        }

        public Node(Node node1, Node parent, double cost1, double distance1){
            this.node =node1.getNode();
            this.parentNode = parent;
            this.cost = cost1;
            this.diagonalDis = distance1;

        }
        // update to master
        public double hers(){
            return cost+diagonalDis;
        }

        @Override
        public int compareTo(Node o)
        {
            if (o == null) return -1;

            if (this.hers() > o.hers())
                return 1;
            else if (this.hers() < o.hers()) return -1;

            return 0;
        }

        public GraphNode getNode() {
            return node;
        }

        public Node getParentNode(){
            return parentNode;
        }
        public double getDistance(){
            return diagonalDis;
        }
        public Double getCost(){return cost;}

        @Override
        public String toString() {
            return "Node{" +
                    "node=" + node.getIndex() +
                    ", parentNode=" + parentNode +
                    ", cost=" + cost +
                    ", diagonalDis=" + diagonalDis +
                    '}';
        }
    }
}
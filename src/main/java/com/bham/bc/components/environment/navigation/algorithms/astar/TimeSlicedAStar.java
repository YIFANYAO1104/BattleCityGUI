package com.bham.bc.components.environment.navigation.algorithms.astar;


import com.bham.bc.components.environment.navigation.algorithms.TimeSlicedAlgorithm;
import com.bham.bc.components.environment.navigation.impl.PathEdge;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.GraphNode;
import com.bham.bc.utils.graph.node.NavNode;

import java.util.*;

/**
 * a A* class that enables a search to be completed over multiple
 * update-steps
 */
public class TimeSlicedAStar//<heuristic extends AStarHeuristicPolicies.Heuristic>
        extends TimeSlicedAlgorithm {

    private Queue<Node> openList = new PriorityQueue<>();
    private HashSet<GraphNode> register = new HashSet<>();

    private SparseGraph sg;
    private Node root;
    private Node goal;
    private ArrayList<NavNode> routine;

    public TimeSlicedAStar(final SparseGraph sg1,
                                GraphNode source,
                                GraphNode target
                                /*heuristic h*/) {
        this.root =  new Node(source);
        this.goal = new Node(target);
        this.sg = sg1;
        this.root =  new Node(root,null,0,getDistance(root));
    }

    @Override
    public int cycleOnce() {
        Node n1 = start();

        if(n1.getNode() == goal.getNode()){
            System.out.println("output 1");
            return 1;
        }
        else{
            System.out.println("output 0");
            return 0;
        }

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
        /*TODO: FILL*/
        List<PathEdge> path = new LinkedList<PathEdge>();
        Queue<Node> noddd = new LinkedList<>(openList);

        if(routine == null) return null;

        for(Node nn:noddd){
            if(nn.getParentNode() != null){
                NavNode n1 = (NavNode) nn.getNode();
                NavNode n2 = (NavNode) nn.getParentNode().getNode();
                path.add(new PathEdge(n1.Pos(),n2.Pos()));
            }
        }

        return null;
    }


    public ArrayList<GraphNode> search(){
        Node n1 = start();
        ArrayList<GraphNode> temp = new ArrayList<>();

        temp.add(n1.getNode());
        while (n1.getParentNode() != null){
            temp.add(n1.getParentNode().getNode());
            n1 = n1.getParentNode();
        }

        return temp;
    }

    public Node start(){
        addNode(root);

        while (isFinish()){
            addNode(openList.poll());
            System.out.println("finding");
        }
        System.out.println("find that !");

        return openList.peek();
    }

    private boolean isFinish(){

//        if(openList.peek().getDistance() ==0.0)
//            return false;
        if(register.contains(goal.getNode()))
            return false;

        return true;
    }

    private void addNode(Node n1){
        for(Object gn1: sg.getNodeList(n1.getNode().Index())){
            Node nn1 = new Node((GraphNode)gn1);
            double cost = getCost(n1,nn1);
            double dis = getDistance(nn1);
            Node nn2 = new Node(nn1, n1, cost+n1.getCost(), dis);
            if(!register.contains(nn2.getNode())){
                openList.add(nn2);
                register.add(nn2.getNode());
            }
        }
    }

    private double getDistance(Node n1){
        NavNode n2 = (NavNode)n1.getNode();
        NavNode n3 = (NavNode)goal.getNode();
        return n2.Pos().ManHadunDis(n3.Pos());
    }

    private double getCost(Node n1 , Node n2){
        GraphEdge e1 = sg.getEdge(n1.getNode().Index(), n2.getNode().Index());
        return e1.Cost() * e1.Cost();
    }


    public class Node implements Comparable<Node> {

        private GraphNode node;
        private Node parentNode;
        private double cost;
        private double distance;

        public Node(GraphNode node1){
            this.node = node1;
        }

        public Node(Node node1, Node parent, double cost1, double distance1){
            this.node =node1.getNode();
            this.parentNode = parent;
            this.cost = cost1;
            this.distance = distance1;

        }

        @Override
        public int compareTo(Node o)
        {
            if (o == null) return -1;

            if (cost+distance > o.cost + o.distance)
                return 1;
            else if (cost+distance < o.cost + o.distance) return -1;

            return 0;
        }

        public GraphNode getNode() {
            return node;
        }

        public Node getParentNode(){
            return parentNode;
        }
        public double getDistance(){
            return distance;
        }
        public Double getCost(){return cost;}
    }
}
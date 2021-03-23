package com.bham.bc.utils.graph.algrithem.astar;

import com.bham.bc.utils.graph.node.GraphNode;

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

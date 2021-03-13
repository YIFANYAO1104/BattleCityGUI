package com.bham.bc.utils.graph.algrithem.astar;

import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.node.GraphNode;
import com.bham.bc.utils.graph.node.NavNode;
import com.sun.glass.events.mac.NpapiEvent;

import java.util.*;

public class Astar {

    private Queue<Node> openList = new PriorityQueue<Node>();
    private HashSet<GraphNode> register = new HashSet<>();

    private SparseGraph sg;
    private Node root;
    private Node goal;

    public Astar(GraphNode location, GraphNode goal, SparseGraph sg1){
        this.root =  new Node(location);
        this.goal = new Node(goal);
        this.sg = sg1;
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
            double cost = getCost(nn1);
            double dis = getDistance(nn1);
            Node nn2 = new Node(nn1, n1, cost, dis);
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

    private double getCost(Node n1){
        int count = 0;
        Node n3 = n1;
        while (n3.getParentNode()!=null){
                n3 = n3.getParentNode();
                count++;
        }

        return count * 16;              // 16 is the cost number which is defined in edge.cost
    }
}


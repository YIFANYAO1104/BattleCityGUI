package com.bham.bc.utils.graph.algrithem;

import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.GraphNode;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;
import javafx.geometry.Point2D;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Astar {

    private ArrayList<Integer> routiner = new ArrayList<Integer>();
    private NavNode aim;
    private NavNode root;
    private SparseGraph sg;


    public Astar( SparseGraph sg,Vector2D root, Vector2D aim){
        this.aim = sg.TrickingTank(aim);
        this.root = sg.TrickingTank(root);
        this.sg =sg;
        routiner.add(this.root.Index());
    }

    public ArrayList<Integer> search() {
        while (!routiner.contains(aim.Index())){
            int i = routiner.get(routiner.size()-1);
            LinkedList<NavNode> e1 = sg.getNodeList(i);

            if(e1 == null) return null;

            double min = Double.MAX_VALUE;
            int index = -1;
            for (NavNode n2 : e1) {
                double distance = Vector2D.Vec2DDistance(n2.Pos(), aim.Pos());
                if (distance < min) index = n2.Index();
            }
            routiner.add(index);

        }
        return routiner;
    }
}

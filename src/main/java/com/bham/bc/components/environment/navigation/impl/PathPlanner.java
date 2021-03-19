package com.bham.bc.components.environment.navigation.impl;

import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.components.environment.navigation.NavigationService;
import com.bham.bc.components.environment.navigation.algorithms.astar.TimeSlicedAStar;
import com.bham.bc.components.environment.navigation.algorithms.TimeSlicedAlgorithm;
import com.bham.bc.components.environment.navigation.algorithms.TimeSlicedDijkstras;
import com.bham.bc.components.environment.navigation.algorithms.terminationPolicies.FindActiveTrigger;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;
import javafx.geometry.Point2D;
import com.bham.bc.components.characters.Character;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PathPlanner implements NavigationService {

    private int no_closest_node_found = -1;
    /**
     * A pointer to the owner of this class
     */
    private Character owner;
    /**
     * a reference to the navgraph
     */
    private final SparseGraph navGraph;
    /**
     * a pointer to an instance of the current graph search algorithm.
     */
    private TimeSlicedAlgorithm curSearchTask;
    /**
     * a note only, being set when an request was created
     * being used when getPath() is called
     */
    private Point2D destinationPos;

    // temp value to render the graphlines
    List<PathEdge> a2 = new ArrayList<PathEdge>();

    public PathPlanner(Character owner, SparseGraph navGraph) {
        this.owner = owner;
        this.navGraph = navGraph;
        curSearchTask =null;
    }

    /**
     * @return node index. -1 if no closest node found
     */
    private int getClosestNode(Point2D pos){
        NavNode n1 = navGraph.getClosestNodeForPlayer(new Vector2D(pos));
        if(n1.isValid() ){
            return n1.Index();
        }
        return no_closest_node_found;
    }


    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param itemType for example, triggers
     * @return true if there been a graph node around bot, otherwise false
     */
    public boolean createRequest(ItemType itemType) {
        //unregister current search
        curSearchTask = null;
        //find closest node around bot, if no return false
        int closestNodeToPlayer = getClosestNode(owner.getPosition());
        if (closestNodeToPlayer == no_closest_node_found){
            return false;
        }
        //create algorithm instance
        //TODO: create Termination Condition - target
        curSearchTask = new TimeSlicedDijkstras(navGraph,closestNodeToPlayer,itemType,new FindActiveTrigger());
        //register task in time slice service
        return true;
    }

    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param targetPos the targetPos that the bot wants to reach
     * @return true if closest nodes exist around both bot and targetPosition, otherwise false
     */
    public boolean createRequest(Point2D targetPos) {

        //unregister current search
        curSearchTask = null;
        destinationPos = new Point2D(targetPos.getX(),targetPos.getY());

        //find closest node around bot, if no return false
        int closestNodeToPlayer = getClosestNode(owner.getPosition());
        if (closestNodeToPlayer == no_closest_node_found){
            return false;
        }
        //find closest node around target, if no return false
        int closestNodeToTarget = getClosestNode(targetPos);
        if (closestNodeToTarget == no_closest_node_found){
            return false;
        }

        //create algorithm instance
        curSearchTask = new TimeSlicedAStar(navGraph, navGraph.getNode(closestNodeToPlayer), navGraph.getNode(closestNodeToTarget));
        //register task in time slice service

        return true;
    }

    @Override
    public int peekRequestStatus() {
        int result = curSearchTask.cycleOnce();
        //System.out.println(a2);
        if (result == 0){
             a2 = getPath();
        }
        return result;
    }

    /**
     * called by an agent after it has been notified that a search has
     * terminated successfully.
     * @return a list of PathEdges
     */
    public List<PathEdge> getPath() {

        //fetch path list from 'task'
        List<PathEdge> path = curSearchTask.getPathAsPathEdges();
        //get closest node around current position
        int closest = getClosestNode(owner.getPosition());
        //add start and end node
        path.add(0,
                new PathEdge(owner.getPosition(), navGraph.getNode(closest).Pos())
        );

//        PathEdge lastEdge = path.get(path.size()-1);
//        if(!lastEdge.getDestination().equals(destinationPos)){//add end note if request is position
//            path.add(new PathEdge(path.get(path.size() - 1).getDestination(),
//                    this.destinationPos)
//            );
//        }

        //smooth path


        return path;
    }

    /**
     * This method makes use of the pre-calculated lookup table
     * @return the cost to travel from the bot's current position to a specific graph node.
     */
    public double getCostToNode(int NodeIdx) {
        //get closest node around current position
        //return Euclidean Distance + cost from pre-calculated lookup table
        return 0;
    }

    /**
     * This method makes use of the pre-calculated lookup table.
     * @return -1 if no active trigger found, otherwise the cost to the closest instance of the giver type
     */
    public double getCostToClosestItem(int GiverType) {
        //get closest node around current position, no return -1
        //iterate trigger list, if type matches and is active, get cost from pre-caculated table
        //if no trigger found, return -1
        //return cost
        return -1;
    }

    @Override
    public void render(GraphicsContext gc) {
        //draw edges
        for (PathEdge graphEdge : a2) {
            Point2D n1 = graphEdge.getSource();
            Point2D n2 = graphEdge.getDestination();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2.0);
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
        }
    }

    /**
     * called by time slice service manager
     * @return the status of the search process
     * 0 if target_found
     * 1 if not found
     * 2 if the search is not completed;
     */
    public int cycleOnce() {
        //exec current search
        //if found, notice agent by msg, also attach pointer to the target if there been a object
        //the agent will call getpath() after received the msg
        return 0;
    }
}

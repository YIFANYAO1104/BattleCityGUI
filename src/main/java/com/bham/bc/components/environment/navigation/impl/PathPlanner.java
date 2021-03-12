package com.bham.bc.components.environment.navigation.impl;

import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.components.environment.navigation.NavigationService;
import com.bham.bc.utils.graph.SparseGraph;
import javafx.geometry.Point2D;

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
//    private Graph_SearchTimeSliced<?> curSearchTask;
    /**
     * a note only, being set when an request was created
     * being used when getPath() is called
     */
    private Point2D destinationPos;

    public PathPlanner(Character owner, SparseGraph navGraph) {
        this.owner = owner;
        this.navGraph = navGraph;
    }

    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param itemType for example, triggers
     * @return true if there been a graph node around bot, otherwise false
     */
    public boolean createRequest(ItemType itemType) {
        //unregister current search
        //find closest node around bot, if no return false
        //create algorithm instance
        //register task in time slice service
        return false;
    }

    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param targetPos the targetPos that the bot wants to reach
     * @return true if closest nodes exist around both bot and targetPosition, otherwise false
     */
    public boolean createRequest(Point2D targetPos) {

        //unregister current search
        destinationPos = new Point2D(targetPos.getX(),targetPos.getY());
        //find closest node around bot, if no return false
        //find closest node around target, if no return false
        //create algorithm instance
        //register task in time slice service

        return false;
    }

    @Override
    public int peekRequestStatus() {
        return 0;
    }

    /**
     * called by an agent after it has been notified that a search has
     * terminated successfully.
     * @return a list of PathEdges
     */
    public List<PathEdge> getPath() {
        //fetch path list from 'task'
        //get closest node around current position
        //add start and end node
        //smooth path
        return null;
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

    /**
     * called by time slice service manager
     * @return the status of the search process
     * 0 if target_found
     * 1 if not found
     * 2 if the search is not completed;
     */
    public int cycleOnce() {
        //exec current search
        //if not found, return no path ava
        //if found, notice agent by msg, also attach pointer to the target if there been a object
        //the agent will call getpath() after received the msg

        return 0;
    }
}

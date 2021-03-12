package com.bham.bc.components.environment.navigation;

import com.bham.bc.components.environment.navigation.impl.PathEdge;
import javafx.geometry.Point2D;

import java.util.List;

public interface NavigationService {

    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * Dijkstras
     * @param itemType for example, triggers
     * @return true if there been a graph node around bot, otherwise false
     */
    public boolean createRequest(ItemType itemType);

    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * A*
     * @param targetPos the targetPos that the bot wants to reach
     * @return true if closest nodes exist around both bot and targetPosition, otherwise false
     */
    public boolean createRequest(Point2D targetPos);

    /**
     * called by an agent after a request was created
     * @return
     * 0 if target found
     * 1 if not found
     * 2 if the search is not completed;
     */
    public int peekRequestStatus();

    /**
     * called by an agent after it has been notified that a search has
     * terminated successfully.
     * @return a list of PathEdges
     */
    public List<PathEdge> getPath();

    //under construction--------------------------------------------
    //utility based AI----------------------------------------------

    /**
     * This method makes use of the pre-calculated lookup table
     * @return the cost to travel from the bot's current position to a specific graph node.
     */
    public double getCostToNode(int NodeIdx);

    /**
     * This method makes use of the pre-calculated lookup table.
     * @return -1 if no active trigger found, otherwise the cost to the closest instance of the giver type
     */
    public double getCostToClosestItem(int GiverType);
}

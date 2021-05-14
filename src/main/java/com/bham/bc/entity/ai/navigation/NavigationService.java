package com.bham.bc.entity.ai.navigation;

import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;

import java.util.LinkedList;
import java.util.List;

/**
 * The interface of path finding for agents
 */
public interface NavigationService {

    /**
     * Create a path finding request and a Dijkstra instance and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param itemType for example, triggers
     * @return true if there been a graph node around bot, otherwise false
     */
    boolean createRequest(ItemType itemType);

    /**
     * Create a path finding request and an AStar instance register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param targetPos the targetPos that the bot wants to reach
     * @return true if closest nodes exist around both bot and targetPosition, otherwise false
     */
    boolean createRequest(Point2D targetPos);

    /**
     * Create a path finding request and register it in the time slice service
     * @param entity the character an agent wants to reach
     * @return true if closest nodes exist around both bot and targetPosition, otherwise false
     */
    boolean createRequest(GameCharacter entity);

    /**
     * called by an agent after a request was created
     * @return {@link SearchStatus} to indicate searching process
     */
    SearchStatus peekRequestStatus();

    /**
     * Resets the search status to <i>no_task</i>. This is useful to not repeat the searches after target was found
     */
    void resetToNoTask();

    /**
     * called by an agent after it has been notified that a search has
     * terminated successfully.
     * @return a list of {@link PathEdge}
     */
    LinkedList<PathEdge> getPath();

    /**
     * set expand condition for algorithm.
     * It will also reset the one inside algorithm.
     * @param expandCondition see{@link ExpandPolicies}
     */
    void setExpandCondition(ExpandPolicies.ExpandCondition expandCondition);

    /**
     * check if the item is active or not
     * @return true if the item is active
     */
    boolean isTriggerActive();


    //for testing--------------------------------------------
    /**
     * For debug
     * Render the path stored in the path planner if the search task had completed
     * @param gc
     */
    void render(GraphicsContext gc);
}

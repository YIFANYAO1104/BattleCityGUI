package com.bham.bc.entity.ai.navigation.impl;

import com.bham.bc.components.characters.enemies.Tank;
import com.bham.bc.components.characters.enemies.Teaser;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.NavigationService;
import com.bham.bc.entity.ai.navigation.PathEdge;
import com.bham.bc.entity.ai.navigation.SearchStatus;
import com.bham.bc.entity.ai.navigation.algorithms.TimeSlicedAlgorithm;
import com.bham.bc.entity.ai.navigation.algorithms.TimeSlicedDijkstras;
import com.bham.bc.entity.ai.navigation.algorithms.astar.TimeSlicedAStar;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bc.entity.ai.navigation.algorithms.policies.TerminationPolices;
import com.bham.bc.entity.graph.SparseGraph;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.NavNode;
import javafx.geometry.Point2D;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static com.bham.bc.components.Controller.services;

/**
 * The implementation of {@link NavigationService}
 */
public class PathPlanner implements NavigationService {

    private int no_closest_node_found = -1;
    /**
     * A pointer to the owner of this class
     */
    private GameCharacter owner;
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
    private SearchStatus taskStatus;

    private ExpandPolicies.ExpandCondition expandCondition;

    // temp value to render the graphlines and getPath
    List<PathEdge> curPath = new ArrayList<PathEdge>();
    List<PathEdge> smoothedPath = new ArrayList<PathEdge>();

    private List<Shape> array = new ArrayList<>();

    public PathPlanner(GameCharacter owner, SparseGraph navGraph) {
        this.owner = owner;
        this.navGraph = navGraph;
        curSearchTask =null;
        taskStatus = SearchStatus.no_task;
        expandCondition = new ExpandPolicies.NoInvalid();
    }

    /**
     * @return node index. -1 if no closest node found
     */
    private int getClosestNode(GameCharacter entity){
        NavNode n1 = navGraph.getClosestNodeForEntity(entity);
        if (n1==null) {
            System.out.println("null node for entity");
            return no_closest_node_found;
        }
        if(n1.isValid()){
            return n1.Index();
        }
        return no_closest_node_found;
    }

    private int getClosestNode(Point2D location){
        NavNode n1 = navGraph.getClosestNodeByCenterPosition(location);
        if(n1.isValid()){
            return n1.Index();
        }
        return no_closest_node_found;
    }

    private void clear() {
        services.getDriver().unRegister(this);
        curSearchTask = null;
        taskStatus = SearchStatus.no_task;
        curPath.clear();
        smoothedPath.clear();
    }


    /**
     * Create a path finding request and register it in the time slice service
     * Note: This function is not a searching function, it only register a request
     * @param itemType for example, triggers
     * @return true if there been a graph node around bot, otherwise false
     */
    public boolean createRequest(ItemType itemType) {
        //unregister current search
        clear();
        //find closest node around bot, if no return false
        int closestNodeToPlayer = getClosestNode(owner);
        if (closestNodeToPlayer == no_closest_node_found){
            return false;
        }
        //create algorithm instance
        curSearchTask = new TimeSlicedDijkstras(navGraph,closestNodeToPlayer,itemType,
                new TerminationPolices.FindActiveTrigger(),
                expandCondition);
        taskStatus = SearchStatus.search_incomplete;
        //register task in time slice service
        services.getDriver().register(this);
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
        clear();
        //find closest node around bot, if no return false
        int closestNodeToPlayer = getClosestNode(owner);
        if (closestNodeToPlayer == no_closest_node_found){
            System.out.println("no_closest_node_found bot");
            return false;
        }
        //find closest node around target, if no return false
        int closestNodeToTarget = getClosestNode(targetPos);
        if (closestNodeToTarget == no_closest_node_found){
            System.out.println(targetPos);
            System.out.println("no_closest_node_found target");
            return false;
        }
        //create algorithm instance
        destinationPos = new Point2D(targetPos.getX(),targetPos.getY());
        curSearchTask = new TimeSlicedAStar(navGraph, closestNodeToPlayer, closestNodeToTarget,expandCondition);
        taskStatus = SearchStatus.search_incomplete;
        //register task in time slice service
        services.getDriver().register(this);
        return true;
    }

    public boolean createRequest(GameCharacter gc) {
        //unregister current search
        clear();
        //find closest node around bot, if no return false
        int closestNodeToPlayer = getClosestNode(owner);
        if (closestNodeToPlayer == no_closest_node_found){
            System.out.println("no_closest_node_found bot");
            return false;
        }
        //find closest node around target, if no return false
        int closestNodeToTarget = getClosestNode(gc);
        if (closestNodeToTarget == no_closest_node_found){
            System.out.println("no_closest_node_found target");
            return false;
        }
        //create algorithm instance
        destinationPos = gc.getCenterPosition();
        curSearchTask = new TimeSlicedAStar(navGraph, closestNodeToPlayer, closestNodeToTarget,expandCondition);
        taskStatus = SearchStatus.search_incomplete;
        //register task in time slice service
        services.getDriver().register(this);
        return true;
    }

    @Override
    public SearchStatus peekRequestStatus() {
        return taskStatus;
    }

    @Override
    public boolean isComplete(){
        return (taskStatus == SearchStatus.target_found) || (taskStatus == SearchStatus.target_not_found);
    }

    private Point2D getCenter(Point2D topLeft, Point2D radius){
        //add used deepcopy
        return topLeft.add(radius.multiply(0.5));
    }

    private void fetchPathFromAlgorithm(){
        //fetch path list from 'task'
        curPath = curSearchTask.getPathAsPathEdges();
        //get closest node around current position
        //matters only if the agent is moving away during the waiting
        int closest = getClosestNode(owner);
        //add start and end node

        curPath.add(0,
                new PathEdge(owner.getCenterPosition(), navGraph.getNode(closest).getPosition())
        );

        //        PathEdge lastEdge = path.get(path.size()-1);
        //        if(!lastEdge.getDestination().equals(destinationPos)){//add end note if request is position
        //            path.add(new PathEdge(path.get(path.size() - 1).getDestination(),
        //                    this.destinationPos)
        //            );
        //        }

        //smooth path
//        for (PathEdge pathEdge : curPath) {
//            smoothedPath.add(new PathEdge(pathEdge));
//        }
//        quickSmooth(smoothedPath);
    }

    /**
     * called by an agent after it has been notified that a search has
     * terminated successfully.
     * @return a list of PathEdges
     */
    public LinkedList<PathEdge> getPath() {
        LinkedList<PathEdge> tempList = new LinkedList<>();

        if (taskStatus==SearchStatus.target_found){
            if (curPath.isEmpty()) {
                fetchPathFromAlgorithm();
            }
            //deep copy
            for (PathEdge pathEdge : curPath) {
                tempList.add(new PathEdge(pathEdge));
            }
        }
        return tempList;
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
    //update
    @Override
    public void render(GraphicsContext gc) {
        //draw edges
        for (PathEdge graphEdge : curPath) {
            Point2D n1 = graphEdge.getSource();
            Point2D n2 = graphEdge.getDestination();
            switch (graphEdge.getBehavior()){
                case GraphEdge.normal:
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(2.0);break;
                case GraphEdge.shoot:
                    gc.setStroke(Color.GOLD);
                    gc.setLineWidth(10.0);break;
            }
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
        }

        for (PathEdge graphEdge : smoothedPath) {
            Point2D n1 = graphEdge.getSource();
            Point2D n2 = graphEdge.getDestination();
            switch (graphEdge.getBehavior()){
                case GraphEdge.normal:
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(2.0);break;
                case GraphEdge.shoot:
                    gc.setStroke(Color.GOLD);
                    gc.setLineWidth(10.0);break;
            }
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
        }
    }

    public List<Shape> getSmoothingBoxes(){
        return array;
    }

    /**
     * called by time slice service manager
     * @return the status of the search process
     * 0 if target_found
     * 1 if not found
     * 2 if the search is not completed;
     */
    public SearchStatus cycleOnce() {
        //carry on searching if not finished
//        if (owner instanceof Teaser){
//            System.out.println(curSearchTask);
//            System.out.println(taskStatus);
//        }
        if (taskStatus == SearchStatus.search_incomplete){
            taskStatus = curSearchTask.cycleOnce();
        }
        return taskStatus;
    }

    @Override
    public void resetTaskStatus() {
        if(taskStatus != SearchStatus.search_incomplete) {
            clear();
        }
    }

    public void setExpandCondition(ExpandPolicies.ExpandCondition expandCondition) {
        if (curSearchTask!=null) curSearchTask.setExpandCondition(expandCondition);
        this.expandCondition = expandCondition;
    }

    public boolean isTriggerActive(){
        if (taskStatus==SearchStatus.target_found){
            return curSearchTask.isTriggerActive();
        }
        return false;
    }
}

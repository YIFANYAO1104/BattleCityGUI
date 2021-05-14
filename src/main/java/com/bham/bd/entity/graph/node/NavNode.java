package com.bham.bd.entity.graph.node;

import com.bham.bd.entity.graph.ExtraInfo;
import javafx.geometry.Point2D;

/**
 * The node which would be used in navigation in graoh
 */

public class NavNode extends GraphNode{

    /**
     * Every GraohNode have their coordinates{@link Point2D}
     */
    protected Point2D position;

    protected ExtraInfo extraInfo;

    /**
     * The number{@link Integer} of obstacles interact with this Navnode
     */
    public int numWithObs = 0;

    /**
     * constructor
     * @param index the index of a node
     * @param pos the position of the node
     */
    public NavNode(int index, Point2D pos){
        super(index);
        position = pos;
    }


    /**
     * getter of numWithObs
     * @return the obstacle number on the node
     */
    public int getNumWithObs() {
        return numWithObs;
    }

    /**
     * add obstacle number on a node
     */
    public void addNum(){
        numWithObs++;
    }

    /**
     * subtract the number of obstacle of a node by 1
     */
    public void minesNum(){
        numWithObs--;
    }

    /**
     * When numWithObs <0 then it means that this has not interaction with obstacles.
     * @return Boolean if true then interacting with obstacles.
     */
    public boolean isHit(){
        if(numWithObs > 0) return true;
        return false;
    }

    /**
     * getter of the position
     * @return the position of a node
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * setter of position
     * @param newPosition the new position to be set
     */
    public void setPos(Point2D newPosition) {
        position = newPosition;
    }

    /**
     * getter of extraInfo
     * @return the extraInfo attached  on the node
     */
    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

    /**
     * setter of extraInfo
     * @param info the object to be attached to the node
     */
    public void setExtraInfo(ExtraInfo info) {
        extraInfo = info;
    }
}

package com.bham.bc.entity.graph.node;

import com.bham.bc.entity.graph.ExtraInfo;
import javafx.geometry.Point2D;

/**
 * The node which would be used in navigation in graoh
 */

public class NavNode extends GraphNode{

    /**
     * Every GraohNode have their coordinates{@link Point2D}
     */
    protected Point2D m_vPosition;

    protected ExtraInfo m_ExtraInfo;

    /**
     * The number{@link Integer} of obstacles interact with this Navnode
     */
    public int numWithObs = 0;

    public NavNode(int idx, Point2D pos){
        super(idx);
        m_vPosition = pos;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    public int getNumWithObs() {
        return numWithObs;
    }
    public void addNum(){
        numWithObs++;
    }
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

    public Point2D getPosition() {
        return m_vPosition;
    }

    public void setPos(Point2D newPosition) {
        m_vPosition = newPosition;
    }


    public ExtraInfo getExtraInfo() {
        return m_ExtraInfo;
    }

    public void setExtraInfo(ExtraInfo info) {
        m_ExtraInfo = info;
    }
}

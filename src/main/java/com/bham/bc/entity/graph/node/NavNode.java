package com.bham.bc.entity.graph.node;

import com.bham.bc.entity.graph.ExtraInfo;
import javafx.geometry.Point2D;

public class NavNode extends GraphNode{

    protected Point2D m_vPosition;
    protected ExtraInfo m_ExtraInfo;
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

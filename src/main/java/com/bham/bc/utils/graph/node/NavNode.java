package com.bham.bc.utils.graph.node;

import com.bham.bc.utils.graph.ExtraInfo;

public class NavNode extends GraphNode{

    protected Vector2D m_vPosition;
    protected ExtraInfo m_ExtraInfo;
    protected int numWithObs = 0;

    public NavNode(int idx, Vector2D pos){
        super(idx);
        m_vPosition = new Vector2D(pos);
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

    public Vector2D Pos() {
        return new Vector2D(m_vPosition);
    }

    public void SetPos(Vector2D NewPosition) {
        m_vPosition = new Vector2D(NewPosition);
    }


    public ExtraInfo getExtraInfo() {
        return m_ExtraInfo;
    }

    public void setExtraInfo(ExtraInfo info) {
        m_ExtraInfo = info;
    }
}

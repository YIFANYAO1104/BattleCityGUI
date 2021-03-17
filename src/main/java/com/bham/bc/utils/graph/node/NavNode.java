package com.bham.bc.utils.graph.node;

import com.bham.bc.utils.graph.ExtraInfo;

public class NavNode<extra_info extends ExtraInfo> extends GraphNode{

    protected Vector2D m_vPosition;
    protected extra_info m_ExtraInfo;

    public NavNode(int idx, Vector2D pos){
        super(idx);
        m_vPosition = new Vector2D(pos);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }



    public Vector2D Pos() {
        return new Vector2D(m_vPosition);
    }

    public void SetPos(Vector2D NewPosition) {
        m_vPosition = new Vector2D(NewPosition);
    }


    public extra_info getExtraInfo() {
        return m_ExtraInfo;
    }

    public void setExtraInfo(extra_info info) {
        m_ExtraInfo = info;
    }
}

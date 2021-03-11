package com.bham.bc.utils.graph.edge;

public class NavEdge extends GraphEdge{

    //if this edge intersects with an object (such as a door or lift), then
    //this is that object's ID.
    protected int m_iIDofIntersectingEntity;

    public NavEdge(int from,
                        int to,
                        double cost,
                        int id) {
        super(from, to, cost);
        m_iIDofIntersectingEntity = id;
    }

    public NavEdge(int from,
                        int to,
                        double cost) {
        this(from, to, cost, -1);
    }

    public NavEdge(NavEdge e) {
        this(e.From(), e.To(), e.Cost(), -1);
    }

    public int IDofIntersectingEntity() {
        return m_iIDofIntersectingEntity;
    }

    public void SetIDofIntersectingEntity(int id) {
        m_iIDofIntersectingEntity = id;
    }

}

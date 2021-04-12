package com.bham.bc.entity.graph.edge;

public class NavEdge extends GraphEdge{


    final public static int normal = 0;
    final public static int shoot = 1;
    //protected int behavior;

    //if this edge intersects with an object (such as a door or lift), then
    //this is that object's ID.
    protected int m_iIDofIntersectingEntity;

    public NavEdge(int from,
                        int to,
                        double cost,
                        int behavior) {
        super(from, to, cost);
        this.behavior = behavior;
    }

    public NavEdge(int from,
                        int to,
                        double cost) {
        this(from, to, cost, normal);
    }

    public NavEdge(NavEdge e) {
        this(e.From(), e.To(), e.Cost(), normal);
    }

    public int IDofIntersectingEntity() {
        return m_iIDofIntersectingEntity;
    }

    public void SetIDofIntersectingEntity(int id) {
        m_iIDofIntersectingEntity = id;
    }

}

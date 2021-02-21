package com.bham.bc.utils.graph.node;

import static com.bham.bc.utils.graph.NodeTypeEnum.invalid_node_index;

public class GraphNode {
    //every node has an index. a Valid index >= 0

    protected  int m_index;

    public GraphNode(){m_index = invalid_node_index;} // invalid index <0

    public GraphNode(int ind){m_index = ind;}

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public int Index() {
        return m_index;
    }

    public void SetIndex(int NewIndex) {
        m_index = NewIndex;
    }
}

package com.bham.bc.entity.graph.node;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.bham.bc.entity.graph.NodeTypeEnum.invalid_node_index;

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

    public void setIndex(int NewIndex) {
        m_index = NewIndex;
    }

    public boolean isValid(){
        return m_index>0;
    }
    public void setInvalid(){
        this.m_index = -1;
    }

    //for reading and writing to streams.
    public OutputStream print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.print("Index: ");
        ps.print(m_index);
        ps.println();
        return os;
    }
}

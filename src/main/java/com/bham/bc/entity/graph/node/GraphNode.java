package com.bham.bc.entity.graph.node;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.bham.bc.entity.graph.NodeTypeEnum.invalid_node_index;

/**
 * The base class of graph node
 */
public class GraphNode {

    /**
     * every node has an index{@link Integer}. a Valid index >= 0
     */
    protected int index;

    /**
     * constructor
     * set index as invalid_node_index by default
     */
    public GraphNode(){
        index = invalid_node_index;
    } // invalid index <0

    /**
     * constructor
     */
    public GraphNode(int index){
        this.index = index;
    }

    /**
     * getter of index
     * @return the index number
     */
    public int getIndex() {
        return index;
    }

    /**
     * setter of index
     */
    public void setIndex(int NewIndex) {
        index = NewIndex;
    }

    /**
     * check whether the index is a valid index
     */
    public boolean isValid(){
        return index >0;
    }

    /**
     * set a node to invalid node
     */
    public void setInvalid(){
        this.index = -1;
    }
}

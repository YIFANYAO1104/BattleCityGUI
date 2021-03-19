package com.bham.bc.utils.graph;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.graph.edge.GraphEdge;
import com.bham.bc.utils.graph.node.GraphNode;
import com.bham.bc.utils.graph.node.NavNode;
import com.bham.bc.utils.graph.node.Vector2D;
import com.bham.bc.utils.messaging.Telegram;
import com.sun.javafx.geom.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.utils.graph.NodeTypeEnum.invalid_node_index;

import java.io.*;
import java.util.*;

public class SparseGraph<node_type extends NavNode, edge_type extends GraphEdge> extends BaseGameEntity {

    private GraphEdge nh1;
    private int rowNums;
    private int eachDisX;
    private int eachDisY;
    private int columnNums;

    //enable easy client access to the edge and node types used in the graph
    //typedef edge_type                EdgeType;
    //typedef node_type                NodeType;
    //a couple more typedefs to save my fingers and to help with the formatting
    //of the code on the printed page
    public class NodeVector extends ArrayList<GraphNode> {

        public NodeVector() {
            super();
        }
    };

    public class EdgeList<edge_type> extends LinkedList<GraphEdge> {

        public EdgeList() {
            super();
        }
    };

    public class EdgeListVector extends ArrayList<EdgeList> {

        public EdgeListVector() {
            super();
        }
    };
    //the nodes that comprise this graph
    private NodeVector nodeVector = new NodeVector();
    //a vector of adjacency edge lists. (each node index keys into the
    //list of edges associated with that node)
    private EdgeListVector edgeListVector = new EdgeListVector();
    //is this a directed graph?
    private boolean isDirectedGraph;
    //the index of the next node to be added
    private int nextNodeIndex;

    /**
     * @return true if the edge is not present in the graph. Used when adding
     * edges to prevent duplication
     */
    private boolean isEdgeNew(int from, int to) {
        ListIterator<edge_type> curEdge = edgeListVector.get(from).listIterator();
        while (curEdge.hasNext()) {
            if (curEdge.next().To() == to) {
                return false;
            }
        }

        return true;
    }

    /**
     * iterates through all the edges in the graph and removes any that point to
     * an invalidated node
     */
//    private void CullInvalidEdges()

    //ctor
    public SparseGraph(boolean digraph) {
        super(GetNextValidID(),-1,-1);
        nextNodeIndex = 0;
        isDirectedGraph = digraph;
    }

    public void SparseGraph(int rowNums, int columnNums, int eachDisX, int eachDisY) {
        this.rowNums = rowNums;
        this.columnNums = columnNums;
        this.eachDisX =eachDisX;
        this.eachDisY = eachDisY;
    }

//    public void display(){
////        System.out.println("size: "+ this.nodeVector.size());
////        System.out.println("size: "+ this.m_Edges.size());
//        for(int i = 0; i < nodeVector.size();i++){
//            NavNode n1 = (NavNode)this.nodeVector.get(i);
//
////            System.out.println(n1.Pos().toString());
//        }


//    }



    @Override
    public void render(GraphicsContext gc){

        gc.setFill(Color.BLACK);
        for(int i = 0; i < nodeVector.size() ; i++){
            NavNode n1 = getNode(i);
            if(n1.isValid()){
                gc.fillRoundRect(n1.Pos().getX(),n1.Pos().getY(),2,2,1,1);
//                System.out.println(n1.Pos().toString());
            }else {
//                System.out.println("------------------------------is----------invallid----------------------------");
            }

        }


        // draw edges
//        for (int i = 0; i<m_Edges.size();i++){
//            SparseGraph.EdgeList g1 = this.m_Edges.get(i);
//            for (int j = 0; j < g1.size();j++){
//                GraphEdge nh1  = (GraphEdge)g1.get(j);
//                NavNode n1 = (NavNode)this.nodeVector.get(nh1.From());
//                NavNode n2 = (NavNode)this.nodeVector.get(nh1.To());
////                Line line1 = new Line(n1.Pos().getX(), n1.Pos().getY(), n2.Pos().getX(), n2.Pos().getY());
//
//                gc.setStroke(Color.BLACK);
//                gc.setLineWidth(1.0);
//                gc.strokeLine(n1.Pos().getX(), n1.Pos().getY(), n2.Pos().getX(), n2.Pos().getY());
//
//
//            }
//        }

    }

    public void renderlines(GraphicsContext gc, ArrayList<GraphNode> a1){
        ArrayList<GraphEdge> ee1 = new ArrayList<>();
        for(int i =0;i<a1.size()-1;i++){
            ee1.add(getEdge(a1.get(i).Index(), a1.get(i+1).Index()));
        }
         //draw edges
        for (int j = 0; j < ee1.size();j++){
            GraphEdge nh1  = (GraphEdge)ee1.get(j);
            NavNode n1 = (NavNode)this.nodeVector.get(nh1.From());
            NavNode n2 = (NavNode)this.nodeVector.get(nh1.To());
//                Line line1 = new Line(n1.Pos().getX(), n1.Pos().getY(), n2.Pos().getX(), n2.Pos().getY());

            gc.setStroke(Color.RED);
            gc.setLineWidth(2.0);
            gc.strokeLine(n1.Pos().getX(), n1.Pos().getY(), n2.Pos().getX(), n2.Pos().getY());


        }

    }
    
    public int renderTankPoints(Vector2D location , GraphicsContext gc){
        gc.setFill(Color.RED);


        NavNode n1 = getClosestNodeForPlayer(location);
//        System.out.println("1 size"+n1.Pos().toString());
        if(n1.isValid() ){
            gc.fillRoundRect(n1.Pos().getX(),n1.Pos().getY(),8,8,1,1);
        }
        gc.setFill(Color.BLUE);
        gc.fillRoundRect(location.getX(),location.getY(),4,4,1,1);

        return n1.Index();


    }

    public NavNode getClosestNodeForPlayer(Vector2D location){
        int i = (int) (location.getX() + 16.0) /eachDisY;
        int j = (int) (location.getY() + 16.0) / eachDisX;
        int c = j*rowNums + i;
        NavNode n1 = (NavNode)this.nodeVector.get(c);

        return getNode(c);
//        System.out.println("1 size"+n1.Pos().toString());

    }

    public LinkedList<NavNode> getNodeList(int n1){
        LinkedList<NavNode> nodes = new LinkedList<>();
        LinkedList<GraphEdge> edges = edgeListVector.get(n1);
        for (GraphEdge e1: edges){
            NavNode nn1 = getNode(e1.To());
            if(nn1.isValid())
                nodes.add(nn1);
        }

        if(nodes.isEmpty())
            return null;
        return nodes;
    }

    /**
     * method for obtaining a reference to a specific node
     *
     * @return the node at the given index
     */
    public node_type getNode(int idx) {
        assert (idx < (int) nodeVector.size())
                && (idx >= 0) :
                "<SparseGraph::GetNode>: invalid index";

        return (node_type) nodeVector.get(idx);
    }


    /**
     * const and non const methods for obtaining a reference to a specific edge
     */
    public edge_type getEdge(int from, int to) {
        assert (from < nodeVector.size())
                && (from >= 0)
                && nodeVector.get(from).Index() != invalid_node_index :
                "<SparseGraph::GetEdge>: invalid 'from' index";

        assert (to < nodeVector.size())
                && (to >= 0)
                && nodeVector.get(to).Index() != invalid_node_index :
                "<SparseGraph::GetEdge>: invalid 'to' index";

        ListIterator<edge_type> it = edgeListVector.get(from).listIterator();
        while (it.hasNext()) {
            edge_type curEdge = it.next();
            if (curEdge.To() == to) {
                return curEdge;
            }
        }

        assert false : "<SparseGraph::GetEdge>: edge does not exist";
        return null;
    }

    /**
     * const and non const methods for obtaining a reference to a specific edge
     */
//    public edge_type GetEdge(int from, int to)

    //retrieves the next free node index
    public int getNextFreeNodeIndex() {
        return nextNodeIndex;
    }

    /**
     * Given a node this method first checks to see if the node has been added
     * previously but is now innactive. If it is, it is reactivated.
     *
     * If the node has not been added previously, it is checked to make sure its
     * index matches the next node index before being added to the graph
     */
    public int addNode(node_type node) {
        if (node.Index() < (int) nodeVector.size()) {
            //make sure the client is not trying to add a node with the same ID as
            //a currently active node
            assert nodeVector.get(node.Index()).Index() == invalid_node_index :
                    "<SparseGraph::AddNode>: Attempting to add a node with a duplicate ID";

            nodeVector.set(node.Index(), node);

            return nextNodeIndex;
        } else {
            //make sure the new node has been indexed correctly
            assert node.Index() == nextNodeIndex : "<SparseGraph::AddNode>:invalid index";

            nodeVector.add(node);
            edgeListVector.add(new EdgeList());

            return nextNodeIndex++;
        }
    }

    /**
     * Removes a node from the graph and removes any links to neighbouring nodes
     */
//    public void RemoveNode(int node)


    /**
     * Use this to add an edge to the graph. The method will ensure that the
     * edge passed as a parameter is valid before adding it to the graph. If the
     * graph is a digraph then a similar edge connecting the nodes in the
     * opposite direction will be automatically added.
     */
    public void addEdge(edge_type edge) {
        //first make sure the from and to nodes exist within the graph
        assert (edge.From() < nextNodeIndex) && (edge.To() < nextNodeIndex) :
                "<SparseGraph::AddEdge>: invalid node index";

        //make sure both nodes are active before adding the edge
        if ((nodeVector.get(edge.To()).Index() != invalid_node_index)
                && (nodeVector.get(edge.From()).Index() != invalid_node_index)) {
            //add the edge, first making sure it is unique
            if (isEdgeNew(edge.From(), edge.To())) {
                edgeListVector.get(edge.From()).add(edge);
            }

            //if the graph is undirected we must add another connection in the opposite
            //direction
            if (!isDirectedGraph) {
                //check to make sure the edge is unique before adding
                if (isEdgeNew(edge.To(), edge.From())) {
//                    edge_type NewEdge = null;
                    GraphEdge NewEdge = null;
                    try {
//                        NewEdge = (edge_type) edge.getClass().getConstructor(edge.getClass()).newInstance(edge);
                        NewEdge = new GraphEdge();
                    } catch (Exception ex) {

                        throw new RuntimeException(ex);
                    }

                    NewEdge.SetTo(edge.From());
                    NewEdge.SetFrom(edge.To());
                    NewEdge.SetCost(edge.Cost());

                    edgeListVector.get(edge.To()).add(NewEdge);
                }
            }
        }
    }


    /**
     * removes the edge connecting from and to from the graph (if present). If a
     * digraph then the edge connecting the nodes in the opposite direction will
     * also be removed.
     */
//    public void RemoveEdge(int from, int to)

    /**
     * Sets the cost of a specific edge
     */
    public void setEdgeCost(int from, int to, double newCost) {
        //make sure the nodes given are valid
        assert (from < nodeVector.size()) && (to < nodeVector.size()) :
                "<SparseGraph::SetEdgeCost>: invalid index";

        //visit each neighbour and erase any edges leading to this node
        ListIterator<edge_type> it = edgeListVector.get(from).listIterator();
        while (it.hasNext()) {
            edge_type curEdge = it.next();
            if (curEdge.To() == to) {
                curEdge.SetCost(newCost);
                break;
            }
        }
    }

    /**
     * returns the number of active + inactive nodes present in the graph
     */
    public int numNodes() {
        return nodeVector.size();
    }

    /**
     * returns the number of active nodes present in the graph (this method's
     * performance can be improved greatly by caching the value)
     */
    public int NumActiveNodes() {
        int count = 0;

        for (int n = 0; n < nodeVector.size(); ++n) {
            if (nodeVector.get(n).Index() != invalid_node_index) {
                ++count;
            }
        }

        return count;
    }

    /**
     * returns the total number of edges present in the graph
     */
    public int numEdges() {
        int tot = 0;

        ListIterator<EdgeList> curEdge = edgeListVector.listIterator();
        while (curEdge.hasNext()) {
            tot += curEdge.next().size();
        }

        return tot;
    }

    /**
     * @return true if the graph is directed
     */
    public boolean isDigraph() {
        return isDirectedGraph;
    }

    /**
     * @return true if the graph contains no nodes
     */
    public boolean isEmpty() {
        return nodeVector.isEmpty();
    }

    /**
     * returns true if a node with the given index is present in the graph
     */
    public boolean isNodePresent(int nd) {
        if ((nd >= (int) nodeVector.size() || (nodeVector.get(nd).Index() == invalid_node_index))) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * @return true if an edge with the given from/to is present in the graph
     */
    public boolean isEdgePresent(int from, int to) {
        if (isNodePresent(from) && isNodePresent(from)) {
            ListIterator<edge_type> curEdge = edgeListVector.get(from).listIterator();
            while (curEdge.hasNext()) {
                if (curEdge.next().To() == to) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }
    

    /**
     * Get nodes which is connected with this node
     * @param n1
     * @return the Integers of those nodes</>
     */

    public ArrayList<GraphNode> getAroundNodes(GraphNode n1){
        ArrayList<GraphNode> temp1 = new ArrayList<GraphNode>();
        if(!n1.isValid())
            return temp1;
        EdgeList<GraphEdge> m1 = edgeListVector.get(n1.Index());


        for(GraphEdge e1:m1){
            temp1.add(nodeVector.get(e1.To()));
        }
        return temp1;
    }

    public void fitting(HashSet<GraphNode> m1){
        for(GraphNode g1: nodeVector){
            if(!m1.contains(g1)){
                g1.setInvalid();
            }
        }
    }

    /**
     * non const class used to iterate through all the edges connected to a
     * specific node.
     */
    public static class EdgeIterator<node_type extends NavNode, edge_type extends GraphEdge> {

        private ListIterator<edge_type> curEdge;
        private SparseGraph<node_type, edge_type> G;
        private final int NodeIndex;
        private boolean end = false;

        public EdgeIterator(SparseGraph<node_type, edge_type> graph,
                            int node) {
            G = graph;
            NodeIndex = node;
            /* we don't need to check for an invalid node index since if the node is
             invalid there will be no associated edges
             */
            curEdge = G.edgeListVector.get(NodeIndex).listIterator();
        }

        public edge_type begin() {
            curEdge = G.edgeListVector.get(NodeIndex).listIterator();
            if (curEdge.hasNext()) {
                end = false;
                return curEdge.next();
            }
            end = true;
            return null;
        }

        public edge_type next() {
            if (!curEdge.hasNext()) {
                end = true;
                return null;
            }
            return curEdge.next();
        }

        //return true if we are at the end of the edge list
        public boolean end() {
            return end;
        }
    }


    public ArrayList<Vector2D> getAllVector(){
        ArrayList<Vector2D> i1 = new ArrayList<Vector2D>();

        for(Object n1 : this.nodeVector){
            NavNode nn1 = (NavNode)n1;
            i1.add(nn1.Pos());
        }

        return i1;

    }

    @Override
    public void update() {

    }

    @Override
    public Rectangle getHitBox() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.Msg){
            case Msg_interact :
                //System.out.println("Find invalid nodes, dealing");
                nodeVector.get((int)msg.ExtraInfo).setInvalid();
                return true;
            default:
                return false;
        }

    }

    @Override
    public String toString() {
        return "Sparse Graph type";
    }

}

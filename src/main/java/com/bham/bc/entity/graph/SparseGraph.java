package com.bham.bc.entity.graph;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.GraphNode;
import com.bham.bc.entity.graph.node.NavNode;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.entity.graph.NodeTypeEnum.invalid_node_index;

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
    // Map the obstacle's ID to the index of nodes interacting with
    private HashMap<Integer, ArrayList<NavNode>> obstacleId = new HashMap<>();
    //Map the closet node for that entities
    private  HashMap<BaseGameEntity, NavNode> trcikingTable = new HashMap<>();

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

    /**
     * rener the graph nodes on the map
     * @param gc
     */
    @Override
    public void render(GraphicsContext gc){
        for(GraphNode node : nodeVector){
            NavNode n1 = (NavNode) node;
            if(n1.isValid()){
                gc.fillRoundRect(n1.getPosition().getX(),n1.getPosition().getY(),2,2,1,1);
                renderNode(gc,Color.BLACK,n1,1);
                for(GraphNode nn1: getAroundNodes(n1)){
//                    if(nn1.isValid()&& getEdge(n1.Index(),nn1.Index()).Cost() >= Constants.GRAPH_GRAPH_OBSTACLE_EDGE_COST){
                    if(nn1.isValid()&& getEdge(n1.Index(),nn1.Index()).getBehavior() == GraphEdge.shoot){
                        renderNode(gc,Color.BLUE,(NavNode) nn1,2);
                        renderline(gc,Color.BLUE,n1,(NavNode) nn1);
                    }
                }
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
    private void renderNode(GraphicsContext gc,Color color, NavNode n1, int level){
        gc.setFill(color);
        gc.fillRoundRect(
                n1.getPosition().getX(),n1.getPosition().getY(),2*level,2*level,1*level,1*level);
    }


    private void renderline(GraphicsContext gc,Color color, NavNode n1, NavNode n2){
        gc.setStroke(color);
        gc.setLineWidth(1.0);
        gc.strokeLine(
                n1.getPosition().getX(), n1.getPosition().getY(), n2.getPosition().getX(), n2.getPosition().getY());
    }
    public void renderTankPoints(ArrayList<BaseGameEntity> entities, GraphicsContext gc){
        for(BaseGameEntity e1: entities) renderTankPoint(e1,gc);
    }

    public int renderTankPoint(BaseGameEntity e1 , GraphicsContext gc){
        NavNode n1 = getClosestNodeForEntity(e1);
        if(n1.isValid() ){
            gc.fillRoundRect(n1.getPosition().getX(),n1.getPosition().getY(),8,8,1,1);
            renderNode(gc,Color.RED,n1,4);
        }
        Point2D location = e1.getPosition();
        gc.setFill(Color.BLUE);
        gc.fillRoundRect(location.getX(),location.getY(),4,4,1,1);

        return n1.Index();
    }

    public NavNode getClosestNodeForEntity(BaseGameEntity entity){
        Point2D location = entity.getPosition();
        Point2D radius = entity.getRadius();
        int i = (int) (location.getX() + radius.getX()/2) /eachDisY;   // 16.0 means the value of tanks 1/2 width and height
        int j = (int) (location.getY() + radius.getY()/2) / eachDisX;
        int c = j*rowNums + i;
        if(c<0 || c>= nodeVector.size()) c=0;
        NavNode n1 = (NavNode)this.nodeVector.get(c);
        if(n1.isValid()){
            trcikingTable.put(entity,n1);
            return n1;
        }else {
            try {
                for(NavNode nn1:getNodeList(c)){
                    if(nn1.isValid()){
                        trcikingTable.put(entity,nn1);
                        return nn1;
                    }
                }
            }catch (Exception e){

            }

            return trcikingTable.get(entity);
        }
    }

    public NavNode getClosestNodeByPosition(Point2D location,Point2D radius){
        int i = (int) (location.getX() + radius.getX()/2) /eachDisY;   // 16.0 means the value of tanks 1/2 width and height
        int j = (int) (location.getY() + radius.getY()/2) / eachDisX;
        int c = j*rowNums + i;
        NavNode n1 = (NavNode)this.nodeVector.get(c);
        return n1;
    }

    public LinkedList<NavNode> getNodeList(int n1){
//        if(getNode(n1).isValid()) return null;
        LinkedList<NavNode> nodes = new LinkedList<>();
        LinkedList<GraphEdge> edges = edgeListVector.get(n1);
        for (GraphEdge e1: edges){
            NavNode nn1 = getNode(e1.To());
            if(nn1.isValid()) nodes.add(nn1);
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



    public void setNodeALlEdagesNormal(int from) {
        for (NavNode n1 : getNodeList(from)) {
            double dis = n1.getPosition().distance(getNode(from).getPosition());
            setEdgeCost(from, n1.Index(), dis);
            setEdgeCost(n1.Index(), from, dis);
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
        private ExpandPolicies.ExpandCondition expandCondition;

        public EdgeIterator(SparseGraph<node_type, edge_type> graph,
                            int node, ExpandPolicies.ExpandCondition expandCondition) {
            G = graph;
            NodeIndex = node;
            /* we don't need to check for an invalid node index since if the node is
             invalid there will be no associated edges
             */
            curEdge = G.edgeListVector.get(NodeIndex).listIterator();
            this.expandCondition = expandCondition;
        }

        public boolean hasNext() {
            return end;
        }

        public GraphEdge next() {
            while (curEdge.hasNext()) {
                GraphEdge e = curEdge.next();
                if(expandCondition.isSatisfied(G,e)){
                    end = false;
                    return e;
                }
            }
            end = true;
            return null;
        }
    }


    public ArrayList<Point2D> getAllVector(){
        ArrayList<Point2D> i1 = new ArrayList<Point2D>();

        for(Object n1 : this.nodeVector){
            NavNode nn1 = (NavNode)n1;
            i1.add(nn1.getPosition());
        }

        return i1;

    }
    private void addToHashMap(int id, NavNode node){
        if(!node.isValid()) return;
        if(obstacleId.containsKey(id)){
            ArrayList<NavNode> temp1 = obstacleId.get(id);
            if(temp1.contains(node))
                System.out.println("this node has been registered in graph HashMap");
            else{
                node.addNum();
                temp1.add(node);
            }


        }else{
            ArrayList<NavNode> temp2 = new ArrayList<>();
            temp2.add(node);
            node.addNum();
            obstacleId.put(id,temp2);
        }
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
//                System.out.println("Find invalid nodes, dealing");
                getNode((int)msg.ExtraInfo).setInvalid();
                return true;
            case Msg_interactWithSoft:
//                System.out.println("Set the nodes edges with max");
                //Object[] params = {indexOfNode,this};
                int indexOfNode = (int)msg.ExtraInfo;
                NavNode nn1 = getNode(indexOfNode);
                addToHashMap(msg.Sender,nn1);
                setNodeALLEdages(indexOfNode, Constants.GRAPH_GRAPH_OBSTACLE_EDGE_COST);
                setEdgesBehavior(indexOfNode, GraphEdge.shoot);
                return true;
            case Msg_removeSoft:
//                System.out.println("release the nodewa");
                removeObstacleInHashMap(msg.Sender);
                return true;

            default:
                return false;
        }
    }

    /**
     * set nodes around the (from) node edges all with new Cost
     * @param from root node
     * @param newCost
     */
    public void setNodeALLEdages(int from, double newCost){
        for(NavNode n1 :getNodeList(from)){
            setEdgeCost(from,n1.Index(),newCost);
            setEdgeCost(n1.Index(),from,newCost);
        }
    }

    public void setEdgesBehavior(int from, int behavior){
        ListIterator<edge_type> it = edgeListVector.get(from).listIterator();
        while (it.hasNext()) {
            edge_type curEdge = it.next();
            curEdge.setBehavior(behavior);
        }
    }

    private void removeObstacleInHashMap(int id){
        if(obstacleId.containsKey(id)){
            for(NavNode node: obstacleId.get(id)){
                node.minesNum();
                if(node.isValid() && (!node.isHit()) ){
                    setNodeALlEdagesNormal(node.Index());
                    setEdgesBehavior(node.Index(), GraphEdge.normal);
                }
            }
            obstacleId.remove(id);
        }else {
            System.out.println("no this key????");
        }
    }

    @Override
    public String toString() {
        return "Sparse Graph type";
    }

}

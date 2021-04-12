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
            if (curEdge.next().getTo() == to) {
                return false;
            }
        }

        return true;
    }

    /**
     * Initial the SparseGraph
     * @param digraph Boolean, if set this Graph DirectedGraph then true.
     */
    public SparseGraph(boolean digraph) {
        super(GetNextValidID(),-1,-1);
        nextNodeIndex = 0;
        isDirectedGraph = digraph;
    }

    /**
     * Initial the SparseGraph
     * @param rowNums               The number of points on the rows
     * @param columnNums            The number of points on the columnNums
     * @param eachDisX              The distance between each points on row
     * @param eachDisY              The distance between each points on column
     */
    public void SparseGraph(int rowNums, int columnNums, int eachDisX, int eachDisY) {
        this.rowNums = rowNums;
        this.columnNums = columnNums;
        this.eachDisX =eachDisX;
        this.eachDisY = eachDisY;
    }

    /**
     * rener the graph nodes on the map
     * @param gc GraphicsContext
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

    /**
     * Render the given entities with red points on the map
     * @param entities List of the BaseGameEntity
     * @param gc GraphicsContext
     */
    public void renderTankPoints(List<BaseGameEntity> entities, GraphicsContext gc){
        for(BaseGameEntity e1: entities) renderTankPoint(e1,gc);
    }

    /**
     * Render red point on map with its entity
     * @param e1 BaseGameEntity
     * @param gc GraphicsContext
     * @return indedx of the node
     */
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

    /**
     *
     * @param entity BaseGameEntity
     * @return Navnode give the closet node to it's coordinates
     */
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

    /**
     * Get coorespondant coordinates for that Point2D location.
     * In order to make sure get close with the center of that entity, it needs radius of entity
     * @param location coordinates
     * @param radius Point2D
     * @return NavNode
     */
    public NavNode getClosestNodeByPosition(Point2D location,Point2D radius){
        int i = (int) (location.getX() + radius.getX()/2) /eachDisY;   // 16.0 means the value of tanks 1/2 width and height
        int j = (int) (location.getY() + radius.getY()/2) / eachDisX;
        int c = j*rowNums + i;
        NavNode n1 = (NavNode)this.nodeVector.get(c);
        return n1;
    }

    public LinkedList<NavNode> getNodeList(int n1){
        LinkedList<NavNode> nodes = new LinkedList<>();
        LinkedList<GraphEdge> edges = edgeListVector.get(n1);
        for (GraphEdge e1: edges){
            NavNode nn1 = getNode(e1.getTo());
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
            if (curEdge.getTo() == to) {
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
        assert (edge.getFrom() < nextNodeIndex) && (edge.getTo() < nextNodeIndex) :
                "<SparseGraph::AddEdge>: invalid node index";

        //make sure both nodes are active before adding the edge
        if ((nodeVector.get(edge.getTo()).Index() != invalid_node_index)
                && (nodeVector.get(edge.getFrom()).Index() != invalid_node_index)) {
            //add the edge, first making sure it is unique
            if (isEdgeNew(edge.getFrom(), edge.getTo())) {
                edgeListVector.get(edge.getFrom()).add(edge);
            }

            //if the graph is undirected we must add another connection in the opposite
            //direction
            if (!isDirectedGraph) {
                //check to make sure the edge is unique before adding
                if (isEdgeNew(edge.getTo(), edge.getFrom())) {
                    GraphEdge reversedEdge = new GraphEdge(edge.getTo(),edge.getFrom(),edge.getCost());
                    edgeListVector.get(edge.getTo()).add(reversedEdge);
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
            if (curEdge.getTo() == to) {
                curEdge.setCost(newCost);
                break;
            }
        }
    }

    /**
     * Set all edges related this node with normal cost
     * @param from index of the node
     */
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
                if (curEdge.next().getTo() == to) {
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
            temp1.add(nodeVector.get(e1.getTo()));
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
        private boolean hasNext;
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
            hasNext = !curEdge.hasNext();
        }

        public boolean hasNext() {
            int retrieve = 0;
            //find the one satisfies condition
            while (curEdge.hasNext()){
                retrieve++;
                GraphEdge e = curEdge.next();
                if(expandCondition.isSatisfied(G,e)){
                    curEdge.previous();
                    return true;
                }
            }
            //no then return false
            while (retrieve>0){
                curEdge.previous();
                retrieve--;
            }
            return false;
        }

        public GraphEdge next() {
            return curEdge.next();
        }
    }


    /**
     * @return ArrayList of Point2D storeds the coordinates
     */
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

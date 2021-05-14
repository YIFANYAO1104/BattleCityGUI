package com.bham.bc.entity.graph;

import com.bham.bc.entity.graph.algorithm.Floodfill;
import com.bham.bc.entity.graph.edge.GraphEdge;
import com.bham.bc.entity.graph.node.GraphNode;
import com.bham.bc.entity.graph.node.NavNode;

import javafx.geometry.Point2D;


public class HandyGraphFunctions {



    /**
     * @return true if x,y is a valid position in the map
     */
    public static boolean ValidNeighbour(int x, int y, int NumCellsX, int NumCellsY) {
        return !((x < 0) || (x >= NumCellsX) || (y < 0) || (y >= NumCellsY));
    }

    /**
     *  use to add the eight neighboring edges of a graph node that
     *  is positioned in a grid layout
     */
    public static <graph_type extends SparseGraph> void GraphHelper_AddAllNeighboursToGridNode(graph_type graph,
                                                                                               int row,
                                                                                               int col,
                                                                                               int NumCellsX,
                                                                                               int NumCellsY) {

        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                int nodeX = col + j;
                int nodeY = row + i;

                if ((i == 0) && (j == 0)) {         // It is 8 direction
                    continue;
                }

                //check to see if this is a valid neighbour
                if (ValidNeighbour(nodeX, nodeY, NumCellsX, NumCellsY)) {
                    //calculate the distance to this node
                    Point2D PosNode = graph.getNode(row * NumCellsX + col).getPosition();
                    Point2D PosNeighbour = graph.getNode(nodeY * NumCellsX + nodeX).getPosition();

                    double dist = PosNode.distance(PosNeighbour);

                    //this neighbour is okay so it can be added
                    GraphEdge NewEdge = new GraphEdge(row * NumCellsX + col,
                            nodeY * NumCellsX + nodeX,
                            dist);
                    graph.addEdge(NewEdge);

                    //if graph is not a diagraph then an edge needs to be added going
                    //in the other direction
                    if (!graph.isDigraph()) {
                        NewEdge = new GraphEdge(nodeY * NumCellsX + nodeX,
                                row * NumCellsX + col,
                                dist);
                        graph.addEdge(NewEdge);
                    }
                }
            }
        }
    }

    /**
     * Createing a graph based on a grid layout.
     * This function requires the dimensions of the environment and
     * the number of cells required horizontally and vertically
     * @param graph         Corresponding graph
     * @param cySize        The map Height
     * @param cxSize        The map Weight
     * @param NumCellsY     The number of points placed on Height
     * @param NumCellsX     The number of points placed on width
     * @param <graph_type>  The type of graph
     */
    public static <graph_type extends SparseGraph> void GraphHelper_CreateGrid(graph_type graph,
                                                                               int cySize,
                                                                               int cxSize,
                                                                               int NumCellsY,
                                                                               int NumCellsX) {

        graph.SparseGraph(NumCellsX,NumCellsY,cxSize/NumCellsX,cySize/NumCellsY);
        //need some temporaries to help calculate each node center
        double CellWidth = (double) cySize / (double) NumCellsX;
        double CellHeight = (double) cxSize / (double) NumCellsY;

        double midX = CellWidth / 2;
        double midY = CellHeight / 2;


        double realTimeNodes = 0;
        //first create all the nodes
        for (int row = 0; row < NumCellsY; ++row) {
            for (int col = 0; col < NumCellsX; ++col) {
                graph.addNode(new NavNode(graph.getNextFreeNodeIndex(),
                        new Point2D(midX + (col * CellWidth),
                                midY + (row * CellHeight))));

                realTimeNodes = realTimeNodes+1.0;
                double total = NumCellsX * NumCellsY;
                double realPer  = realTimeNodes / (total*2);
                graph.setRealContrustPercentage(realPer * 0.6);

            }
        }
        //now to calculate the edges. (A position in a 2d array [x][y] is the
        //same as [y*NumCellsX + x] in a 1d array). Each cell has up to eight
        //neighbours.
        for (int row = 0; row < NumCellsY; ++row) {
            for (int col = 0; col < NumCellsX; ++col) {
                GraphHelper_AddAllNeighboursToGridNode(graph, row, col, NumCellsX, NumCellsY);

                double total = NumCellsX * NumCellsY;
                double realPer = 1.0 / (total*2) * 0.6 + graph.getRealContrustPercentage();
                graph.setRealContrustPercentage(realPer);
//                System.out.println("grph comstruct:  "+ graph.getRealContrustPercentage());
            }
        }
    }

    /**
     * @param sg the Sparse graph on the mao
     * @param gn the node which tricking the player
     * @return Sparse graph by using SparseGraph
     */
    public SparseGraph floodFill(SparseGraph sg, GraphNode gn){
        Floodfill fl = new Floodfill(gn);

        return fl.stratFLood(sg);
    }

}

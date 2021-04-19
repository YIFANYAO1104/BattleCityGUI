package com.bham.bc.entity.physics;


import com.bham.bc.components.Controller;
import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.entity.graph.node.NavNode;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

import static com.bham.bc.utils.GeometryEnhanced.isZero;

/**
 * The fundamental section of whole map. It has the list which stores all the base elements.
 * And it also has the Hitbox attribute which will be used to tell if interact with another.
 * @param <entity>
 */

class Cell <entity extends Object>{

    public List<entity> Unites = new LinkedList<>();        // Store all game entites.

    public Hitbox cBox;

    public Cell(Point2D topleft, Point2D bottomright){
        cBox = new Hitbox(topleft,bottomright);
    }

    public void render(GraphicsContext gc, double cellWidth, double cellHeight,int NumCellsX){
        Point2D p = cBox.getTopLeft();
        int idx = (int) (p.getX() / cellWidth)
                + ((int) (p.getY() / cellHeight) * NumCellsX);
        gc.setFill(Color.GOLD);
        gc.fillText(String.valueOf(idx),p.getX(),p.getY()+15);
    }

}

public class MapDivision<entity extends BaseGameEntity>{
    private List<Cell<entity>> m_Cells = new ArrayList<>();             // List of all cell on map

    private List<entity> surround_entities;                             //Record all the entities surround with the target
    private ListIterator<entity> m_curSurr;

    private double m_Width;                                             //Map width
    private double m_Height;                                            //Map Height
    private int m_NumCellsX;                                            //The number of Cells on the row
    private int m_NumCellsY;                                            //The number of Cells on the column
    private double cellWidth, cellHeight;                               //The fundamental cell width and height. It depands the m_NumCellsX, m_NumCellsY

    // record the moving entities old index of Cell
    private HashMap<MovingEntity,Integer> register = new HashMap<>();


    /**
     * Given a Potin2d representing a position within the map, this
     * method calculates an index into its appropriate cell
     */
    private int PositionToIndex(Point2D pos) {

        int idx = (int) (pos.getX() / cellWidth)
                + ((int) (pos.getY() / cellHeight) * m_NumCellsX);

        //if the entity's position is equal to Point2d(m_Width, m_Height)
        //then the index will overshoot. We need to check for this and adjust
        if (idx > (int) m_Cells.size() - 1) {
            idx = (int) m_Cells.size() - 1;
        }

        return idx;
    }

    private List<Integer> getCellIndexes(Point2D pos,Point2D size) {

        int ix1 = (int) (pos.getX() / cellWidth);
        int iy1 = (int) (pos.getY() / cellHeight);

        Point2D br = pos.add(size);
        int ix2 = (int) (br.getX() / cellWidth);
        int iy2 = (int) (br.getY() / cellHeight);
        if (br.getX()-ix2*cellWidth<1E-8){
            ix2--;
        }
        if (br.getY()-iy2*cellHeight<1E-8){
            iy2--;
        }

        List<Integer> idxes = new ArrayList<>();
        for (int i = ix1; i <= ix2; i++) {
            for (int j = iy1; j <= iy2; j++) {
                int idx = i + j*m_NumCellsX;
                if (idx < m_Cells.size()){
                    idxes.add(idx);
                }
            }
        }

        return idxes;
    }

    /**
     *
     * @param m_Width1 Map width
     * @param m_Height1 Map Height
     * @param cellsX    The number of cell on the row
     * @param cellsY    The number of cell on the column
     * @param MaxEntities  The MaxEntities on the cell
     */

    public MapDivision(double m_Width1,
                       double m_Height1,
                       int cellsX,
                       int cellsY,
                       int MaxEntities){
        m_Width = m_Width1;
        m_Height = m_Height1;
        m_NumCellsX = cellsX;
        m_NumCellsY = cellsY;

        surround_entities = new ArrayList<entity>(MaxEntities);
        // calculate the height and width of the cell
        cellWidth = m_Width/m_NumCellsX;
        cellHeight = m_Height/m_NumCellsY;

        //create the cells
        for (int y = 0; y < m_NumCellsY; ++y) {
            for (int x = 0; x < m_NumCellsX; ++x) {
                double left = x * cellWidth;
                double right = left + cellWidth;
                double top = y * cellHeight;
                double bot = top + cellHeight;

                m_Cells.add(new Cell<entity>(new Point2D(left, top), new Point2D(right, bot)));
            }
        }
    }

    /**
     * Used to add the entities to the cells
     */
    public void addEntity(entity ent){
        assert (ent != null);

        int idx = PositionToIndex(ent.getPosition());

        m_Cells.get(idx).Unites.add(ent);
        if(ent instanceof MovingEntity){
            register.put((MovingEntity)ent,idx);
        }
    }

    /**
     * Add the list of entities into the cells
     * @param m1 List of entities.
     */

    public void addEntities(List<entity> m1){
        for(entity b1:m1){
            addEntity(b1);
        }
    }

    public void addObstacles(List<entity> m1){
        for(entity b1:m1){
            assert (b1 != null);
            List<Integer> idxes = getCellIndexes(b1.getPosition(),b1.getSize());
            for (Integer idx : idxes) {
                m_Cells.get(idx).Unites.add(b1);
            }
        }
    }

    public void updateMovingEntities(List<entity> n1){
        n1.forEach(this::updateMovingEntity);
    }

    /**
     * Update all Moving Entitis(bullets and gameCharacters) on the map,
     * when they should not be exist the game,they will be removed.
     * when they moved to another cell, it will be added in new cell and removed in old cell.
     * @param ent BaseGameEntity
     */
    public void updateMovingEntity(entity ent){
        if(!register.containsKey((MovingEntity) ent)){
            System.out.println("Two possible errors:\n1. It is not a MovingEntity\n2. The division does not contain this entity. Make sure to add it first");
            return;
        }
        int oldIdx = register.get(ent);
        int newIdx = PositionToIndex(ent.getPosition());

        //----------------------------remove-------if it is not exist----------------
        if(!((MovingEntity) ent).exists()){
            removedEntity(ent,oldIdx);
            register.remove(ent);
            return;
        }
        //---------------------------replace new idx to old poistion----------------
        if(newIdx != oldIdx){
            m_Cells.get(oldIdx).Unites.remove(ent);
            m_Cells.get(newIdx).Unites.add(ent);
            register.replace((MovingEntity) ent,newIdx);
        }
        return;

    }

    /**
     * remove entity from register
     * @param ent Entity
     * @param idx Cell's idx
     */
    public void removedEntity(entity ent, int idx){
        m_Cells.get(idx).Unites.remove(ent);
    }

    /**
     * update a List of genericObstacle.
     * @param a1
     */
    public void updateObstacles(List<entity> a1){
        a1.forEach(b1-> updateObstacle(b1));
    }

    /**
     * Update the genericObstacle,
     * when it should not exist. it would be removed from cell.
     * @param genericObstacle
     */
    public void updateObstacle(entity genericObstacle){
        if(! (genericObstacle instanceof Obstacle)) System.out.println("it is not a genericObstacle,but I will delete it");
        try {
            Obstacle g1 = (Obstacle) genericObstacle;
            if(!g1.exists()){
                List<Integer> idxes = getCellIndexes(genericObstacle.getPosition(),genericObstacle.getSize());
                for (Integer idx : idxes) {
                    removedEntity(genericObstacle, idx);
                }
//                int idx = PositionToIndex(genericObstacle.getPosition());
//                removedEntity(genericObstacle, idx);
            }
        }catch (Exception e){}
    }

    /**
     * Get the List of the Entity that should be consider if interacting with target.
     * @param entity the entity should be check collision
     * @param radius is the raidus of Hitbox
     */

    public List<entity> calculateNeighborsArray(entity entity, double radius){
        surround_entities.clear();
        Hitbox targetBox = new Hitbox(entity.getPosition(),entity.getPosition().add(entity.getSize()));


        ListIterator<Cell<entity>> c_iter = m_Cells.listIterator();
        while (c_iter.hasNext()){
            Cell<entity> curCell = c_iter.next();

            if(!curCell.Unites.isEmpty() && curCell.cBox.isInteractedWith(targetBox)){
                for(entity ent :curCell.Unites){
                    if(!curCell.Unites.isEmpty() && ent != entity &&  ent.intersects(entity))
                        surround_entities.add(ent);
                }
            }
        }
        return surround_entities;
    }

    /**
     * Get the List of the Entity that should be consider if interacting with target the location.
     * @param target Point2D the location should be check collision
     * @param radius is the raidus of Hitbox
     */
    public List<entity> calculateNeighborsArray(Point2D target, double radius){
        surround_entities.clear();
        // creat the hitbox whcih is the interact test box of the target area
        Hitbox targetBox = new Hitbox(target.subtract(radius,radius),target.add(radius,radius));


        ListIterator<Cell<entity>> c_iter = m_Cells.listIterator();
        while (c_iter.hasNext()){
            Cell<entity> curCell = c_iter.next();

            if(!curCell.Unites.isEmpty() && curCell.cBox.isInteractedWith(targetBox)){
                for(entity ent :curCell.Unites){
                    if(ent.getPosition().distance(target) < radius)
                        surround_entities.add(ent);
                }
            }
        }
        return surround_entities;
    }

    /**
     * Empty all cells' linklist on BaseGameEntities.
     */
    public void EmptyCells(){
        for(Cell cc: m_Cells){
            cc.Unites.clear();
        }
    }

    public void render(GraphicsContext gc){

//        renderNode(gc, Color.RED,new Point2D(400,400),7);
//        System.out.println(cellWidth);
//        System.out.println(cellHeight);
//        renderline(gc, Color.RED, new Point2D(cellWidth,0), new Point2D(cellWidth, m_Height));
        for(double i = 0; i<m_Height;i = i + cellHeight){
//            System.out.println("rendering");
            renderline(gc,Color.BISQUE,new Point2D(0,i),new Point2D(m_Width,i));
        }
        for(double i = 0; i<m_Width;i = i + cellWidth){
            renderline(gc,Color.BISQUE,new Point2D(i,0),new Point2D(i,m_Height));
        }

        for (Cell<entity> m_cell : m_Cells) {
            m_cell.render(gc,cellWidth,cellHeight,m_NumCellsX);
        }

    }

    private void renderNode(GraphicsContext gc,Color color, Point2D n1, int level){
        gc.setFill(color);
        gc.fillRoundRect(
                n1.getX(),n1.getY(),2*level,2*level,1*level,1*level);
    }

    private void renderline(GraphicsContext gc, Color color, Point2D n1, Point2D n2){
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.strokeLine(
                n1.getX(), n1.getY(), n2.getX(), n2.getY());
    }

    public int sizeOfCells(){
        return m_Cells.size();
    }

    public double getCellWidth(){
        return cellWidth;
    }

}

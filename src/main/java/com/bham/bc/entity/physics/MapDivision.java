package com.bham.bc.entity.physics;


import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;

import java.util.*;

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

        int idx = (int) (m_NumCellsX * pos.getX() / m_Width)
                + ((int) ((m_NumCellsY) * pos.getY() / m_Height) * m_NumCellsX);

        //if the entity's position is equal to Point2d(m_Width, m_Height)
        //then the index will overshoot. We need to check for this and adjust
        if (idx > (int) m_Cells.size() - 1) {
            idx = (int) m_Cells.size() - 1;
        }

        return idx;
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
                int idx = PositionToIndex(genericObstacle.getPosition());
                removedEntity(genericObstacle, idx);
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
        Point2D target = entity.getCenterPosition();
        // creat the hitbox whcih is the interact test box of the target area
        Hitbox targetBox = new Hitbox(target.subtract(radius,radius),target.add(radius,radius));


        ListIterator<Cell<entity>> c_iter = m_Cells.listIterator();
        while (c_iter.hasNext()){
            Cell<entity> curCell = c_iter.next();

            if(!curCell.Unites.isEmpty() && curCell.cBox.isInteractedWith(targetBox)){
                for(entity ent :curCell.Unites){
                    if(!curCell.Unites.isEmpty() && ent != entity &&  ent.getPosition().distance(target) < radius)
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

    public void Render(){}

    public int sizeOfCells(){
        return m_Cells.size();
    }
    public double getCellWidth(){
        return cellWidth;
    }

}

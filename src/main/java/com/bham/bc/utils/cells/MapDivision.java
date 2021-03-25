package com.bham.bc.utils.cells;


import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.MovingEntity;
import javafx.geometry.Point2D;

import java.util.*;


class Cell <entity extends Object>{

    public List<entity> Unites = new LinkedList<>();

    public Hitbox cBox;

    public Cell(Point2D topleft, Point2D bottomright){
        cBox = new Hitbox(topleft,bottomright);
    }

}

public class MapDivision<entity extends BaseGameEntity>{
    private List<Cell<entity>> m_Cells = new ArrayList<>();

    private List<entity> surround_entities;
    private ListIterator<entity> m_curSurr;

    private double m_Width;
    private double m_Height;
    private int m_NumCellsX;
    private int m_NumCellsY;
    private double cellWidth;
    private double cellHeight;

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
     * Used to add the entities to the cells linklist
     */
    public void AddEntity(entity ent){
        assert (ent != null);

        int idx = PositionToIndex(ent.getPosition());

        m_Cells.get(idx).Unites.add(ent);
        if(ent instanceof MovingEntity){
            register.put((MovingEntity)ent,idx);
        }
    }

    public void UpdateEntity(entity ent){
        if(!register.containsKey((MovingEntity) ent)){
            System.out.println("It may not be a Moving entitys or Does not contain this ent!!!, it should be added firstly");
            return;
        }
        int oldIdx = register.get(ent);
        int newIdx = PositionToIndex(ent.getPosition());

        if(newIdx == oldIdx) return;

        m_Cells.get(oldIdx).Unites.remove(ent);
        m_Cells.get(newIdx).Unites.add(ent);
        register.replace((MovingEntity) ent,newIdx);
    }

    /**
     *
     * @param target the entity should be check
     * @param radius is the raidus of Hitbox
     */
    public void CalculateNeighbors(Point2D target, double radius){
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

    }

    public entity start(){
        m_curSurr = surround_entities.listIterator();
        if(!m_curSurr.hasNext()) return null;

        return m_curSurr.next();
    }

    public entity next(){
        if(m_curSurr == null || !m_curSurr.hasNext()) return null;

        return m_curSurr.next();
    }

    public boolean end(){return (m_curSurr == null || (!m_curSurr.hasNext()));}

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
    public double getCellHeight(){
        return cellHeight;
    }

    public HashMap<MovingEntity, Integer> getRegister() {
        return register;
    }
}

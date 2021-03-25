package com.bham.bc.utils.cells;


import com.bham.bc.entity.BaseGameEntity;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


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

    private double m_Width;
    private double m_Height;
    private int m_NumCellsX;
    private int m_NumCellsY;
    private double cellWidth;
    private double cellHeight;


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
    }
}

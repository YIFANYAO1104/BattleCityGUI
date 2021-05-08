package com.bham.bc.entity.physics;


import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.*;

/**
 * The fundamental section of whole map. It has the list which stores all the base elements.
 * And it also has the Hitbox attribute which will be used to tell if interact with another.
 *
 */

class Cell <entity extends Object>{



    public List<entity> Unites = new LinkedList<>();        // Store all game entites.

    public Rectangle cBox;

    public Cell(Point2D topleft, Point2D bottomright){
        Point2D size = bottomright.subtract(topleft);
        cBox = new Rectangle(topleft.getX(),topleft.getY(), size.getY(),size.getY());
    }

    public void render(GraphicsContext gc, double cellWidth, double cellHeight,int NumCellsX){
        Point2D p = new Point2D(cBox.getX(),cBox.getY());
        int idx = (int) (p.getX() / cellWidth)
                + ((int) (p.getY() / cellHeight) * NumCellsX);
        gc.setFill(Color.GOLD);
        gc.fillText(String.valueOf(idx),p.getX(),p.getY()+15);
    }

}

public class MapDivision<entity extends BaseGameEntity>{
    private List<Cell<entity>> cellList = new ArrayList<>();             // List of all cell on map

    List<Shape> hb = new LinkedList<>();
    private double mapWidth;                                             //Map width
    private double mapHeight;                                            //Map Height
    private int horizontalCellNum;                                            //The number of Cells on the row
    private int verticalCellNum;                                            //The number of Cells on the column
    private double cellWidth, cellHeight;                               //The fundamental cell width and height. It depands the m_NumCellsX, m_NumCellsY

    // record the moving entities old index of Cell
    private HashMap<MovingEntity,Integer> register = new HashMap<>();


    /**
     * Given a Potin2d representing a position within the map, this
     * method calculates an index into its appropriate cell
     */
    private int PositionToIndex(Point2D pos) {

        int idx = (int) (pos.getX() / cellWidth)
                + ((int) (pos.getY() / cellHeight) * horizontalCellNum);

        //if the entity's position is equal to Point2d(m_Width, m_Height)
        //then the index will overshoot. We need to check for this and adjust
        if (idx > (int) cellList.size() - 1) {
            idx = (int) cellList.size() - 1;
        }

        return idx;
    }

    /**
     * return a list of cell zone id given a rectangle
     * @param pos the top left position
     * @param size the rectangle size
     * @return List of cell zone id
     */
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
                int idx = i + j* horizontalCellNum;
                if (idx < cellList.size()){
                    idxes.add(idx);
                }
            }
        }

        return idxes;
    }

    /**
     * Constructor for MapDivision
     * @param mapWidth Map width
     * @param mapHeight Map Height
     * @param cellsX    The number of cell on the row
     * @param cellsY    The number of cell on the column
     */

    public MapDivision(double mapWidth,
                       double mapHeight,
                       int cellsX,
                       int cellsY){
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.horizontalCellNum = cellsX;
        this.verticalCellNum = cellsY;

        // calculate the height and width of the cell
        this.cellWidth =  this.mapWidth / this.horizontalCellNum;
        this.cellHeight = this.mapHeight/ this.verticalCellNum;

        //create the cells
        for (int y = 0; y < verticalCellNum; ++y) {
            for (int x = 0; x < horizontalCellNum; ++x) {
                double left = x * cellWidth;
                double right = left + cellWidth;
                double top = y * cellHeight;
                double bot = top + cellHeight;

                cellList.add(new Cell<entity>(new Point2D(left, top), new Point2D(right, bot)));
            }
        }
    }

    /**
     * Add the entities to the cells
     * @param ent the entity
     */
    public void addEntity(entity ent){
        assert (ent != null);

        int idx = PositionToIndex(ent.getPosition());

        cellList.get(idx).Unites.add(ent);
        if(ent instanceof MovingEntity){
            register.put((MovingEntity)ent,idx);
        }
    }

    /**
     * Add the list of entities into the cells
     * @param entityList List of entities
     */
    public void addEntities(List<entity> entityList){
        for(entity b1:entityList){
            addEntity(b1);
        }
    }

    //test update
    public void addCrossZoneEntities(List<entity> m1){
        for(entity b1:m1){
            assert (b1 != null);
            List<Integer> idxes = getCellIndexes(b1.getPosition(),b1.getSize());
            for (Integer idx : idxes) {
                cellList.get(idx).Unites.add(b1);
            }
        }
    }

    public void updateMovingEntities(List<entity> n1){
        n1.forEach(this::updateMovingEntityZone);
    }

    /**
     * Update all Moving Entitis(bullets and gameCharacters) on the map,
     * when they should not be exist the game,they will be removed.
     * when they moved to another cell, it will be added in new cell and removed in old cell.
     * @param ent BaseGameEntity
     */
    public void updateMovingEntityZone(entity ent){
        if(!register.containsKey((MovingEntity) ent)){
            System.out.println("Two possible errors:\n1. It is not a MovingEntity\n2. The division does not contain this entity. Make sure to add it first");
            return;
        }
        int oldIdx = register.get(ent);
        int newIdx = PositionToIndex(ent.getPosition());

        //----------------------------remove-------if it is not exist----------------
//        if(!((MovingEntity) ent).exists()){
//            removeEntity(ent,oldIdx);
//            register.remove(ent);
//            return;
//        }
        //---------------------------replace new idx to old poistion----------------
        if(newIdx != oldIdx){
            cellList.get(oldIdx).Unites.remove(ent);
            cellList.get(newIdx).Unites.add(ent);
            register.replace((MovingEntity) ent,newIdx);
        }
        return;

    }

    /**
     * remove entity from register
     * @param ent Entity
     * @param idx Cell's idx
     */
    public void removeEntity(entity ent, int idx){
        cellList.get(idx).Unites.remove(ent);
    }

    /**
     * remove all the non existing entities
     */
    public void cleanNonExistingEntities(){
        for (Cell<entity> entityCell : cellList) {
            Iterator<entity> iterator = entityCell.Unites.iterator();
            while (iterator.hasNext()){
                entity next = iterator.next();
                if (!next.exists()){
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Get the List of the Entity that should be considered when doing collision test.
     * This method does not do collision test. It only return an entity list in relevant zones
     * @param entity the entity should be check collision
     * @return List of entity in relevant zones except the entity itself
     */
    public List<entity> getRelevantEntities(entity entity){
//        surround_entities.clear();
        Set<entity> surr = new HashSet<>();

        Bounds b = entity.getHitBox().getBoundsInParent();
        List<Integer> idxes = getCellIndexes(new Point2D(b.getMinX(), b.getMinY()), new Point2D(b.getWidth(), b.getHeight()));

        for (Integer idx : idxes) {
            Cell<entity> curCell = cellList.get(idx);
            for(entity ent :curCell.Unites){
                if(ent != entity)
                    surr.add(ent);
            }
        }
        return new ArrayList<entity>(surr);
    }

    public List<entity> getIntersectedEntities(Rectangle hitbox){
        Set<entity> surr = new HashSet<>();

        Bounds b = hitbox.getBoundsInParent();
        List<Integer> idxes = getCellIndexes(new Point2D(b.getMinX(), b.getMinY()), new Point2D(b.getWidth(), b.getHeight()));

        for (Integer idx : idxes) {
            Cell<entity> curCell = cellList.get(idx);
            for(entity ent :curCell.Unites){
                if(ent.intersects(hitbox))
                    surr.add(ent);
            }
        }
        return new ArrayList<entity>(surr);
    }

    /**
     * Get the List of the Entity that should be consider if interacting with target the location.
     * @param centerPos Point2D the location should be check collision
     * @param radius is the raidus of Hitbox
     */
    public List<entity> getIntersectedEntities(Point2D centerPos, double radius){
        Set<entity> surr = new HashSet<>();
        // creat the hitbox whcih is the interact test box of the target area
        Point2D tl = centerPos.subtract(radius,radius);
        Point2D size = new Point2D(2*radius,2*radius);
        List<Integer> idxes = getCellIndexes(tl, size);

        for (Integer idx : idxes) {
            Cell<entity> curCell = cellList.get(idx);
            for(entity ent :curCell.Unites){
                if(ent.getCenterPosition().distance(centerPos) < radius+ent.getHitBoxRadius())
                    surr.add(ent);
            }
        }
        if (radius==300){
            for (entity entity1 : surr) {
                hb.add(entity1.getHitBox());
            }
            hb.add(new Circle(centerPos.getX(),centerPos.getY(),radius));
        }
        return new ArrayList<entity>(surr);
    }

    /**
     * Empty all cells' linklist on BaseGameEntities.
     */
    public void clearCells(){
        for(Cell cc: cellList){
            cc.Unites.clear();
        }
    }

    public void render(GraphicsContext gc){

        for (Shape shape : hb) {
            GeometryEnhanced.renderHitBox(gc,shape);
        }
//        hb.clear();
//        renderNode(gc, Color.RED,new Point2D(400,400),7);
//        System.out.println(cellWidth);
//        System.out.println(cellHeight);
//        renderline(gc, Color.RED, new Point2D(cellWidth,0), new Point2D(cellWidth, m_Height));
        for(double i = 0; i< mapHeight; i = i + cellHeight){
//            System.out.println("rendering");
            renderline(gc,Color.BISQUE,new Point2D(0,i),new Point2D(mapWidth,i));
        }
        for(double i = 0; i< mapWidth; i = i + cellWidth){
            renderline(gc,Color.BISQUE,new Point2D(i,0),new Point2D(i, mapHeight));
        }

        for (Cell<entity> m_cell : cellList) {
            m_cell.render(gc,cellWidth,cellHeight, horizontalCellNum);
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
        return cellList.size();
    }

    public double getCellWidth(){
        return cellWidth;
    }

    public void cleanHB(){
        hb.clear();
    }

}

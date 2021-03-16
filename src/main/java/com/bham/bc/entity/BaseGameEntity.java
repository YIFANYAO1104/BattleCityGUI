package com.bham.bc.entity;

import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static com.bham.bc.entity.EntityManager.EntityMgr;

abstract public class BaseGameEntity {
    private static int m_iNextValidID = 0;
    private  int m_ID;

    protected double x, y;
    protected Image[] entityImages;


    /**
     * this must be called within each constructor to make sure the ID is set
     * correctly. It verifies that the value passed to the method is greater or
     * equal to the next valid ID, before setting the ID and incrementing the
     * next valid ID
     */
    private void SetID(int id) {
        //make sure the val is equal to or greater than the next available ID
        assert (id >= m_iNextValidID) : "<BaseGameEntity::SetID>: invalid ID";

        m_ID = id;
        m_iNextValidID = m_ID + 1;
    }

    @Override
    protected void finalize() throws Throwable{super.finalize();}


    protected BaseGameEntity(int ID, double x, double y) {
        SetID(ID);
        EntityMgr.RegisterEntity(this);
        this.x = x;
        this.y = y;
    }

    public static int GetNextValidID() {
        return m_iNextValidID;
    }

    /**
     *
     * @return
     */
    public int ID(){ return m_ID; }

    /**
     *
     * @return
     */
    public double getX() { return x; }

    /**
     *
     * @return
     */
    public double getY() { return y; }

    /**
     *
     * @param entity BaseGameEntity instance we want to check if the this instance is intersecting with
     * @return true if the two entities intersect and false otherwise
     */
    public boolean intersects(BaseGameEntity entity) { return this.getHitBox().intersects(entity.getHitBox().getBoundsInLocal()); }

    /**
     *
     */
    abstract public void update();

    /**
     *
     * @param gc
     */
    abstract public void render(GraphicsContext gc);

    /**
     *
     * @return
     */
    abstract public Shape getHitBox();

    /**
     *
     * @param msg
     * @return
     */
    abstract public boolean handleMessage(Telegram msg);

    /**
     *
     * @return
     */
    abstract public String toString();

}

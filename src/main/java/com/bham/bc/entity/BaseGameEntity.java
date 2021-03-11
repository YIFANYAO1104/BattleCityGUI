package com.bham.bc.entity;

import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.entity.EntityManager.EntityMgr;

abstract public class BaseGameEntity {

    /**
     * each entity has a unique ID
     */
    private  int m_ID;

    /**
     * this is the next valid ID. Each time a BaseGameEntity is instantiated
     * this value is updated
     */
    private static int m_iNextValidID = 0;

    /**
     * the position
     */
    protected int x, y;

    /**
     * tool for loading and storing Images
     */
    protected Image[] entityImages = null;


    /**
     * this must be called within each constructor to make sure the ID is set
     * correctly. It verifies that the value passed to the method is greater or
     * equal to the next valid ID, before setting the ID and incrementing the
     * next valid ID
     */
    private void SetID(int val) {
        //make sure the val is equal to or greater than the next available ID
        assert (val >= m_iNextValidID) : "<BaseGameEntity::SetID>: invalid ID";

        m_ID = val;

        m_iNextValidID = m_ID + 1;
    }

    @Override
    protected void finalize() throws Throwable{super.finalize();}

    public static int GetNextValidID() {
        return m_iNextValidID;
    }

    public int ID(){return m_ID;}

    protected BaseGameEntity(int ID, int x, int y) {
        SetID(ID);
        EntityMgr.RegisterEntity(this);
        this.x = x;
        this.y = y;
    }

    //getters and setters are only for non-son classes
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    abstract public void update();

    abstract public void render(GraphicsContext gc);
    abstract public Rectangle getHitBox();
    abstract public boolean handleMessage(Telegram msg);
    abstract public String toString();
    public boolean intersects(BaseGameEntity b) {return this.getHitBox().intersects(b.getHitBox().getBoundsInLocal()); };
}

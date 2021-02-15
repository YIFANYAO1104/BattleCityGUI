package com.bham.bc.common;

import com.bham.bc.common.Messaging.Telegram;

import java.awt.*;

import static com.bham.bc.EntityManager.EntityMgr;

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
    protected static Toolkit tk = Toolkit.getDefaultToolkit();
    protected Image[] entityImags = null;


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

    //g.drawImage(wallImags[0], x, y, null);
    abstract public void render(Graphics g);
    //return new Rectangle(x, y, width, length);
    abstract public Rectangle getRect();
    abstract public boolean handleMessage(Telegram msg);
    abstract public String toString();
}

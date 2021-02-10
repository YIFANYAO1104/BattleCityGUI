package main.java.com.bham.bc;

import java.awt.*;

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

    protected BaseGameEntity(int ID) {
        SetID(ID);
    }

    abstract public void Update();
    abstract public void Render(Graphics g);

    public static int GetNextValidID() {
        return m_iNextValidID;
    }

    public int GetID(){return m_ID;}
}

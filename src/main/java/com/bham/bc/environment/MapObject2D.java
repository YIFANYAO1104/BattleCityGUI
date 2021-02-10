package main.java.com.bham.bc.environment;

import main.java.com.bham.bc.tankGame;

import java.awt.*;
/* An abstract parent class of All Map Objects */
public abstract class MapObject2D {
    /**
     * Coordinate of Object
     */
    protected int x, y;
    protected tankGame tc;
    protected static Toolkit tk = Toolkit.getDefaultToolkit();

    /**
     * Constructor Of Object
     * @param x
     * @param y
     * @param tc
     */
    public MapObject2D(int x, int y, tankGame tc) {
        this.x = x;
        this.y = y;
        this.tc = tc;
    }

    //g.drawImage(wallImags[0], x, y, null);
    public abstract void render(Graphics g);

    //return new Rectangle(x, y, width, length);
    public abstract Rectangle getRect();

}

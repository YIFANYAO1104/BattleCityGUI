package main.java.com.bham.bc.environment;

import main.java.com.bham.bc.tankGame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
/**
 * Size OF Metal Wall (Metal Wall can not be destroyed )
 */
public class MetalWall extends MapObject2D{
	public static final int width = 36; 
	public static final int length = 37;
	private static Image[] wallImags = null;
	static {
		wallImags = new Image[] { tk.getImage(CommonWall.class
				.getResource("/Images/metalWall.gif")), };
	}

	/**
	 * Constructor Of Metal Wall
	 * @param x
	 * @param y
	 * @param tc
	 */
	public MetalWall(int x, int y, tankGame tc) {
		super(x,y,tc);
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(wallImags[0], x, y, null);
	}

	@Override
	public Rectangle getRect() { 
		return new Rectangle(x, y, width, length);
	}
}

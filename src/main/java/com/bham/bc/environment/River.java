package main.java.com.bham.bc.environment;

import main.java.com.bham.bc.tankGame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * Building River on the Map
 */
public class River extends MapObject2D{
	public static final int riverWidth = 55;
	public static final int riverLength = 154;
	private static Image[] riverImags = null;
	
	static {   
		riverImags = new Image[]{
				tk.getImage(CommonWall.class.getResource("/Images/river.jpg")),
		};
	}

	/**
	 * Constructor Of River
	 * @param x
	 * @param y
	 * @param tc
	 */
	public River(int x, int y, tankGame tc) {
		super(x,y,tc);
	}
	

	public static int getRiverWidth() {
		return riverWidth;
	}

	public static int getRiverLength() {
		return riverLength;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(x, y, riverWidth, riverLength);
	}

	/**
	 * Draw River On Map
	 * @param g
	 */
	@Override
	public void render(Graphics g) {
		g.drawImage(riverImags[0],x, y, null);
	}


}

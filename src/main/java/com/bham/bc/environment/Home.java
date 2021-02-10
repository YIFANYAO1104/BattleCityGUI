package main.java.com.bham.bc.environment;

import main.java.com.bham.bc.tankGame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * Home (Or something we need to protect, can be modified later)
 */
public class Home extends MapObject2D{
	/**
	 * Size Of Home
	 */
	public static final int width = 43, length = 43; 
	private boolean live = true;

	private static Image[] homeImags = null;
	static {
		homeImags = new Image[] { tk.getImage(CommonWall.class
				.getResource("/Images/home.jpg")), };
	}

	public Home(int x, int y, tankGame tc) {
		super(x,y,tc);
	}

	/**
	 * Draw the Home
	 * @param g
	 */
	public void render(Graphics g) {
		g.drawImage(homeImags[0], x, y, null);
	}

	public boolean isLive() { 
		return live;
	}

	public void setLive(boolean live) { 
		this.live = live;
	}

	public Rectangle getRect() { 
		return new Rectangle(x, y, width, length);
	}

}

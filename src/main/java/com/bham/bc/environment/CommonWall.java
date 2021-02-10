package main.java.com.bham.bc.environment;

import java.awt.*;
import main.java.com.bham.bc.tankGame;

public class CommonWall extends MapObject2D{
	/**
	 * Size OF CommonWall (Common Wall can be destroyed )
	 */
	public static final int width = 22; 
	public static final int length = 21;
	private static Image[] wallImags = null;
	static {
		wallImags = new Image[] { 
		tk.getImage(CommonWall.class.getResource("/Images/commonWall.gif")), };
	}

	public CommonWall(int x, int y, tankGame tc) {
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

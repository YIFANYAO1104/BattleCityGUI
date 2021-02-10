package main.java.com.bham.bc.environment;

import main.java.com.bham.bc.tankGame;

import java.awt.*;

/**
 * Build Trees
 */
public class Tree extends MapObject2D{
	/**
	 * Size OF trees
	 */
	public static final int width = 30;
	public static final int length = 30;
	private static Image[] treeImags = null;
	static {
		treeImags = new Image[]{
				tk.getImage(CommonWall.class.getResource("/Images/tree.gif")),
		};
	}
	
	
	public Tree(int x, int y, tankGame tc) {
		super(x,y,tc);
	}

	/**
	 * Draw Trees
	 * @param g
	 */
	@Override
	public void render(Graphics g) {
		g.drawImage(treeImags[0],x, y, null);
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

}

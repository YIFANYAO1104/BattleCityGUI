package main.java.com.bham.bc;

import main.java.com.bham.bc.environment.CommonWall;

import java.awt.*;
import java.util.Random;

/**
 * Add Blood to Tank
 */
public class GetBlood {
	/**
	 * Size of Blood Object
	 */
	public static final int width = 34;
	public static final int length = 30;

	private int x, y;
	tankGame tc;
	private static Random r = new Random();

	int step = 0;
	private boolean live = false;
	/**
	 * java.awt
	 */
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] bloodImags = null;
	static {
		bloodImags = new Image[] { tk.getImage(CommonWall.class
				.getResource("/Images/hp.png")), };
	}

	/**
	 * The position of Blood Object, should be changed to generating randomly in later progress
	 */
	private int[][] position = { { 700, 196 }, { 500, 58 }, { 80, 300 },
			{600,321}, { 345, 456 }, { 123, 321 }, { 258, 413 } };

	/**
	 *
	 * @param g
	 * Using the probability of Random to create live blood Object
	 */
	public void draw(Graphics g) {
		if (r.nextInt(100) > 98) {
			this.live = true;
			move();
		}
		if (!live)
			return;
		g.drawImage(bloodImags[0], x, y, null);

	}

	/**
	 * Indicate the position of Blood Object using step as the index of Position list
	 * When step reaches max, initialize to zero,so it will start a new loop of generating Blood Object
	 */
	private void move() {
		step++;
		if (step == position.length) {
			step = 0;
		}
		x = position[step][0];
		y = position[step][1];
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

	public boolean isLive() {
		return live;
	}

	/**
	 *
	 * @param live
	 * Change the availability of Current Blood Object
	 */
	public void setLive(boolean live) {
		this.live = live;
	}

}

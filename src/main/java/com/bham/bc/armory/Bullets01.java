package main.java.com.bham.bc.armory;

import main.java.com.bham.bc.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Bullets01 extends Tank_Bullet {

	/**
	 * Java.awt
	 */
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] bulletImages = null;
	/**
	 * Using a HashMap to Store Bullet Direction and its corresponding pic as key-value Entry
	 */
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	static {
		bulletImages = new Image[] {
				tk.getImage(Bullets01.class.getClassLoader().getResource(
						"images/bulletL.gif")),

				tk.getImage(Bullets01.class.getClassLoader().getResource(
						"images/bulletU.gif")),

				tk.getImage(Bullets01.class.getClassLoader().getResource(
						"images/bulletR.gif")),

				tk.getImage(Bullets01.class.getClassLoader().getResource(
						"images/bulletD.gif")),

		};

		imgs.put("L", bulletImages[0]);

		imgs.put("U", bulletImages[1]);

		imgs.put("R", bulletImages[2]);

		imgs.put("D", bulletImages[3]);

	}

	/**
	 * Constructor of Bullet, including coordinate and the bullet direction
	 * @param x
	 * @param y
	 * @param dir
	 */
	public Bullets01(int x, int y, Direction dir) {
		super(GetNextValidID());
		this.x = x;
		this.y = y;
		this.diretion = dir;
	}

	/**
	 * Overloading Constructor of Bullet
	 * @param x
	 * @param y
	 * @param good
	 * @param dir
	 * @param tc
	 */
	public Bullets01(int x, int y, boolean good, Direction dir, tankGame tc) {
		this(x, y, dir);
		this.good = good;
		this.tc = tc;
	}

	/**
	 * Movement Of Bullet
	 */
	@Override
	protected void move() {

		switch (diretion) {
		case L:
			x -= speedX;
			break;

		case U:
			y -= speedY;
			break;

		case R:
			x += speedX;
			break;

		case D:
			y += speedY;
			break;

		case STOP:
			break;
		}

		/**
		 * When bullet Out Of Bound then Destroy
		 */
		if (x < 0 || y < 0 || x > tankGame.Fram_width
				|| y > tankGame.Fram_length) {
			live = false;
		}
	}

	/**
	 * The method to draw the Bullet
	 * Using bullet's Direction to Get the corresponding Pic to render
	 * @param g
	 */
	@Override
	public void Render(Graphics g) {
		if (!live) {
			tc.bullets.remove(this);
			return;
		}

		switch (diretion) {
		case L:
			g.drawImage(imgs.get("L"), x, y, null);
			break;

		case U:
			g.drawImage(imgs.get("U"), x, y, null);
			break;

		case R:
			g.drawImage(imgs.get("R"), x, y, null);
			break;

		case D:
			g.drawImage(imgs.get("D"), x, y, null);
			break;

		}
		/**
		 * Update Bullet's Coordinate
		 */
		move();
	}



	@Override
	public void Update() {

	}

}

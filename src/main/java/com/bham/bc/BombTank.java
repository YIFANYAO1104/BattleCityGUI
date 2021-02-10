package main.java.com.bham.bc;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * Bomb effect
 */

public class BombTank extends BaseGameEntity{
	private int x, y;
	private boolean live = true; 
	private tankGame tc;
	/**
	 * java.awt Should be change to JavaFx later.
	 */
	private static Toolkit tk = Toolkit.getDefaultToolkit();

	private static Image[] imgs = {
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/1.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/2.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/3.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/4.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/5.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/6.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/7.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/8.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/9.gif")),
			tk.getImage(BombTank.class.getClassLoader().getResource(
					"Images/10.gif")), };
	int step = 0;

	/**
	 *
	 * @param x
	 * @param y
	 * @param tc
	 * Constructor of BombTank. When tank gets bombed the Object should be created
	 */
	public BombTank(int x, int y, tankGame tc) {
		super(GetNextValidID());
		this.x = x;
		this.y = y;
		this.tc = tc;
	}

	@Override
	public void Update() {

	}

	/**
	 * If the Bombed Tank is died, remove it from bombTank List in tankGame
	 * When the tank is Bombed 10 times it dies.
	 * @param g
	 */
	@Override
	public void Render(Graphics g) {
		if (!live) {
			tc.bombTanks.remove(this);
			return;
		}
		if (step == imgs.length) {
			live = false;
			step = 0;
			return;
		}

		g.drawImage(imgs[step], x, y, null);
		step++;
	}
}

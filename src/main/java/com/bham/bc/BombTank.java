package com.bham.bc;

import com.bham.bc.common.BaseGameEntity;
import com.bham.bc.common.Messaging.Telegram;

import java.awt.*;

import  static  com.bham.bc.CenterController.*;

/**
 * Bomb effect
 */

public class BombTank extends BaseGameEntity {

	/**
	 * Status of tank's life
	 */
	private boolean live = true;
	/**
	 * Indicates the progress of bombing, we use couple images to show one time of bombing
	 */
	int step = 0;
	/**
	 *
	 * @param x
	 * @param y
	 * @param tc
	 * Constructor of BombTank. When tank gets bombed the Object should be created
	 */
	public BombTank(int x, int y) {
		super(GetNextValidID(),x,y);
		initImages();
	}
	/**
	 * Images of bombing,should be replaced later
	 */
	private void initImages() {
		entityImags = new Image[] {
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
	}

	@Override
	public void update() {

	}
	/**
	 * If the Bombed Tank is died, remove it from bombTank List in tankGame
	 * When the tank is Bombed 10 times it dies.
	 * @param g
	 */
	@Override
	public void render(Graphics g) {
		if (!live) {
			centerController.removeBombTank(this);
			return;
		}
		if (step == entityImags.length) {
			live = false;
			step = 0;
			return;
		}

		g.drawImage(entityImags[step], x, y, null);
		step++;
	}

	@Override
	public Rectangle getRect() {
		return null;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	@Override
	public String toString() {
		return "specially effect";
	}
}

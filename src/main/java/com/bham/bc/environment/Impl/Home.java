package com.bham.bc.environment.Impl;

import com.bham.bc.common.Messaging.Telegram;
import com.bham.bc.environment.MapObject2D;

import java.awt.*;

/**
 * Home (Or something we need to protect, can be modified later)
 */
public class Home extends MapObject2D {
	/**
	 * Size Of Home
	 */
	public static int width = 43;
	public static int length = 43;

	private boolean live = true;

	public Home(int x, int y) {
		super(x,y);
		initImages();
	}

	private void initImages() {
		entityImags = new Image[] {
				tk.getImage(CommonWall.class.getResource("/Images/home.jpg")), };
	}


	@Override
	public void update() {

	}
	/**
	 * Draw the Home
	 * @param g
	 */
	public void render(Graphics g) {
		g.drawImage(entityImags[0], x, y, null);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	public boolean isLive() { 
		return live;
	}

	public void setLive(boolean live) { 
		this.live = live;
	}

	@Override
	public Rectangle getRect() { 
		return new Rectangle(x, y, width, length);
	}

}

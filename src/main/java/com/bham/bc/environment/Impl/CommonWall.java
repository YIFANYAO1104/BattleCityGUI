package com.bham.bc.environment.Impl;

import com.bham.bc.common.Messaging.Telegram;
import com.bham.bc.environment.MapObject2D;

import java.awt.*;

public class CommonWall extends MapObject2D {
	/**
	 * Size OF CommonWall (Common Wall can be destroyed )
	 */
	public static int width = 22;
	public static int length = 21;

	public CommonWall(int x, int y) {
		super(x,y);
		initImages();
	}

	private void initImages() {
		entityImags = new Image[] {
				tk.getImage(CommonWall.class.getResource("/Images/commonWall.gif")), };
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(entityImags[0], x, y, null);
	}

	@Override
	public Rectangle getRect() {  
		return new Rectangle(x, y, width, length);
	}

	@Override
	public void update() {

	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
}

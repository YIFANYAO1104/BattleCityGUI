package com.bham.bc.components.armory;

import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

import static com.bham.bc.components.CenterController.centerController;
import static com.bham.bc.entity.EntityManager.EntityMgr;

public class Bullets01 extends Bullet {
	/**
	 * Static member to indicates the velocity of Bullet in horizontal and vertical
	 * It's not redundant but other classes could simply reference them by Bullets01.speedX
	 */
	public static int speedX = 15;
	public static int speedY = 15;
	/**
	 * Size of Bullet01
	 * It's not redundant but other classes could simply reference them by Bullets01.width
	 */
	public static int width = 10;
	public static int length = 10;
	/**
	 * Using a HashMap to Store Bullet Direction and its corresponding pic as key-value Entry
	 */
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	/**
	 * Constructor of Bullet
	 * @param x
	 * @param y
	 * @param dir
	 */
	public Bullets01(int owner ,int x, int y, Direction dir) {
		super(owner,speedX,speedY, x, y,width,length, dir);
		initImages();
	}

	/**
	 * A image Array to put bullet in different directions, should replace images later
	 */
	void initImages() {

		entityImages = new Image[] {
				new Image("file:src/main/resources/img/bulletL.gif"),
				new Image("file:src/main/resources/img/bulletU.gif"),
				new Image("file:src/main/resources/img/bulletR.gif"),
				new Image("file:src/main/resources/img/bulletD.gif"),
		};

		imgs.put("L", entityImages[0]);

		imgs.put("U", entityImages[1]);

		imgs.put("R", entityImages[2]);

		imgs.put("D", entityImages[3]);

	}
	/**
	 * Movement Of Bullet
	 * The bullet can not go beyond the frame or it will be set to dead
	 */
	@Override
	protected void move() {

		switch (direction) {
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

		if (x < 0 || y < 0 || x > Constants.WINDOW_WIDTH
				|| y > Constants.WINDOW_HEIGHT) {
			live = false;
		}
	}
	/**
	 * The method to draw the Bullet
	 * Using bullet's Direction to Get the corresponding Pic to render
	 * @param gc
	 */
	@Override
	public void render(GraphicsContext gc) {

		switch (direction) {
		case L:
			gc.drawImage(imgs.get("L"), x, y);
			break;

		case U:
			gc.drawImage(imgs.get("U"), x, y);
			break;

		case R:
			gc.drawImage(imgs.get("R"), x, y);
			break;

		case D:
			gc.drawImage(imgs.get("D"), x, y);
			break;

		}

	}

	@Override
	public void update() {
		if(!live){
			EntityMgr.RemoveEntity(this);
		}

		move();

		if (!live) {
			centerController.removeBullet(this);
			return;
		}

	}


}

package com.bham.bc.components.armory;

import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;


import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.EntityMgr;

public class DefaultBullet extends Bullet {
	public static final double SPEED = 5;
	public static final int WIDTH = 6;
	public static final int HEIGHT = 12;

	/**
	 * Constructor of Bullet
	 * @param owner
	 * @param x
	 * @param y
	 */
	public DefaultBullet(int owner, double x, double y, double angle) {
		super(owner, x, y, SPEED, WIDTH, HEIGHT, angle);
		initImages();
	}

	/**
	 * A image Array to put bullet in different directions, should replace images later
	 */
	void initImages() {

		entityImages = new Image[] {
				new Image("file:src/main/resources/img/armory/defaultBullet.png"),
		};
	}
	/**
	 * Movement Of Bullet
	 * The bullet can not go beyond the frame or it will be set to dead
	 */
	@Override
	protected void move() {
		x += Math.sin(Math.toRadians(angle)) * speed;
		y -= Math.cos(Math.toRadians(angle)) * speed;

		if (x < 0 || y < 0 || x > Constants.MAP_WIDTH || y > Constants.MAP_HEIGHT) {
			isAlive = false;
		}
	}
	/**
	 * The method to draw the Bullet
	 * Using bullet's Direction to Get the corresponding Pic to render
	 * @param gc
	 */
	@Override
	public void render(GraphicsContext gc) {
		drawRotatedImage(gc, entityImages[0], angle, x, y);
	}

	@Override
	public void update() {
		move();

		if (!isAlive) {
			EntityMgr.RemoveEntity(this);
			backendServices.removeBullet(this);
		}
	}
}

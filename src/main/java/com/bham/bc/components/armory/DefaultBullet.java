package com.bham.bc.components.armory;

import com.bham.bc.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;


import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;

public class DefaultBullet extends Bullet {
	public static final String IMAGE_PATH = "file:src/main/resources/img/armory/defaultBullet.png";
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
		super(owner, x, y, SPEED, angle);
		entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
	}

	@Override
	protected void move() {
		x += Math.sin(Math.toRadians(angle)) * speed;
		y -= Math.cos(Math.toRadians(angle)) * speed;

		if (x < 0 || y < 0 || x > Constants.MAP_WIDTH || y > Constants.MAP_HEIGHT) exists = false;
	}

	@Override
	public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

	@Override
	public Rectangle getHitBox() {
		Rectangle hitBox = new Rectangle(x, y, WIDTH, HEIGHT);
		hitBox.getTransforms().add(new Rotate(angle, x + WIDTH/2,y + HEIGHT/2));

		return hitBox;
	}

	@Override
	public void update() {
		move();

		if (!exists) {
			entityManager.removeEntity(this);
			backendServices.removeBullet(this);
		}
	}
}

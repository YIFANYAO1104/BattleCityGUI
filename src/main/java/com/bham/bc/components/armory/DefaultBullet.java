package com.bham.bc.components.armory;

import com.bham.bc.components.characters.SIDE;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.utils.Constants;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;


import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Represents a default bullet the player starts with
 */
public class DefaultBullet extends Bullet {

	public static final String IMAGE_PATH = "file:src/main/resources/img/armory/defaultBullet.png";
	public static final int WIDTH = 6;
	public static final int HEIGHT = 12;

	public static final double SPEED = 5;
	public static final double DAMAGE = 25;

	/**
	 * Constructs a bullet using default bullet's attributes for speed and damage
	 *
	 * @param x      top left position in x axis
	 * @param y      top left position in y axis
	 * @param angle  angle at which the bullet will move
	 * @param side   ALLY or ENEMY side the bullet belongs to
	 */
	public DefaultBullet(double x, double y, double angle, SIDE side) {
		super(x, y, SPEED, angle, side, DAMAGE);
		entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
	}

	@Override
	public void destroy() {
		entityManager.removeEntity(this);
		exists = false;
	}

	@Override
	public void move() {
		//TODO:Move to steering.calculate
//		velocity = new Point2D(Math.sin(Math.toRadians(angle)),Math.cos(Math.toRadians(angle))).multiply(speed);

//		System.out.println("bullet"+velocity);
//		System.out.println("angle"+angle);

		x += velocity.getX();
		y -= velocity.getY();

		if (x < 0 || y < 0 || x > GameMap.getWidth() || y > GameMap.getHeight()) {
			entityManager.removeEntity(this);
			exists = false;
		}
	}

	@Override
	public void update() { move(); }

	@Override
	public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

	@Override
	public Rectangle getHitBox() {
		Rectangle hitBox = new Rectangle(x, y, WIDTH, HEIGHT);
		hitBox.getTransforms().add(new Rotate(angle, x + WIDTH/2,y + HEIGHT/2));

		return hitBox;
	}


}

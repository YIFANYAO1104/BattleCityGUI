package com.bham.bc.components.armory;

import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;


import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Represents a default bullet the player starts with
 */
public class DefaultBullet extends Bullet {
	public static final BulletType TYPE = BulletType.DEFAULT;
	public static final double SPEED = 5;
	public static final double DAMAGE = 25;

	/**
	 * Constructs a bullet using default bullet's attributes for speed and damage
	 *
	 * @param x      top left position in x axis
	 * @param y      top left position in y axis
	 * @param heading  a normalized vector indicate the direction
	 * @param side   ALLY or ENEMY side the bullet belongs to
	 */
	public DefaultBullet(double x, double y, Point2D heading, Side side) {
		super(x, y, SPEED, heading, TYPE, side, DAMAGE);
	}

	@Override
	public void destroy() {
		entityManager.removeEntity(this);
		exists = false;
	}

	@Override
	public void move() {
		x += velocity.getX();
		y += velocity.getY();

		if (x < 0 || y < 0 || x > GameMap.getWidth() || y > GameMap.getHeight()) {
			entityManager.removeEntity(this);
			exists = false;
		}
	}

	@Override
	public void update() { move(); }

	@Override
	public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], getAntiAngleY()); }

	@Override
	public Rectangle getHitBox() {
		Rectangle hitBox = new Rectangle(x, y, getRadius().getX(), getRadius().getY());
		hitBox.getTransforms().add(new Rotate(getAntiAngleY(), x + getRadius().getX()/2,y + getRadius().getY()/2));

		return hitBox;
	}


}

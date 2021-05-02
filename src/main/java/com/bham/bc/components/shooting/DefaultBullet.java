package com.bham.bc.components.shooting;

import com.bham.bc.components.characters.Side;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.triggers.effects.Dissolve;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;


import static com.bham.bc.components.Controller.services;
import static com.bham.bc.entity.EntityManager.entityManager;

/**
 * Represents a default bullet the player starts with
 */
public class DefaultBullet extends Bullet {
	public static final BulletType TYPE = BulletType.DEFAULT;
	public static final double SPEED = 5;
	public static final double DAMAGE = 10;

	/**
	 * Constructs a bullet using default bullet's attributes for speed and damage
	 *
	 * @param x       top left position in x axis
	 * @param y       top left position in y axis
	 * @param heading a normalized vector indicating the direction the bullet is moving
	 * @param side    ALLY or ENEMY side the bullet belongs to
	 */
	public DefaultBullet(double x, double y, Point2D heading, Side side) {
		super(x, y, SPEED, heading, TYPE, side, DAMAGE);
	}

	@Override
	public void destroy() {
		entityManager.removeEntity(this);
		exists = false;

		Dissolve dissolve = new Dissolve(getPosition(), entityImages[0], getAngle(), 2, 2);
		services.addEffectTrigger(dissolve);
	}

	@Override
	public void setTime() {

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
	public void update() {
		move();
	}

	@Override
	public Rectangle getHitBox() {
		Rectangle hitBox = new Rectangle(x, y, getSize().getX(), getSize().getY());
		hitBox.getTransforms().add(new Rotate(getAngle(), x + getSize().getX()/2,y + getSize().getY()/2));

		return hitBox;
	}

	@Override
	public double getHitBoxRadius() {
		return Math.hypot(getHitBox().getWidth()/2, getHitBox().getHeight()/2);
	}
}

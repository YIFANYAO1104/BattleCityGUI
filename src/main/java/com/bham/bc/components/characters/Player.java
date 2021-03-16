package com.bham.bc.components.characters;


import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.Optional;

import static com.bham.bc.components.CenterController.backendServices;
import static java.lang.Math.signum;

public class Player extends Character implements TrackableCharacter {

	public static final int WIDTH = 24;
	public static final int HEIGHT = 35;
	public static final int MAX_HP = 100;
	private int hp;


	private double angle;

	private SimpleDoubleProperty trackableX;
	private SimpleDoubleProperty trackableY;




	/**
	 * Constructor of Player Tank,also indicates which Player it is creating
	 * @param x
	 * @param y
	 */
	public Player(double x, double y) {
		super(x, y, 5,32,32);

		initImages();
		initTrackableCoordinates();

		hp = MAX_HP;

	}

	/**
	 * List of images should be replaced later
	 */
	private void initImages() {
		entityImages = new Image[] {
				new Image("file:src/main/resources/img/characters/player.png", 36, 36, true, false),
		};
	}

	/**
	 * Draw Blood Bar
	 * @param gc
	 */
	private void renderBloodbBar(GraphicsContext gc) {
		Paint c = gc.getFill();
		gc.setFill(Color.RED);
		gc.fillRect(375, 585, width, 10);
		int w = width * hp / 200;
		gc.fillRect(375, 585, w, 10);
		gc.setFill(c);
	}


	/**
	 * Render Method
	 * Use directions to determine the image of Player Tank
	 * @param gc
	 */
	@Override
	public void render(GraphicsContext gc) {
		if (!isAlive) return;
		renderBloodbBar(gc);
		drawRotatedImage(gc, entityImages[0], angle, x, y);
	}

	@Override
	public Ellipse getHitBox() {
		Point2D hitBoxOffset = new Point2D(0, 4);
		Ellipse hitBox = new Ellipse(x + entityImages[0].getWidth()/2, y + entityImages[0].getHeight()/2, 12, 14);
		hitBox.getTransforms().add(new Rotate(angle, hitBox.getCenterX(), hitBox.getCenterY()));
		hitBox.getTransforms().add(new Translate(hitBoxOffset.getX(), hitBoxOffset.getY()));

		return hitBox;
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getCode()) {
			case F: fire(); break;
			case W: directionSet.add(Direction.U); break;
			case A: directionSet.add(Direction.L); break;
			case S: directionSet.add(Direction.D); break;
			case D: directionSet.add(Direction.R); break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getCode()) {
			case W: directionSet.remove(Direction.U); break;
			case A: directionSet.remove(Direction.L); break;
			case S: directionSet.remove(Direction.D); break;
			case D: directionSet.remove(Direction.R); break;
		}
	}

	/**
	 * Updates angle at which the player is facing
	 *
	 * <p>This method goes through every direction in the directionSet, coverts them to basis vectors,
	 * adds them up to get a final direction vector and calculates the angle between it and (0, 1)</p>
	 *
	 * <b>Note:</b> the basis vector which is used for angle calculation must be (0, 1) as this is the
	 * way the player in the image is facing (upwards)
	 */
	private void updateAngle() {
		Optional<Point2D> directionPoint = directionSet.stream().map(Direction::toPoint).reduce(Point2D::add);
		directionPoint.ifPresent(p -> { if(p.getX() != 0 || p.getY() != 0) angle = p.angle(0, 1) * (p.getX() > 0 ? 1 : -1); });
	}


	/**This method create the firing bullet01
	 * Use Kdirection to set the direction of Bullet
	 * Add bullet to list of bullets
	 */
	public DefaultBullet fire() {
		double centerBulletX = x + entityImages[0].getWidth()/2;
		double centerBulletY = y - DefaultBullet.HEIGHT/2;

		Rotate rot = new Rotate(angle, x + entityImages[0].getWidth()/2, y + entityImages[0].getHeight()/2);
		Point2D newBul = rot.transform(centerBulletX, centerBulletY);

		double topLeftBulletX = newBul.getX() - DefaultBullet.WIDTH/2;
		double topLeftBulletY = newBul.getY() - DefaultBullet.HEIGHT/2;

		DefaultBullet m = new DefaultBullet(this.ID(), topLeftBulletX, topLeftBulletY, angle);
		backendServices.addBullet(m);
		return m;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean alive) {
		this.isAlive = alive;
	}

	public boolean isUser() {
		return true;
	}
	/**
	 * Since the tanks on the map was put into a list, we need to check if the this.tank collide
	 * with any of those tanks, if intersects, change both of tanks's coordinate to previous value
	 * so they can not go further
	 * Note: IF more environment class are creating, should write more collideWithXXXX methods...
	 */
	public boolean collideWithTanks(java.util.List<Enemy> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			MovingEntity t = tanks.get(i);
			if (this != t) {
				if (this.isAlive && t.isAlive()
						&& this.intersects(t)) {
					this.changToOldDir();
					t.changToOldDir();
					return true;
				}
			}
		}
		return false;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	@Override
	public void increaseHealth(int health) { hp = Math.min(hp + health, MAX_HP); }

	@Override
	public void switchWeapon(Weapon w) { }


	@Override
	public void update() {
		updateAngle();
		move();
		trackableX.set(this.x + this.width/2);
		trackableY.set(this.y + this.length/2);
	}


	@Override
	protected void move() {
		if(!directionSet.isEmpty()) {
			x += Math.sin(Math.toRadians(angle)) * speed;
			y -= Math.cos(Math.toRadians(angle)) * speed;
		}
	}

	@Override
	public void initTrackableCoordinates() {
		trackableX = new SimpleDoubleProperty(Constants.WINDOW_WIDTH/2);
		trackableY = new SimpleDoubleProperty(Constants.WINDOW_HEIGHT/2);
	}

	@Override
	public SimpleDoubleProperty getTrackableCoordinateX() {
		return trackableX;
	}
	@Override
	public SimpleDoubleProperty getTrackableCoordinateY() {
		return trackableY;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		switch (msg.Msg.id){
			case 0:
				System.out.println("you are deafeated by id "+ msg.Sender);
				return true;

			default:
				System.out.println("no match");
				return false;
		}
	}

	@Override
	public String toString() { return "Player"; }
}
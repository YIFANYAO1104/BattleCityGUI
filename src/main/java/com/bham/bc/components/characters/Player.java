package com.bham.bc.components.characters;


import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.DIRECTION;
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

import java.util.List;

import static com.bham.bc.components.CenterController.backendServices;
import static java.lang.Math.signum;

public class Player extends Character implements TrackableCharacter {

	public static final String IMAGE_PATH = "file:src/main/resources/img/characters/player.png";
	public static final int WIDTH = 25;
	public static final int HEIGHT = 35;
	public static final int MAX_HP = 100;

	private int hp;
	private boolean stop;

	private SimpleDoubleProperty trackableX;
	private SimpleDoubleProperty trackableY;

	/**
	 * Constructs a player instance with default speed value set to 5
	 *
	 * @param x top left x coordinate of the player
	 * @param y top left y coordinate of the player
	 */
	public Player(double x, double y) {
		super(x, y, 5);
		hp = MAX_HP;
		stop = false;

		initTrackableCoordinates();
		entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
	}

	/**
	 * Gets the HP of the player
	 * @return integer representing current HP
	 */
	public int getHp() { return hp; }

	/**
	 * Increases HP for the player
	 * @param health amount by which the player's HP is increased
	 */
	public void increaseHP(int health) { hp = Math.min(hp + health, MAX_HP); }

	/**
	 * Handles pressed key
	 *
	 * <p>If one of the control keys are pressed, namely, W, A, S or D, a corresponding
	 * {@link com.bham.bc.entity.DIRECTION} is added to the directionSet. If the key F
	 * is pressed, then an appropriate bullet is fired</p>
	 *
	 * @param e key to handle
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getCode()) {
			case F: fire(); break;
			case W: directionSet.add(DIRECTION.U); break;
			case A: directionSet.add(DIRECTION.L); break;
			case S: directionSet.add(DIRECTION.D); break;
			case D: directionSet.add(DIRECTION.R); break;
		}
	}

	/**
	 * Handles released key
	 *
	 * <p>If one of the control keys are released, namely, W, A, S or D, a corresponding
	 * {@link com.bham.bc.entity.DIRECTION} is removed from the directionSet</p>
	 *
	 * @param e key to handle
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getCode()) {
			case W: directionSet.remove(DIRECTION.U); break;
			case A: directionSet.remove(DIRECTION.L); break;
			case S: directionSet.remove(DIRECTION.D); break;
			case D: directionSet.remove(DIRECTION.R); break;
		}
	}

	/**
	 * Shoots default bullet
	 *
	 * <p>This method creates a new instance of {@link com.bham.bc.components.armory.DefaultBullet}
	 * based on player's position and angle</p>
	 *
	 * @return instance of DefaultBullet
	 */
	public DefaultBullet fire() {
		double centerBulletX = x + entityImages[0].getWidth()/2;
		double centerBulletY = y - DefaultBullet.HEIGHT/2;

		Rotate rot = new Rotate(angle, x + entityImages[0].getWidth()/2, y + entityImages[0].getHeight()/2);
		Point2D newBul = rot.transform(centerBulletX, centerBulletY);

		double topLeftBulletX = newBul.getX() - DefaultBullet.WIDTH/2;
		double topLeftBulletY = newBul.getY() - DefaultBullet.HEIGHT/2;

		DefaultBullet m = new DefaultBullet(this.getID(), topLeftBulletX, topLeftBulletY, angle);
		backendServices.addBullet(m);
		return m;
	}

	/**
	 * Checks if the player intersects any of the enemies
	 * @param enemies list of all enemies in the game map
	 * @return true if the player intersects some enemy and false otherwise
	 */
	public boolean intersectsEnemies(List<Enemy> enemies) { return enemies.stream().anyMatch(this::intersects); }

	@Override
	public Ellipse getHitBox() {
		Point2D hitBoxOffset = new Point2D(0, 4);

		Ellipse hitBox = new Ellipse(x + WIDTH/2, y + HEIGHT/2, WIDTH/2-1, HEIGHT/2-3);
		hitBox.getTransforms().add(new Rotate(angle, hitBox.getCenterX(), hitBox.getCenterY()));
		hitBox.getTransforms().add(new Translate(hitBoxOffset.getX(), hitBoxOffset.getY()));

		return hitBox;
	}

	@Override
	protected void move() {
		if(!directionSet.isEmpty() && !stop) {
			x += Math.sin(Math.toRadians(angle)) * speed;
			y -= Math.cos(Math.toRadians(angle)) * speed;
		}
	}

	@Override
	public void update() {
		updateAngle();
		move();
		trackableX.set(this.x + WIDTH/2);
		trackableY.set(this.y + HEIGHT/2);
	}

	@Override
	public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

	@Override
	public void initTrackableCoordinates() {
		trackableX = new SimpleDoubleProperty(Constants.WINDOW_WIDTH/2);
		trackableY = new SimpleDoubleProperty(Constants.WINDOW_HEIGHT/2);
	}

	@Override
	public SimpleDoubleProperty getTrackableCoordinateX() { return trackableX; }

	@Override
	public SimpleDoubleProperty getTrackableCoordinateY() { return trackableY; }

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
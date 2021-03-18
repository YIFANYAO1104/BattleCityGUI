package com.bham.bc.components.characters;

import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.DIRECTION;
import com.bham.bc.utils.messaging.Telegram;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Represents the character controlled by the user
 */
public class Player extends Character {

	public static final String IMAGE_PATH = "file:src/main/resources/img/characters/player.png";
	public static final int WIDTH = 25;
	public static final int HEIGHT = 35;
	public static final int MAX_HP = 100;

	private double hp;

	public static final SimpleDoubleProperty TRACKABLE_X = new SimpleDoubleProperty(Constants.WINDOW_WIDTH/2);
	public static final SimpleDoubleProperty TRACKABLE_Y = new SimpleDoubleProperty(Constants.WINDOW_HEIGHT/2);

	/**
	 * Constructs a player instance with initial speed value set to 5
	 *
	 * @param x top left x coordinate of the player
	 * @param y top left y coordinate of the player
	 */
	public Player(double x, double y) {
		super(x, y, 5, MAX_HP, SIDE.ALLY);
		entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
	}

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
	 * TODO: generalize the method once weapon class is defined of more bullet types appear
	 *
	 * @return instance of DefaultBullet
	 */
	public DefaultBullet fire() {
		double centerBulletX = x + WIDTH/2;
		double centerBulletY = y - DefaultBullet.HEIGHT/2;

		Rotate rot = new Rotate(angle, x + WIDTH/2, y + HEIGHT/2);
		Point2D rotatedCenterXY = rot.transform(centerBulletX, centerBulletY);

		double topLeftBulletX = rotatedCenterXY.getX() - DefaultBullet.WIDTH/2;
		double topLeftBulletY = rotatedCenterXY.getY() - DefaultBullet.HEIGHT/2;

		DefaultBullet b = new DefaultBullet(topLeftBulletX, topLeftBulletY, angle, side);
		backendServices.addBullet(b);
		return b;
	}

	@Override
	public Ellipse getHitBox() {
		Point2D hitBoxOffset = new Point2D(0, 4);

		Ellipse hitBox = new Ellipse(x + WIDTH/2, y + HEIGHT/2, WIDTH/2-1, HEIGHT/2-3);
		hitBox.getTransforms().add(new Rotate(angle, hitBox.getCenterX(), hitBox.getCenterY()));
		hitBox.getTransforms().add(new Translate(hitBoxOffset.getX(), hitBoxOffset.getY()));

		return hitBox;
	}

	@Override
	public void update() {
		updateAngle();
		move();
		TRACKABLE_X.set(x + WIDTH/2);
		TRACKABLE_Y.set(y + HEIGHT/2);
	}

	@Override
	public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

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
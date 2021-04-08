package com.bham.bc.components.characters;

import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.ExplosiveBullet;
import com.bham.bc.components.shooting.Gun;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.ai.navigation.NavigationService;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bc.entity.ai.navigation.impl.PathPlanner;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.GeometryEnhanced;
import com.bham.bc.utils.messaging.Telegram;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.EnumSet;
import java.util.Optional;

import java.util.List;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.GeometryEnhanced.isZero;

/**
 * Represents a character controlled by the user
 */
public class Player extends GameCharacter {

	public static final String IMAGE_PATH = "file:src/main/resources/img/characters/player.png";
	public static final String IMAGE_PATH2 ="file:src/main/resources/img/characters/state1.png";
	public static final int SIZE = 25;
	public static final double HP = 100;
	public static final double SPEED = 5;

	public static final SimpleDoubleProperty TRACKABLE_X = new SimpleDoubleProperty(Constants.WINDOW_WIDTH/2.0);
	public static final SimpleDoubleProperty TRACKABLE_Y = new SimpleDoubleProperty(Constants.WINDOW_HEIGHT/2.0);

	private final EnumSet<Direction> DIRECTION_SET;
	private final Gun GUN;

	// TODO: remove, player doesn't need
	private NavigationService navigationService;

	/**
	 * Constructs a player instance with directionSet initialized to empty
	 *
	 * @param x top left x coordinate of the player
	 * @param y top left y coordinate of the player
	 */
	public Player(double x, double y) {
		super(x, y, SPEED, HP, Side.ALLY);
		entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
		DIRECTION_SET = EnumSet.noneOf(Direction.class);
		GUN = new Gun(this, BulletType.DEFAULT);

		navigationService = new PathPlanner(this, backendServices.getGraph());
	}

	// TEMPORARY -------------------------------------------
	// CAN ALSO BE TEMPORARY IF NOT DOCUMENTED

	public void bomb() {
		Point2D center = getPosition().add(getRadius().multiply(0.5));
		ExplosiveBullet b = new ExplosiveBullet(center.getX(), center.getY(), heading, side);
		backendServices.addBullet(b);
	}

	public void toState1(){
		this.entityImages =  new Image[] { new Image(IMAGE_PATH2, SIZE, 0, true, false) };

	}
	public List<Shape> getSmoothingBoxes(){
		return navigationService.getSmoothingBoxes();
	}
	// -----------------------------------------------------------

	/**
	 * Updates angle at which the player is facing
	 *
	 * <p>This method goes through every direction in the directionSet, coverts them to basis vectors,
	 * adds them up to get a final direction vector and calculates the angle between it and (0, 1)</p>
	 *
	 * <p><b>Note:</b> the basis vector which is used for angle calculation must be (0, 1) as this is the
	 * way the character in the image is facing (upwards)</p>
	 */
	private void updateAngle() {
		// Sum up direction basis vectors to get a final direction vector
		Optional<Point2D> directionPoint = DIRECTION_SET.stream().map(Direction::toPoint).map(p -> p.multiply(TRAPPED ? -1 : 1)).reduce(Point2D::add);

		// Convert that direction vector to angle
		directionPoint.ifPresent(p -> {
			Point2D p1 = new Point2D(p.getX(), -p.getY());

			velocity = p1.multiply(maxSpeed);
			//Truncate
			velocity = GeometryEnhanced.truncate(velocity, maxSpeed);
			if (!isZero(velocity)) {
				heading = velocity.normalize();
			}
		});
	}

	@Override
	protected void destroy() { }

	/**
	 * Handles pressed key
	 *
	 * <p>If one of the control keys are pressed, namely, W, A, S or D, a corresponding
	 * {@link Direction} is added to the directionSet. If the key F
	 * is pressed, then an appropriate bullet is fired</p>
	 *
	 * @param e key to handle
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getCode()){
			case F: fire(); break;
			case B: bomb(); break;
			case W: DIRECTION_SET.add(Direction.U); break;
			case A: DIRECTION_SET.add(Direction.L); break;
			case S: DIRECTION_SET.add(Direction.D); break;
			case D: DIRECTION_SET.add(Direction.R); break;
			case P: testDijistra();break;
		}
	}
	private void testDijistra(){
		navigationService.createRequest(ItemType.HEALTH);
		navigationService.setExpandCondition(new ExpandPolicies.NoSoft());
		System.out.println(navigationService.peekRequestStatus());
		navigationService.getPath();
	}

	/**
	 * Handles released key
	 *
	 * <p>If one of the control keys are released, namely, W, A, S or D, a corresponding
	 * {@link Direction} is removed from the directionSet</p>
	 *
	 * @param e key to handle
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getCode()) {
			case W: DIRECTION_SET.remove(Direction.U); break;
			case A: DIRECTION_SET.remove(Direction.L); break;
			case S: DIRECTION_SET.remove(Direction.D); break;
			case D: DIRECTION_SET.remove(Direction.R); break;
		}
	}

	/**
	 * Shoots a default bullet (or multiple)
	 *
	 * <p>This method creates a new instance of {@link com.bham.bc.components.shooting.DefaultBullet}
	 * based on player's position and angle</p>
	 */
	public void fire() {
		GUN.shoot();
		if(tripleTicks != 0) GUN.shoot(-45, 45);
	}

	@Override
	public void move() {
		if(!DIRECTION_SET.isEmpty()) {
			x += velocity.getX();
			y += velocity.getY();
		}
	}

	@Override
	public Circle getHitBox() {
		return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0);
	}

	@Override
	public void update() {
		updateTriggers();
		if (freezeTicks == 0) {
			updateAngle();
			move();
		}
		TRACKABLE_X.set(getCenterPosition().getX());
		TRACKABLE_Y.set(getCenterPosition().getY());
	}


	@Override
	public void render(GraphicsContext gc) {
		if (navigationService!=null) navigationService.render(gc);
		drawRotatedImage(gc, entityImages[0], getAngle());
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
	public String toString() {
		return "Player";
	}
}

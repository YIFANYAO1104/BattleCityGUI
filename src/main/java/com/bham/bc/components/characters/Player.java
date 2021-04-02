package com.bham.bc.components.characters;

import com.bham.bc.components.armory.BulletType;
import com.bham.bc.components.armory.ExplosiveBullet;
import com.bham.bc.components.armory.Gun;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.components.environment.navigation.NavigationService;
import com.bham.bc.components.environment.navigation.SearchStatus;
import com.bham.bc.components.environment.navigation.impl.PathPlanner;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.GeometryEnhanced;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.messaging.Telegram;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

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

	private EnumSet<Direction> directionSet;

	// TODO: remove, player doesn't need
	private NavigationService navigationService;
	private Gun gun;

	/**
	 * Constructs a player instance with directionSet initialized to empty
	 *
	 * @param x top left x coordinate of the player
	 * @param y top left y coordinate of the player
	 */
	public Player(double x, double y) {
		super(x, y, SPEED, HP, Side.ALLY);
		entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
		directionSet = EnumSet.noneOf(Direction.class);
		gun = new Gun(this, BulletType.DEFAULT);
	}

	// TEMPORARY -------------------------------------------
	// CAN ALSO BE TEMPORARY IF NOT DOCUMENTED
	public void initNavigationService(SparseGraph sg){
		navigationService = new PathPlanner(this,sg);
	}

	public void createNewRequestItem() {
		if(navigationService.createRequest(ItemType.health)==true){
			if(navigationService.peekRequestStatus()== SearchStatus.target_found){
				navigationService.getPath();
			} else {
				System.out.println("target not found");
			}
		} else {
			System.out.println("no closest node around player/target");
		}
	}

	public void createNewRequestAStar() {
		if(navigationService.createRequest(new Point2D(440,550))==true){
			if(navigationService.peekRequestStatus()== SearchStatus.target_found){
				navigationService.getPath();
			} else {
				System.out.println("target not found");
			}
		} else {
			System.out.println("no closest node around player/target");
		}
	}

	public void bomb() {
		Point2D center = getPosition().add(getRadius().multiply(0.5));
		ExplosiveBullet b = new ExplosiveBullet(center.getX(), center.getY(), angle, side);
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
		//sum up directions
		Optional<Point2D> directionPoint = directionSet.stream().map(Direction::toPoint).reduce(Point2D::add);
		//convert to angle
		directionPoint.ifPresent(p -> {
			Point2D p1 = new Point2D(p.getX(), -p.getY());
			Point2D force = sb.seek(getCenterPosition().add(p1));
//			Point2D acceleration = force.multiply(1./5);
//			velocity = velocity.add(acceleration);
			//we want an instant change on speed rather than accumulation
			velocity = velocity.add(force);
			//Truncate
			velocity = GeometryEnhanced.truncate(velocity, maxSpeed);
			if (!isZero(velocity)) {
				heading = velocity.normalize();
			}
		});
	}
//	public void updateAngle() {
//		Optional<Point2D> directionPoint = directionSet.stream().map(Direction::toPoint).map(p -> p.multiply(TRAPPED ? -1 : 1)).reduce(Point2D::add);
//		directionPoint.ifPresent(p -> { if(p.getX() != 0 || p.getY() != 0) angle = p.angle(0, 1) * (p.getX() > 0 ? 1 : -1); });
//	}

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
			case P: this.createNewRequestAStar(); break;		// TODO: remove
			case O: this.createNewRequestItem(); break;
			case W: directionSet.add(Direction.U); break;
			case A: directionSet.add(Direction.L); break;
			case S: directionSet.add(Direction.D); break;
			case D: directionSet.add(Direction.R); break;
		}
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
			case W: directionSet.remove(Direction.U); break;
			case A: directionSet.remove(Direction.L); break;
			case S: directionSet.remove(Direction.D); break;
			case D: directionSet.remove(Direction.R); break;
		}
	}

	/**
	 * Shoots a default bullet (or multiple)
	 *
	 * <p>This method creates a new instance of {@link com.bham.bc.components.armory.DefaultBullet}
	 * based on player's position and angle</p>
	 */
	public void fire() {
		gun.shoot();
		if(tripleTicks != 0) gun.shoot(-45, 45);
	}

	@Override
	public void move() {
		if(!directionSet.isEmpty()) {
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
		drawRotatedImage(gc, entityImages[0], getAntiAngleY());
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

package com.bham.bc.components.characters;

import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.components.armory.ExplosiveBullet;
import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.components.environment.navigation.NavigationService;
import com.bham.bc.components.environment.navigation.SearchStatus;
import com.bham.bc.components.environment.navigation.impl.PathPlanner;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.graph.SparseGraph;
import com.bham.bc.utils.messaging.Telegram;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

import java.util.EnumSet;
import java.util.Optional;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Represents a character controlled by the user
 */
public class Player extends GameCharacter {

	public static final String IMAGE_PATH = "file:src/main/resources/img/characters/player.png";
	public static final int SIZE = 25;
	public static final double HP = 100;
	public static final double SPEED = 5;


	public static final SimpleDoubleProperty TRACKABLE_X = new SimpleDoubleProperty(Constants.WINDOW_WIDTH/2.0);
	public static final SimpleDoubleProperty TRACKABLE_Y = new SimpleDoubleProperty(Constants.WINDOW_HEIGHT/2.0);

	private EnumSet<Direction> directionSet;

	// TODO: remove, player doesn't need
	private NavigationService navigationService;

	/**
	 * Constructs a player instance with directionSet initialized to empty
	 *
	 * @param x top left x coordinate of the player
	 * @param y top left y coordinate of the player
	 */
	public Player(double x, double y) {
		super(x, y, SPEED, HP, SIDE.ALLY);
		entityImages = new Image[] { new Image(IMAGE_PATH, SIZE, 0, true, false) };
		directionSet = EnumSet.noneOf(Direction.class);
	}

	// TEMPORARY -------------------------------------------
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
		Optional<Point2D> directionPoint = directionSet.stream().map(Direction::toPoint).reduce(Point2D::add);
		directionPoint.ifPresent(p -> { if(p.getX() != 0 || p.getY() != 0) angle = p.angle(0, 1) * (p.getX() > 0 ? 1 : -1); });
	}

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
		switch (e.getCode()) {
			case F: fire(); break;
			case B: bomb(); break;
			case P:this.createNewRequestAStar();break;		// TODO: remove
			case O:this.createNewRequestItem();break;		// TODO: remove
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
	 * Shoots default bullet
	 *
	 * <p>This method creates a new instance of {@link com.bham.bc.components.armory.DefaultBullet}
	 * based on player's position and angle</p>
	 *
	 * TODO: generalize the method once weapon class is defined of more bullet types appear
	 *
	 * @return instance of DefaultBullet
	 */
	public void fire() {
		double centerBulletX = x + getRadius().getX()/2.0;
		double centerBulletY = y - DefaultBullet.HEIGHT/2.0;

		Rotate rot = new Rotate(angle, getCenterPosition().getX(), getCenterPosition().getY());
		Point2D rotatedCenterXY = rot.transform(centerBulletX, centerBulletY);

		double topLeftBulletX = rotatedCenterXY.getX() - DefaultBullet.WIDTH/2.0;
		double topLeftBulletY = rotatedCenterXY.getY() - DefaultBullet.HEIGHT/2.0;

		DefaultBullet b = new DefaultBullet(topLeftBulletX, topLeftBulletY, angle, side);
		backendServices.addBullet(b);
	}

	public void bomb() {
		Point2D center = getPosition().add(getRadius().multiply(0.5));
		ExplosiveBullet b = new ExplosiveBullet(center.getX(), center.getY(), angle, side);
		backendServices.addBullet(b);
	}

	@Override
	public void move() {
		if(!directionSet.isEmpty()) {
			x += Math.sin(Math.toRadians(angle)) * speed;
			y -= Math.cos(Math.toRadians(angle)) * speed;
		}
	}

	@Override
	public void destroy() {}

	@Override
	public Circle getHitBox() { return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0); }

	@Override
	public void update() {
		updateAngle();
		move();
		TRACKABLE_X.set(getCenterPosition().getX());
		TRACKABLE_Y.set(getCenterPosition().getY());
	}

	@Override
	public void render(GraphicsContext gc) {
		if (navigationService!=null) navigationService.render(gc);
		drawRotatedImage(gc, entityImages[0], angle); }

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
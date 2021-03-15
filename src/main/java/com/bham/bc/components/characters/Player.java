package com.bham.bc.components.characters;


import com.bham.bc.components.armory.Bullets01;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;

import static com.bham.bc.components.CenterController.backendServices;
import static java.lang.Double.NaN;
import static java.lang.Math.signum;

public class Player extends Character implements TrackableCharacter {

	public static final int MAX_HP = 100;
	private Direction Kdirection = Direction.U;
	private int hp;

	private EnumSet<Direction> directionSet;
	private double angle;

	private SimpleDoubleProperty trackableX;
	private SimpleDoubleProperty trackableY;




	/**
	 * Constructor of Player Tank,also indicates which Player it is creating
	 * @param x
	 * @param y
	 */
	public Player(int x, int y) {
		super(5,5, x,y,32,32, Direction.U);
		directionSet = EnumSet.noneOf(Direction.class);
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
	 * Draws an image on a graphics context.
	 *
	 * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
	 * (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
	 *
	 * @param gc the graphics context the image is to be drawn on.
	 * @param angle the angle of rotation.
	 * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
	 * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
	 *
	 * @see <a href="https://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas">stackoverflow.com</a>
	 */
	void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
		gc.save();
		Rotate r = new Rotate(angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		gc.drawImage(image, tlpx, tlpy);
		gc.restore();
	}

	@Override
	public Ellipse getHitBox() {
		Ellipse el = new Ellipse(x + entityImages[0].getWidth()/2, y + entityImages[0].getHeight()/2, 12, 14);
		el.getTransforms().addAll(new Rotate(angle, el.getCenterX(), el.getCenterY()), new Translate(0, 4));
		return el;
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
		drawRotatedImage(gc, entityImages[0], angle, x,y);
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
	public Bullets01 fire() {
		if (!isAlive) return null;
		int x=0;
		int y=0;
		switch (Kdirection) {
			case D:
				x = this.x + this.width / 2 - Bullets01.width / 2;
				y = this.y + this.length;
				break;

			case U:
				x = this.x + this.width / 2 - Bullets01.width / 2;
				y = this.y - Bullets01.length;
				break;
			case L:
				x = this.x - Bullets01.width;
				y = this.y + this.length / 2 - Bullets01.length / 2;
				break;

			case R:
				x = this.x + this.width;
				y = this.y + this.length / 2 - Bullets01.length / 2;
				break;
		}
		Bullets01 m = new Bullets01(this.ID(),x, y, Kdirection);
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
			x += Math.sin(Math.toRadians(angle)) * speedX;
			y -= Math.cos(Math.toRadians(angle)) * speedX;
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
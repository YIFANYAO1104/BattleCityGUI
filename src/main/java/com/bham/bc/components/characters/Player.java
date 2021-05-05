package com.bham.bc.components.characters;

import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.shooting.*;
import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.ExplosiveBullet;
import com.bham.bc.components.shooting.Gun;
import com.bham.bc.components.triggers.Trigger;
import com.bham.bc.components.triggers.effects.RingExplosion;
import com.bham.bc.entity.ai.navigation.NavigationService;
import com.bham.bc.entity.ai.navigation.algorithms.policies.ExpandPolicies;
import com.bham.bc.entity.ai.navigation.impl.PathPlanner;
import com.bham.bc.utils.GeometryEnhanced;
import com.bham.bc.view.GameSession;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.EnumSet;
import java.util.Optional;

import java.util.List;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.utils.GeometryEnhanced.isZero;

/**
 * Represents a character controlled by the user
 */
public class Player extends GameCharacter {

	public static final String IMAGE_PATH = "img/characters/player.png";
	public static final String IMAGE_PATH2 ="img/characters/state1.png";
	public static final int SIZE = 25;
	public static double HP = 100;
	public static final double SPEED = 5;

	public static final DoubleProperty TRACKABLE_X = new SimpleDoubleProperty(GameSession.WIDTH/2.0);
	public static final DoubleProperty TRACKABLE_Y = new SimpleDoubleProperty(GameSession.HEIGHT/2.0);

	private final EnumSet<Direction> DIRECTION_SET;
	private final Gun GUN;

	// TODO: remove, player doesn't need
	private NavigationService navigationService;
	private boolean bomb;

	/**
	 * Used For testing
	 */
	public void testDIRECTION_SET(){
		this.DIRECTION_SET.add(Direction.L);
	}
	public void testDIRECTION_SET1(){
		this.DIRECTION_SET.clear();
	}
	public Gun testGun(){
		return this.GUN;
	}
	public void testFire() {
		fire();
	}
	public boolean testBomb() {
		boolean b = bomb;
		bomb();
		return b;
	}
	/**
	 * Constructs a player instance with directionSet initialized to empty
	 *
	 * @param x top left x coordinate of the player
	 * @param y top left y coordinate of the player
	 */
	public Player(double x, double y) {
		super(x, y, SPEED, HP, Side.ALLY);
		try{
		entityImages = new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH), SIZE, 0, true, false) };
		}catch (IllegalArgumentException | NullPointerException e){
			e.printStackTrace();
		}
		DIRECTION_SET = EnumSet.noneOf(Direction.class);
		GUN = new Gun(this, BulletType.DEFAULT,LaserType.Default);

		navigationService = new PathPlanner(this, services.getGraph());
		steering.setKeys(true);
	}

	@Override
	public NavigationService getNavigationService() {
		return this.navigationService;
	}

	/**
	 * Assign activation time of Triple Bullet trigger
	 * @param numTicks activation time in ticks
	 */
    public void activateTriple(int numTicks) {
        isTriple = numTicks;
    }

    /**
	 * Assign activation time of Freeze trigger
	 * @param numTicks activation time in ticks
	 */
    public void activateFreeze(int numTicks) {
        isFreeze = numTicks;
    }

    /**
	 * Assign activation time of Immune trigger
	 * @param numTicks activation time in ticks
	 */
    public void activateImmune(int numTicks) {
        isImmune = numTicks;
    }
    
    /**
	 * Assign activation time of Inverse Trap trigger
	 * @param numTicks activation time in ticks
	 */
    public void activateInverse(int numTicks) {
		isInverse = numTicks;
    }
    
    /**
	 * Assign activation time of Bomb trigger
	 */
    public void activateBomb() {
    	bomb = true;
    }

    /**
	 * Update trigger(s) activation time
	 */
    protected void updateTriggers() {
        if(isImmune!=0) --isImmune;
        if(isFreeze!=0) --isFreeze;
        if(isTriple!=0) --isTriple;
        if(isInverse!=0) --isInverse;
    }

    public void teleport(){
    	x = GameMap.getWidth()/2.0;
		y = GameMap.getHeight()/2.0;
    }
    
	// TEMPORARY -------------------------------------------
	// CAN ALSO BE TEMPORARY IF NOT DOCUMENTED
	// TODO: remove, this is another example of bomb()
	public void ring() {
		Trigger explosion = new RingExplosion(getCenterPosition(), 50, side);
		services.addTrigger(explosion);
	}

	public void bomb() {
		if (!bomb) return;
		Point2D center = getPosition().add(getSize().multiply(0.5));
		ExplosiveBullet b = new ExplosiveBullet(center.getX(), center.getY(), heading, side);
		services.addBullet(b);
		bomb = false;
	}
	
	public void toState1(){
    	try{
		this.entityImages =  new Image[] { new Image(getClass().getClassLoader().getResourceAsStream(IMAGE_PATH2), SIZE, 0, true, false) };
		}catch (IllegalArgumentException | NullPointerException e){
			e.printStackTrace();
		}
	}
	public List<Shape> getSmoothingBoxes(){
		return navigationService.getSmoothingBoxes();
	}
	private void testDijistra(){
		navigationService.setExpandCondition(new ExpandPolicies.NoShoot());
		System.out.println(navigationService.createRequest(new Point2D(850,758)));;
		System.out.println(navigationService.peekRequestStatus());
		navigationService.getPath();
	}
	// -----------------------------------------------------------

	/**
	 * Updates angle at which the player is facing
	 *
	 * <p>This method goes through every direction in the directionSet, coverts them to basis vectors,
	 * adds them up to get a final direction vector and normalizes it to set the heading param.</p>
	 */
	private void updateAngle() {
		Optional<Point2D> directionPoint = DIRECTION_SET.stream().map(Direction::toPoint).map(p -> p.multiply(isInverse!=0 ? -1 : 1)).reduce(Point2D::add);

		directionPoint.ifPresent(p -> {
			if(!isZero(p)) heading = p.normalize();
			velocity = p.normalize().multiply(velocity.magnitude());
		});
	}
	
	public void gunChange(BulletType bullet){
		this.GUN.setBulletType(bullet);
	}
	public void laserChange(LaserType laser){
		this.GUN.setLaserType(laser);
	}
	
	/**
	 * Shoots a default bullet (or multiple)
	 *
	 * <p>This method creates new instance(-s) of {@link com.bham.bc.components.shooting.DefaultBullet}
	 * based on player's position and angle.</p>
	 */
	private void fire() {
		if(isTriple == 0) GUN.shoot();
		else GUN.shoot(-45, 0, 45);
	}
	private  void FireLaser(){
		GUN.shootLaser();
	}

	/**
	 * Handles pressed key
	 *
	 * <p>If one of the control keys are pressed, namely, W, A, S or D, a corresponding
	 * {@link Direction} is added to the directionSet. If the key F is pressed, then an
	 * appropriate bullet is fired</p>
	 *
	 * @param e key to handle
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getCode()){
			case F: fire(); break;
			case R: FireLaser(); break;
			case B: bomb(); break;
			case G: ring(); break;
			case W: DIRECTION_SET.add(Direction.U); break;
			case A: DIRECTION_SET.add(Direction.L); break;
			case S: DIRECTION_SET.add(Direction.D); break;
			case D: DIRECTION_SET.add(Direction.R); break;
			case H: testDijistra();		System.out.println(getCenterPosition());break;
			case K: targetingSystem.statatat();break;
			case L: services.getMapDivision().cleanHB();break;
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
			case W: DIRECTION_SET.remove(Direction.U); break;
			case A: DIRECTION_SET.remove(Direction.L); break;
			case S: DIRECTION_SET.remove(Direction.D); break;
			case D: DIRECTION_SET.remove(Direction.R); break;
		}
	}

	/**
	 * Gets the number of direction keys currently pressed (i.e., from a set of WASD)
	 * @return size of the direction set
	 */
	public int getNumDirKeysPressed() {
		return DIRECTION_SET.size();
	}

	@Override
	public void move() {
		Point2D force = steering.calculate();
		acceleration = steering.validateAcceleration(force.multiply(1/mass), !DIRECTION_SET.isEmpty());
		velocity = velocity.add(acceleration);

		x += velocity.getX();
		y += velocity.getY();

		Point2D pos = GeometryEnhanced.bound(new Point2D(x,y),getSize(), GameMap.getWidth(),GameMap.getHeight());
		x = pos.getX();
		y = pos.getY();
	}

	@Override
	public Circle getHitBox() {
		return new Circle(getCenterPosition().getX(), getCenterPosition().getY(), SIZE/2.0);
	}

	@Override
	public void update() {
		updateTriggers();
		if (isFreeze == 0) {
			updateAngle();
			move();
		}
		TRACKABLE_X.set(getCenterPosition().getX());
		TRACKABLE_Y.set(getCenterPosition().getY());
	}

//	@Override
//	public void render(GraphicsContext gc) {
//		if(navigationService!=null) navigationService.render(gc);
//		drawRotatedImage(gc, entityImages[0], getAngle());
//	}

	@Override
	protected void destroy() {
//		entityManager.removeEntity(this);
//		exists = false;
//
//		Trigger dissolve = new Dissolve(getPosition(), entityImages[0], getAngle());
//		services.addTrigger(dissolve);
	}

	@Override
	public String toString() {
		return "Player";
	}
}

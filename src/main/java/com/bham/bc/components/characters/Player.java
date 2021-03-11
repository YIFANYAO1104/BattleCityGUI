package com.bham.bc.components.characters;


import com.bham.bc.components.armory.Bullets01;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.environment.triggers.Weapon;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static com.bham.bc.components.CenterController.backendServices;

public class Player extends Character implements TrackableCharacter {

	public static int count = 0;
	/**
	 * the STABLE direction of the Player Tank
	 * It's value should not be 'stop'
	 * Mainly for renderer
	 */
	private Direction Kdirection = Direction.U;
	/**
	 *  initialize the health of tank to 200 hp */
	private int hp = 200;
	/**
	 * The direction that will be set by KeyAction
	 */
	private boolean bL = false, bU = false, bR = false, bD = false;

	private SimpleDoubleProperty trackableX;
	private SimpleDoubleProperty trackableY;

	//-----------------------------------------------------------------//
	private Weapon currWeapon = null;



	/**
	 * Constructor of Player Tank,also indicates which Player it is creating
	 * @param x
	 * @param y
	 * @param dir
	 */
	public Player(int x, int y, Direction dir) {
		super(1,1, x,y,32,32,dir);
		initImages();
		initTrackableCoordinates();
	}

	/**
	 * List of images should be replaced later
	 */
	private void initImages() {
		entityImages = new Image[] {
				new Image("file:src/main/resources/img/HtankD.gif"),
				new Image("file:src/main/resources/img/HtankU.gif"),
				new Image("file:src/main/resources/img/HtankL.gif"),
				new Image("file:src/main/resources/img/HtankR.gif"),
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

		//It is covered by home currently. So you could not see it.
		renderBloodbBar(gc);

		switch (Kdirection) {
			case D:
				gc.drawImage(entityImages[0], x, y);
				break;
			case U:
				gc.drawImage(entityImages[1], x, y);
				break;
			case L:
				gc.drawImage(entityImages[2], x, y);
				break;
			case R:
				gc.drawImage(entityImages[3], x, y);
				break;
		}
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getCode()) {
			case D: bR = true; break;
			case A: bL = true; break;
			case W: bU = true; break;
			case S: bD = true; break;
		}
		decideDirection();
	}
	/**
	 * Determine the direction Of Player Tank
	 */
	void decideDirection() {
		if (!bL && !bU && bR && !bD) 
			direction = Direction.R;

		else if (bL && !bU && !bR && !bD) 
			direction = Direction.L;

		else if (!bL && bU && !bR && !bD) 
			direction = Direction.U;

		else if (!bL && !bU && !bR && bD) 
			direction = Direction.D;

		else if (!bL && !bU && !bR && !bD)
			direction = Direction.STOP; 
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getCode()) {
			case F: fire(); break;
			case D: bR = false; break;
			case A: bL = false; break;
			case W: bU = false; break;
			case S: bD = false; break;
		}
		decideDirection(); 
	}
	/**This method create the firing bullet01
	 * Use Kdirection to set the direction of Bullet
	 * Add bullet to list of bullets
	 */
	public Bullets01 fire() {
		if (!isAlive)
			return null;
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
	public void update() {
		move();
		trackableX.set(this.x + this.width/2);
		trackableY.set(this.y + this.length/2);
	}
	/**
	 * Method to implements the movement of Player tanks
	 * Record the current coordinate as old coordinates and move to the specific direction
	 * Note: Always check the constraints of boundary:
	 * x AND y coordinate of Player tank can not go outside of the frame
	 */
	@Override
	protected void move() {

		this.oldX = x;
		this.oldY = y;

		switch (direction) {
			case L:
				x -= speedX;
				break;
			case U:
				y -= speedY;
				break;
			case R:
				x += speedX;
				break;
			case D:
				y += speedY;
				break;
			case STOP:
				break;
		}

		if (this.direction != Direction.STOP) {
			this.Kdirection = this.direction;
		}



		//guarantee the tank is in Frame
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x + this.width > Constants.MAP_WIDTH) x = Constants.MAP_WIDTH - this.width;
		if (y + this.length > Constants.MAP_HEIGHT) y = Constants.MAP_HEIGHT - this.length;
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
	public String toString() {
		return "Player Type";
	}

	@Override
	public void increaseHealth(int health){
		if(this.hp +health<=200){
			this.hp = this.hp +health;
		} else{
			this.hp = 200;
		}
	}

	@Override
	public void switchWeapon(Weapon w) {

	}


	//----------------------------------------------------------------------------------------//

}
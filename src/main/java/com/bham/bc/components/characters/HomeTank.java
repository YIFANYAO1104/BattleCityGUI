package com.bham.bc.components.characters;


import com.bham.bc.components.armory.Bullets01;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.utils.Constants;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.characters.Tank;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static com.bham.bc.components.CenterController.centerController;

public class HomeTank extends Tank {

	public static int count = 0;
	/**
	 * the STABLE direction of the Player Tank
	 * It's value should not be 'stop'
	 * Mainly for renderer
	 */
	private Direction Kdirection = Direction.U;
	/**
	 *  initialize the health of tank to 200 hp */
	private int life = 200;
	/**
	 * The direction that will be set by KeyAction
	 */
	private boolean bL = false, bU = false, bR = false, bD = false;



	/**
	 * Constructor of Player Tank,also indicates which Player it is creating
	 * @param x
	 * @param y
	 * @param dir
	 */
	public HomeTank(int x, int y, Direction dir) {
		super(1,1, x,y,35,35,dir);
		initImages();
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
		int w = width * life / 200;
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

		if (!live) return;

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
		if (!live)
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
		centerController.addBullet(m);
		return m;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
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
				if (this.live && t.isLive()
						&& this.isIntersect(t)) {
					this.changToOldDir();
					t.changToOldDir();
					return true;
				}
			}
		}
		return false;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	@Override
	public void update() {
		move();
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
		if (y < 40) y = 40;
		if (x + this.width > Constants.WINDOW_WIDTH) x = Constants.WINDOW_WIDTH - this.width;
		if (y + this.length > Constants.WINDOW_HEIGHT) y = Constants.WINDOW_HEIGHT - this.length;
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
		return "HomeTank";
	}

	@Override
	public void increaseHealth(int health){
		if(this.life+health<=200){
			this.life = this.life+health;
		} else{
			this.life = 200;
		}
	}
}
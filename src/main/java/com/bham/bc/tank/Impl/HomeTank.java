package com.bham.bc.tank.Impl;


import com.bham.bc.*;
import com.bham.bc.armory.Impl.Bullets01;
import com.bham.bc.common.Constants;
import com.bham.bc.common.Direction;
import com.bham.bc.common.Messaging.Telegram;
import com.bham.bc.common.MovingEntity;
import com.bham.bc.tank.Tank;

import java.awt.*;
import java.awt.event.KeyEvent;

import static com.bham.bc.CenterController.centerController;

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
		super(6,6,
				x,y,
				35,35,
				dir);
		initImages();
	}

	/**
	 * List of images should be replaced later
	 */
	private void initImages() {
		entityImags = new Image[] {
				tk.getImage(BombTank.class.getResource("/Images/HtankD.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankU.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankL.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankR.gif")),
		};
	}


	/**
	 * Draw Blood Bar
	 * @param g
	 */
	private void renderBloodbBar(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.drawRect(375, 585, width, 10);
		int w = width * life / 200;
		g.fillRect(375, 585, w, 10);
		g.setColor(c);
	}

	/**
	 * Render Method
	 * Use directions to determine the image of Player Tank
	 * @param g
	 */
	@Override
	public void render(Graphics g) {

		if (!live) return;

		//It is covered by home currently. So you could not see it.
		renderBloodbBar(g);

		switch (Kdirection) {
			case D:
				g.drawImage(entityImags[0], x, y, null);
				break;
			case U:
				g.drawImage(entityImags[1], x, y, null);
				break;
			case L:
				g.drawImage(entityImags[2], x, y, null);
				break;
			case R:
				g.drawImage(entityImags[3], x, y, null);
				break;
		}
	}

	public void keyPressed(KeyEvent e) {  
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_D:
			bR = true;
			break;
			
		case KeyEvent.VK_A:
			bL = true;
			break;
		
		case KeyEvent.VK_W:  
			bU = true;
			break;
		
		case KeyEvent.VK_S:
			bD = true;
			break;
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
		int key = e.getKeyCode();
		switch (key) {
		
		case KeyEvent.VK_F:
			fire();
			break;
			
		case KeyEvent.VK_D:
			bR = false;
			break;
		
		case KeyEvent.VK_A:
			bL = false;
			break;
		
		case KeyEvent.VK_W:
			bU = false;
			break;
		
		case KeyEvent.VK_S:
			bD = false;
			break;
			

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

	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
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
						&& this.getRect().intersects(t.getRect())) {
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
		if (x + this.width > Constants.WindowWidth) x = Constants.WindowWidth - this.width;
		if (y + this.length > Constants.WindowHeight) y = Constants.WindowHeight - this.length;
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
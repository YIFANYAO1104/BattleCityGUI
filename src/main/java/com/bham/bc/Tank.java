package main.java.com.bham.bc;


import main.java.com.bham.bc.armory.Bullets01;

import main.java.com.bham.bc.armory.Bullets02;
import main.java.com.bham.bc.armory.Tank_Bullet;
import main.java.com.bham.bc.environment.CommonWall;
import main.java.com.bham.bc.environment.Home;
import main.java.com.bham.bc.environment.MetalWall;
import main.java.com.bham.bc.environment.River;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tank extends MovingEntity {
	/**
	 * the velocity of moving object( speed x in horizontal and speed y in vertical)
	 */
	public static int speedX = 6, speedY =6;
	/**
	 *
	 */
	public static int count = 0;

	/**
	 *  the length and width of the moving object
	 */
	public static final int width = 35, length = 35;
	/**
	 * the moving direction of the moving object
	 */
	private Direction direction = Direction.STOP;

	private Direction Kdirection = Direction.U;
	/*initialize a user tank called tc */
	tankGame tc;
	/*initiliaze the number of players to 0 */
	private int player=0;
	private boolean good;
	/**
	 * the coordinate of the current position of the moving object
	 */
	private int x, y;
	/**
	 *  the  coordinate to record the previous location of the tank
	 */
	private int oldX, oldY;

	/**
	 * the status to check if the object is alive or dead
	 */
	private boolean live = true;
	/**
	 *  initialize the health of tank to 200 hp */
	private int life = 200;

	private int rate=1;
	private static Random r = new Random();
	/**
	 * generate an action of random steps the tank may take,
	for example,if step==5, means the tank moving forward 5 steps
	 */
	private int step = r.nextInt(10)+5 ;
	/**
	 * The direction that will be set by KeyAction
	 */
	private boolean bL = false, bU = false, bR = false, bD = false;
	
	/**
	 * It is in java.awt package */
	/**
	 * All java.awt package component need to be replaced using javafx.package
	 */
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	/**
	 * A list of images of tank,should be replaced later or using javafx to draw the tanks
	 */
	private static Image[] tankImags = null; 
	static {
		tankImags = new Image[] {
				tk.getImage(BombTank.class.getResource("/Images/tankD.gif")),
				tk.getImage(BombTank.class.getResource("/Images/tankU.gif")),
				tk.getImage(BombTank.class.getResource("/Images/tankL.gif")),
				tk.getImage(BombTank.class.getResource("/Images/tankR.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankD.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankU.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankL.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankR.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankD2.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankU2.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankL2.gif")),
				tk.getImage(BombTank.class.getResource("/Images/HtankR2.gif")),
				};

	}

	/**
	 * the constructor of a tank, using the coordinate to indication the position of tank initially,
	 * 	and using the initial status good
	 */
	public Tank(int x, int y, boolean good) {
		super();
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}

	/**
	 *  a constructor of player tank, using additional parameter direction, tankclient tc and number of players
	to create a user-controlled tank */
	/**
	 * a different constructor from machine tank */
	public Tank(int x, int y, boolean good, Direction dir, tankGame tc, int player) {
		this(x, y, good);
		this.direction = dir;
		this.tc = tc;
		this.player=player;
	}
	/**the method to draw all kinds of tanks needed by choosing the particular image in the tankImage list */
	public void draw(Graphics g) {

		if (!live) {
			if (!good) {
				tc.tanks.remove(this);
			}
			return;
		}

		if (good)
			new DrawBloodbBar().draw(g);

		switch (Kdirection) {
			/**choosing the particular tank image  using the corresponding initial direction  */
			/**Different current player number correspond to different tank image */
		case D:
			if(player==1){	g.drawImage(tankImags[4], x, y, null);
			}
			else if(tc.Player2&&player==2){
				g.drawImage(tankImags[8], x, y, null);
			}else{
			g.drawImage(tankImags[0], x, y, null);}
			break;

		case U:
			if(player==1){	g.drawImage(tankImags[5], x, y, null);
			}else if(tc.Player2&&player==2){
				g.drawImage(tankImags[9], x, y, null);
			}else{
			g.drawImage(tankImags[1], x, y, null);}
			break;

		case L:
			if(player==1){	g.drawImage(tankImags[6], x, y, null);
			}else if(tc.Player2&&player==2){
			g.drawImage(tankImags[10], x, y, null);
		}else{
			g.drawImage(tankImags[2], x, y, null);}
			break;

		case R:
			if(player==1){	g.drawImage(tankImags[7], x, y, null);
		}else if(tc.Player2&&player==2){
			g.drawImage(tankImags[11], x, y, null);
		}else{
			g.drawImage(tankImags[3], x, y, null);}
			break;

		}

		move();   
	}

	/**
	 * the method to move the tank, using old coordinate(current position) and direction to generate new coordinate
	Because we use sppedX and speedY as tank velocity, if the tank goes left, then its horizontal coordinate
	 will be the current x position minus its speed,in the other hand, going right means x plus the speed
	 */
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
		/**Set the boudary
		 the position of the tank can not be smaller than 0 and bigger than the frame
		 If the coordinate exceeds the bondary it will be set to the boundary limit
		 */
		if (x < 0)
			x = 0;
		if (y < 40)     
			y = 40;
		if (x + Tank.width > tankGame.Fram_width)
			x = tankGame.Fram_width - Tank.width;
		if (y + Tank.length > tankGame.Fram_length)
			y = tankGame.Fram_length - Tank.length;

		if (!good) {
			Direction[] directons = Direction.values();
			if (step == 0) {                  
				step = r.nextInt(12) + 3;  
				int mod=r.nextInt(9);
				/**If the player tank appears in the same horizontal route or vertical route,
					the machine tank choose the correct direction to chase the player tank
					for example: if they have same x coordinate, and player tank is above machine tank,
					then we set the direction of machine tank as UP.
				 */
				/**The condition of this If statement means Player Tank is in the same rectangle region of AI Tank */
				if (playertankaround()){
					if(x==tc.homeTank.x){ if(y>tc.homeTank.y) direction=directons[1];
					else if (y<tc.homeTank.y) direction=directons[3];
					}else if(y==tc.homeTank.y){ if(x>tc.homeTank.x) direction=directons[0];
					else if (x<tc.homeTank.x) direction=directons[2];
					}
					/**If the Player Tank is neither in same horizontal nor vertical route,
						randomly choose a direction for machine tank to move forward
					 */
					else{
						int rn = r.nextInt(directons.length);
						direction = directons[rn]; 
					}
					rate=2;
				}else if (mod==1){
					rate=1;
				}else if(1<mod&&mod<=3){
					rate=1;
				}else{
				int rn = r.nextInt(directons.length);
				direction = directons[rn]; 
				rate=1;}    
			}
			step--;
			if(rate==2){
				if (r.nextInt(40) > 35)
					this.fire();
			}else if (r.nextInt(40) > 38)
				this.fire();
		}
	}
	/**This is a method to indicate if Player Tank is in the area of the Rectangle around AI Tank,
		if they are in the same rectangle region then return true
	 */
	public boolean playertankaround(){
		int rx=x-15,ry=y-15;
		if((x-15)<0) rx=0;
		if((y-15)<0)ry=0;
		Rectangle a=new Rectangle(rx, ry,60,60);
		if (this.live && a.intersects(tc.homeTank.getRect())) {
		return true;	
		}
		return false;	
	}
	/**divide the map into different zones using coordinate */
	public int getzone(int x,int y){
		int tempx=x;
		int tempy=y;
		if (tempx<85&&tempy<300) return 11;
		else if(tempx>85&&tempx<140&&tempy>0&&tempy<100) return 9;
		else if(tempx>85&&tempx<140&&tempy>254&&tempy<300) return 10;
		else if(tempx>0&&tempx<200&&tempy>300&&tempy<715) return 12;
		else if(tempx>140&&tempx<400&&tempy>0&&tempy<150) return 7;
		else if(tempx>140&&tempx<400&&tempy>210&&tempy<300) return 8;
		else if(tempx>400&&tempx<500&&tempy>0&&tempy<300) return 6;
		else if(tempx>500&&tempy>0&&tempy<180) return 5;
		else if(tempx>500&&tempy>180&&tempy<300) return 4;
		else if(tempx>520&&tempx<600&&tempy>3000&&tempy<715) return 2;
		else if(tempx>600&&tempy>300&&tempy<715) return 3;
	return 1;
	}
	public int getdirect(int a,int b){
		if(b==13){
			
		}
		return 4;
	}
	/**go back to previous position */
	private void changToOldDir() {  
		x = oldX;
		y = oldY;
	}

	public void keyPressed(KeyEvent e) {  
		int key = e.getKeyCode();
		if (player==1){
		switch (key) {
			/** KeyEvent.VK_R -> Refresh game, clean all data and create new data */
		case KeyEvent.VK_R:  
			tc.tanks.clear(); 
			tc.bullets.clear();
//			tc.trees.clear();
//			tc.otherWall.clear();
//			tc.homeWall.clear();
//			tc.metalWall.clear();
			tc.gameMap.clearTrees();
			tc.gameMap.clearOtherWalls();
			tc.gameMap.clearHomeWalls();
			tc.gameMap.clearMetalWalls();



			tc.homeTank.setLive(false);
			if (tc.tanks.size() == 0) {        
				for (int i = 0; i < 20; i++) {
					if (i < 9)                             
						tc.tanks.add(new Tank(150 + 70 * i, 40, false,
								Direction.R, tc,0));
					else if (i < 15)
						tc.tanks.add(new Tank(700, 140 + 50 * (i -6), false,
								Direction.D, tc,0));
					else
						tc.tanks.add(new Tank(10,  50 * (i - 12), false,
								Direction.L, tc,0));
				}
			}
			
			tc.homeTank = new Tank(300, 560, true, Direction.STOP, tc,0);
			if (!tc.gameMap.isHomeLive())
				tc.gameMap.setHomeLive();
			tankGame abc=new tankGame();
			if (tc.Player2) abc.Player2=true;
			break;
			/**set direction of Player Tank using KeyEvent
			 */
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
		}}
		if (player==2){
		switch(key){
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
			
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		
		case KeyEvent.VK_UP: 
			bU = true;
			break;
		
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		}
		}
		decideDirection();
	}
	/**Set the direction of Tank according to the values of Key pressed */
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
	/** every time when the key we pressed is released,
	 we set back the bDirection(bR,bU...) to false.
	 in case we can guarantee only One direction value will set to true
	 */
	public void keyReleased(KeyEvent e) {  
		int key = e.getKeyCode();
		if (player==1){
		switch (key) {
		
		case KeyEvent.VK_F:
			fire02();
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
			

		}}
		if (player==2){
			switch (key) {
			
			case KeyEvent.VK_SLASH:
				fire();
				break;
				
			case KeyEvent.VK_RIGHT:
				bR = false;
				break;
			
			case KeyEvent.VK_LEFT:
				bL = false;
				break;
			
			case KeyEvent.VK_UP:
				bU = false;
				break;
			
			case KeyEvent.VK_DOWN:
				bD = false;
				break;
				

			}}
		decideDirection(); 
	}
	/**this method create the firing bullet
	Each fire method refers to different Players
	 */
	public Bullets01 fire() {
		if (!live)
			return null;
		int x = this.x + Tank.width / 2 - Tank_Bullet.width / 2;
		int y = this.y + Tank.length / 2 - Tank_Bullet.length / 2;
		Bullets01 m = new Bullets01(x, y + 2, good, Kdirection, this.tc);
		tc.bullets.add(m);                                                
		return m;
	}
	public Bullets02 fire02() {
		if (!live)
			return null;
		int x = this.x + Tank.width / 2 - Tank_Bullet.width / 2;
		int y = this.y + Tank.length / 2 - Tank_Bullet.length / 2;
		Bullets02 m = new Bullets02(x, y + 2, good, Kdirection, this.tc);
		tc.bullets.add(m);
		return m;
	}

	/**
	 * generate a rectangle to indicate if AI tank and player tank are close around */
	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}
	/* indicates if tank is alive */
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isGood() {
		return good;
	}

	/**When the current position of Tank intersects the area of Wall
	Tank can not go further so sets tanks's coordinate to previous (turn back)
	*/
	public boolean collideWithWall(CommonWall w) {
		if (this.live && this.getRect().intersects(w.getRect())) {
			 this.changToOldDir();    
			return true;
		}
		return false;
	}
	/**
	 * When the current position of Tank intersects the area of Wall
	Tank can not go further so sets tanks's coordinate to previous (turn back)
	*/
	public boolean collideWithWall(MetalWall w) {
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.changToOldDir();     
			return true;
		}
		return false;
	}
	/**When the current position of Tank intersects the area of River
	Tank can not go further so sets tanks's coordinate to previous (turn back)
	*/
	public boolean collideRiver(River r) {
		if (this.live && this.getRect().intersects(r.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}
	/**
	 * When the current position of Tank intersects the area of Home
	Tank can not go further so sets tanks's coordinate to previous (turn back)
	*/
	public boolean collideHome(Home h) {
		if (this.live && this.getRect().intersects(h.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}
	/**
	 *Since the tanks on the map was put into a list, we need to check if the this.tank collide
	with any of those tanks, if intersects, change both of tanks's coordinate to previous value
	so they can not go further
	 */
	public boolean collideWithTanks(java.util.List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
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

	/**
	 *
	 * @return returns the health of tank
	 */
	public int getLife() {
		return life;
	}

	/**
	 *
	 * @param life
	 * sets tank's life to param
	 */
	public void setLife(int life) {
		this.life = life;
	}

	@Override
	public void Update() {

	}

	@Override
	public void Render(Graphics g) {

	}

	/**
	 * Draw the blood bar of the tank
	 */
	private class DrawBloodbBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(375, 585, width, 10);
			int w = width * life / 200;
			g.fillRect(375, 585, w, 10);
			g.setColor(c);
		}
	}

	/**
	 *
	 * @param b the blood object to be eaten
	 * @return returns true if the tank eats the blood object
	 */
	public boolean eat(GetBlood b) {
		if (this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
			if(this.life<=100)
			this.life = this.life+100;      
			else
				this.life = 200;
			b.setLive(false);
			return true;
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
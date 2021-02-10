package main.java.com.bham.bc;


import main.java.com.bham.bc.armory.Bullets01;
import main.java.com.bham.bc.armory.Tank_Bullet;
import main.java.com.bham.bc.environment.GameMap;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;

public class tankGame extends Frame implements ActionListener {


	private static final long serialVersionUID = 1L;
	/**set the frame size */
	public static final int Fram_width = 800;
	public static final int Fram_length = 600;
	public static boolean printable = true;

	MenuBar jmb = null;
	Menu jm1 = null, jm2 = null, jm3 = null, jm4 = null,jm5=null;
	MenuItem jmi1 = null, jmi2 = null, jmi3 = null, jmi4 = null, jmi5 = null,
			jmi6 = null, jmi7 = null, jmi8 = null, jmi9 = null,jmi10=null,jmi11=null;
	Image screenImage = null;
	/**create two home tanks */
	Tank homeTank = new Tank(300, 560, true, Direction.STOP, this,1);
	Tank homeTank2 = new Tank(449, 560,true,Direction.STOP,this,2);
	Boolean Player2=false;
	GetBlood blood = new GetBlood();
	Boolean win=false,lose=false;
	/**Create Container for Game Objects */
	List<Tank> tanks = new ArrayList<Tank>();
	public List<BombTank> bombTanks = new ArrayList<BombTank>();
	public List<Tank_Bullet> bullets = new ArrayList<Tank_Bullet>();

	public GameMap gameMap;


	public void update(Graphics g) {

		screenImage = this.createImage(Fram_width, Fram_length);

		Graphics gps = screenImage.getGraphics();
		Color c = gps.getColor();
		gps.setColor(Color.GRAY);
		gps.fillRect(0, 0, Fram_width, Fram_length);
		gps.setColor(c);
		framPaint(gps);
		g.drawImage(screenImage, 0, 0, null);
	}

	public void framPaint(Graphics g) {

		Color c = g.getColor();
		g.setColor(Color.green);

		Font f1 = g.getFont();
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		if(!Player2)g.drawString("Tanks left in the field: ", 200, 70);
		else g.drawString("Tanks left in the field: ", 100, 70);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		if(!Player2)g.drawString("" + tanks.size(), 400, 70);
		else g.drawString("" + tanks.size(), 300, 70);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		if(!Player2)g.drawString("Health: ", 580, 70);
		else g.drawString("Health: ", 380, 70);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		if(!Player2) g.drawString("" + homeTank.getLife(), 650, 70);
		else g.drawString("Player1: " + homeTank.getLife()+"    Player2:"+homeTank2.getLife(), 450, 70);
		g.setFont(f1);
		if (!Player2){
			if (tanks.size() == 0 && gameMap.isHomeLive() && homeTank.isLive()&&lose==false) {
			Font f = g.getFont();
			g.setFont(new Font("Times New Roman", Font.BOLD, 60));
			gameMap.clearOtherWalls();
			g.drawString("Congratulations! ", 200, 300);
			g.setFont(f);
			win=true;
		}

		if (homeTank.isLive() == false&&win==false) {
			Font f = g.getFont();
			g.setFont(new Font("Times New Roman", Font.BOLD, 40));
			tanks.clear();
			bullets.clear();
			g.drawString("Sorry. You lose!", 200, 300);
			lose=true;
			g.setFont(f);
		}}else{
			if (tanks.size() == 0 && gameMap.isHomeLive() && (homeTank.isLive()||homeTank2.isLive())&&lose==false) {
				Font f = g.getFont();
				g.setFont(new Font("Times New Roman", Font.BOLD, 60));
				gameMap.clearOtherWalls();
				g.drawString("Congratulations! ", 200, 300);
				g.setFont(f);
				win=true;
			}

			if (homeTank.isLive() == false&&homeTank2.isLive()==false&&win==false) {
				Font f = g.getFont();
				g.setFont(new Font("Times New Roman", Font.BOLD, 40));
				tanks.clear();
				bullets.clear();
				g.drawString("Sorry. You lose!", 200, 300);
				System.out.println("2");
				g.setFont(f);
				lose=true;
			}
		}
		g.setColor(c);



		gameMap.collideWithRiver(homeTank,homeTank2, Player2,g);
		gameMap.renderRiver(g);
		if (gameMap.isHomeLive()) {
			gameMap.renderHome(g);
			gameMap.renderHomeWall(g);
		} else {
			//game over
			tanks.clear();

			gameMap.clearAllExceptHome();

			bombTanks.clear();
			bullets.clear();
			homeTank.setLive(false);

			c = g.getColor();
			g.setColor(Color.green);
			Font f = g.getFont();
			g.setFont(new Font(" ", Font.PLAIN, 40));
			g.setFont(f);
			g.setColor(c);
		}



		homeTank.draw(g);
		homeTank.eat(blood);
		if (Player2) {homeTank2.draw(g);homeTank2.eat(blood);}

		for (int i = 0; i < bullets.size(); i++) {
			Tank_Bullet m = bullets.get(i);
			m.hitTanks(tanks);
			m.hitTank(homeTank);
			m.hitTank(homeTank2);
			gameMap.hitHome(m);
			for(int j=0;j<bullets.size();j++){
				if (i==j) continue;
				Tank_Bullet bts=bullets.get(j);
				m.hitBullet(bts);
			}
			gameMap.hitWall(m);
			m.Render(g);
		}

		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);

			gameMap.collideWithAll(t);
			gameMap.renderAll(g);


			t.collideWithTanks(tanks);
			gameMap.collideWithHome(t);

			t.draw(g);
		}

		blood.draw(g);
		gameMap.renderTrees(g);

		for (int i = 0; i < bombTanks.size(); i++) {
			BombTank bt = bombTanks.get(i);
			bt.Render(g);
		}

		gameMap.renderOtherWall(g);
		gameMap.renderMetalWall(g);

		homeTank.collideWithTanks(tanks);
		gameMap.collideWithHome(homeTank);
		if (Player2) {
			homeTank2.collideWithTanks(tanks);
			gameMap.collideWithHome(homeTank2);
		}


		gameMap.collideWithWalls(homeTank);
		if (Player2)gameMap.collideWithWalls(homeTank2);
		gameMap.renderHomeWall(g);
		gameMap.renderOtherWall(g);
		gameMap.renderMetalWall(g);

	}

	public tankGame() {
		// printable = false;

		jmb = new MenuBar();
		jm1 = new Menu("Game");
		jm2 = new Menu("Pause/Continue");
		jm3 = new Menu("Help");
		jm4 = new Menu("Level");
		jm5 = new Menu("Addition");
		jm1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm2.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm3.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm4.setFont(new Font("Times New Roman", Font.BOLD, 15));

		jmi1 = new MenuItem("New Game");
		jmi2 = new MenuItem("Exit");
		jmi3 = new MenuItem("Stop");
		jmi4 = new MenuItem("Continue");
		jmi5 = new MenuItem("Help");
		jmi6 = new MenuItem("Level1");
		jmi7 = new MenuItem("Level2");
		jmi8 = new MenuItem("Level3");
		jmi9 = new MenuItem("Level4");
		jmi10=new MenuItem("Add Player 2");
		jmi11= new MenuItem("Join other's game");
		jmi1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi2.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi3.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi4.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi5.setFont(new Font("Times New Roman", Font.BOLD, 15));

		jm1.add(jmi1);
		jm1.add(jmi2);
		jm2.add(jmi3);
		jm2.add(jmi4);
		jm3.add(jmi5);
		jm4.add(jmi6);
		jm4.add(jmi7);
		jm4.add(jmi8);
		jm4.add(jmi9);
		jm5.add(jmi10);
		jm5.add(jmi11);

		jmb.add(jm1);
		jmb.add(jm2);

		jmb.add(jm4);
		jmb.add(jm5);
		jmb.add(jm3);


		jmi1.addActionListener(this);
		jmi1.setActionCommand("NewGame");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("Exit");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("Stop");
		jmi4.addActionListener(this);
		jmi4.setActionCommand("Continue");
		jmi5.addActionListener(this);
		jmi5.setActionCommand("help");
		jmi6.addActionListener(this);
		jmi6.setActionCommand("level1");
		jmi7.addActionListener(this);
		jmi7.setActionCommand("level2");
		jmi8.addActionListener(this);
		jmi8.setActionCommand("level3");
		jmi9.addActionListener(this);
		jmi9.setActionCommand("level4");
		jmi10.addActionListener(this);
		jmi10.setActionCommand("Player2");
		jmi11.addActionListener(this);
		jmi11.setActionCommand("Join");

		this.setMenuBar(jmb);
		this.setVisible(true);

		gameMap = new GameMap(this);

		for (int i = 0; i < 20; i++) {
			if (i < 9)
				tanks.add(new Tank(150 + 70 * i, 40, false, Direction.D, this,0));
			else if (i < 15)
				tanks.add(new Tank(700, 140 + 50 * (i - 6), false, Direction.D,
						this,0));
			else
				tanks
						.add(new Tank(10, 50 * (i - 12), false, Direction.D,
								this,0));
		}

		this.setSize(Fram_width, Fram_length);
		this.setLocation(280, 50);
		this
				.setTitle("Battle City    Final Project for CPE 640");

		this.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		this.setVisible(true);

		this.addKeyListener(new KeyMonitor());
		new Thread(new PaintThread()).start();
	}

	public static void main(String[] args) {
		new tankGame();
	}

	private class PaintThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			while (printable) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			homeTank.keyReleased(e);
			homeTank2.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			homeTank.keyPressed(e);
			homeTank2.keyPressed(e);
		}

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("NewGame")) {
			printable = false;
			Object[] options = { "Confirm", "Cancel" };
			int response = JOptionPane.showOptionDialog(this, "Confirm to start a new game?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {

				printable = true;
				this.dispose();
				new tankGame();
			} else {
				printable = true;
				new Thread(new PaintThread()).start();
			}

		} else if (e.getActionCommand().endsWith("Stop")) {
			printable = false;
			// try {
			// Thread.sleep(10000);
			//
			// } catch (InterruptedException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
		} else if (e.getActionCommand().equals("Continue")) {

			if (!printable) {
				printable = true;
				new Thread(new PaintThread()).start();
			}
		} else if (e.getActionCommand().equals("Exit")) {
			printable = false;
			Object[] options = { "Confirm", "Cancel" };
			int response = JOptionPane.showOptionDialog(this, "Confirm to exit?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				System.out.println("break down");
				System.exit(0);
			} else {
				printable = true;
				new Thread(new PaintThread()).start();

			}

		} else if(e.getActionCommand().equals("Player2")){
			printable = false;
			Object[] options = { "Confirm", "Cancel" };
			int response = JOptionPane.showOptionDialog(this, "Confirm to add player2?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				printable = true;
				this.dispose();
				tankGame Player2add=new tankGame();
				Player2add.Player2=true;
			} else {
				printable = true;
				new Thread(new PaintThread()).start();
			}
		}
		else if (e.getActionCommand().equals("help")) {
			printable = false;
			JOptionPane.showMessageDialog(null, "Use WSAD to control Player1's direction, use F to fire and restart with pressing R\nUse diection key to Control Player2, use slash to fire",
					"Help", JOptionPane.INFORMATION_MESSAGE);
			this.setVisible(true);
			printable = true;
			new Thread(new PaintThread()).start();
		} else if (e.getActionCommand().equals("level1")) {
			Tank.count = 12;
			Tank.speedX = 6;
			Tank.speedY = 6;
			Tank_Bullet.speedX = 10;
			Tank_Bullet.speedY = 10;
			this.dispose();
			new tankGame();
		} else if (e.getActionCommand().equals("level2")) {
			Tank.count = 12;
			Tank.speedX = 10;
			Tank.speedY = 10;
			Bullets01.speedX = 12;
			Bullets01.speedY = 12;
			this.dispose();
			new tankGame();

		} else if (e.getActionCommand().equals("level3")) {
			Tank.count = 20;
			Tank.speedX = 14;
			Tank.speedY = 14;
			Bullets01.speedX = 16;
			Bullets01.speedY = 16;
			this.dispose();
			new tankGame();
		} else if (e.getActionCommand().equals("level4")) {
			Tank.count = 20;
			Tank.speedX = 16;
			Tank.speedY = 16;
			Bullets01.speedX = 18;
			Bullets01.speedY = 18;
			this.dispose();
			new tankGame();
		} else if (e.getActionCommand().equals("Join")){
			printable = false;
			String s=JOptionPane.showInputDialog("Please input URL:");
			System.out.println(s);
			printable = true;
			new Thread(new PaintThread()).start();
		}

	}
}

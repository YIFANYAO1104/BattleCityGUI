package com.bham.bc;


import com.bham.bc.common.Constants;

import static com.bham.bc.CenterController.*;

import java.awt.*;
import java.awt.event.*;


public class DevelopClient extends Frame  {

	/**
	 *This is the UI Logic, need to be replace using javaFX.
	 */
	private static final long serialVersionUID = 1L;
	public static final int Fram_width = Constants.WindowWidth;
	public static final int Fram_length = Constants.WindowHeight;
	public static boolean printable = true;
	private Image screenImage = null;


	@Override
	public void update(Graphics g) {
		if(centerController.isLoss()){
			g.setColor(Color.GREEN);
			Font f = g.getFont();
			g.setFont(new Font("Times New Roman", Font.BOLD, 40));
			g.drawString("Sorry. You lose!", 200, 300);
			g.setFont(f);

			centerController.clear();
		}else if(centerController.isWin()){
			g.setColor(Color.GREEN);
			Font f = g.getFont();
			g.setFont(new Font("Times New Roman", Font.BOLD, 60));
			g.drawString("Congratulations! ", 200, 300);
			g.setFont(f);

			centerController.clear();
		} else {
			//double buffering
			screenImage = this.createImage(Fram_width, Fram_length);

			Graphics gps = screenImage.getGraphics();
			gps.setColor(Color.GRAY);
			gps.fillRect(0, 0, Fram_width, Fram_length);

			centerController.render(gps); //render backend game content
			updateScoreBoard(gps);
			g.drawImage(screenImage, 0, 0, null);

			centerController.update(); //update backend game content
		}
	}

	public void updateScoreBoard(Graphics g){
		g.setColor(Color.BLUE);
		Font f1 = g.getFont();

		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		g.drawString("Tanks left in the field: ", 200, 70);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		g.drawString("" + centerController.getEnemyNumber(), 400, 70);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		g.drawString("Health: ", 580, 70);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		g.drawString("" + centerController.getLife(), 650, 70);
		g.setFont(f1);
	}

	public DevelopClient() {

		this.setSize(Fram_width, Fram_length);
		this.setLocation(280, 50);
		this.setTitle("Battle City    Final Project");

		this.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});

		this.setResizable(false);
		this.setBackground(Color.GREEN);
		this.setVisible(true);

		this.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				centerController.keyReleased(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				centerController.keyPressed(e);
			}
		});
		startPainting();
	}

	private void startPainting(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (printable) {
					repaint();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		new DevelopClient();
	}
}

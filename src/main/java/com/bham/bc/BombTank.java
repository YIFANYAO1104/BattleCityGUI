package com.bham.bc;

import com.bham.bc.common.BaseGameEntity;
import com.bham.bc.common.Messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import  static  com.bham.bc.CenterController.*;

/**
 * Bomb effect
 */

public class BombTank extends BaseGameEntity {

	/**
	 * Status of tank's life
	 */
	private boolean live = true;
	/**
	 * Indicates the progress of bombing, we use couple images to show one time of bombing
	 */
	int step = 0;
	/**
	 *
	 * @param x
	 * @param y
	 * Constructor of BombTank. When tank gets bombed the Object should be created
	 */
	public BombTank(int x, int y) {
		super(GetNextValidID(),x,y);
		initImages();
	}
	/**
	 * Images of bombing,should be replaced later
	 */
	private void initImages() {
		entityImages = new Image[] {
				new Image("file:src/main/resources/Images/1.gif"),
				new Image("file:src/main/resources/Images/2.gif"),
				new Image("file:src/main/resources/Images/3.gif"),
				new Image("file:src/main/resources/Images/4.gif"),
				new Image("file:src/main/resources/Images/5.gif"),
				new Image("file:src/main/resources/Images/6.gif"),
				new Image("file:src/main/resources/Images/7.gif"),
				new Image("file:src/main/resources/Images/8.gif"),
				new Image("file:src/main/resources/Images/9.gif"),
				new Image("file:src/main/resources/Images/10.gif"), };
	}

	@Override
	public void update() {

	}
	/**
	 * If the Bombed Tank is died, remove it from bombTank List in tankGame
	 * When the tank is Bombed 10 times it dies.
	 * @param gc
	 */
	@Override
	public void render(GraphicsContext gc) {
		if (!live) {
			centerController.removeBombTank(this);
			return;
		}
		if (step == entityImages.length) {
			live = false;
			step = 0;
			return;
		}

		gc.drawImage(entityImages[step], x, y);
		step++;
	}

	@Override
	public Rectangle getHitBox() {
		return null;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	@Override
	public String toString() {
		return "specially effect";
	}
}

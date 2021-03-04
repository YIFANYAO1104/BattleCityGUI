package com.bham.bc.entity.physics;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import  static com.bham.bc.components.CenterController.*;

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
	 * Constructor of BombTank. When tank gets bombed the Object should be created
	 * @param x coordinate of x axis
	 * @param y coordinate of y axis
	 */
	public BombTank(int x, int y) {
		super(GetNextValidID(), x, y);
		initImages();
	}
	/**
	 * Images of bombing,should be replaced later
	 */
	private void initImages() {
		entityImages = new Image[] {
				new Image("file:src/main/resources/img/1.gif"),
				new Image("file:src/main/resources/img/2.gif"),
				new Image("file:src/main/resources/img/3.gif"),
				new Image("file:src/main/resources/img/4.gif"),
				new Image("file:src/main/resources/img/5.gif"),
				new Image("file:src/main/resources/img/6.gif"),
				new Image("file:src/main/resources/img/7.gif"),
				new Image("file:src/main/resources/img/8.gif"),
				new Image("file:src/main/resources/img/9.gif"),
				new Image("file:src/main/resources/img/10.gif"),
		};
	}

	@Override
	public void update() {

	}
	/**
	 * If the Bombed Tank died, remove it from bombTank List in tankGame
	 * When the tank is Bombed 10 times it dies.
	 * @param gc graphics context
	 */
	@Override
	public void render(GraphicsContext gc) {
		if (!live) {
			backendServices.removeBombTank(this);
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

	@Override
	public boolean isIntersect(BaseGameEntity b) {
		return false;
	}
}

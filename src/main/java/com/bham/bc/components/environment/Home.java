package com.bham.bc.components.environment;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * Home (Or something we need to protect, can be modified later)
 */
public class Home extends MapObject2D {
	/**
	 * Size Of Home
	 */
	public static int width = 43;
	public static int length = 43;

	private boolean live = true;

	public Home(int x, int y) {
		super(x,y);
		initImages();
	}

	private void initImages() {
		entityImages = new Image[] { new Image("file:src/main/resources/img/home.jpg"), };
	}


	@Override
	public void update() {

	}
	/**
	 * Draw the Home
	 * @param gc
	 */
	public void render(GraphicsContext gc) {
		gc.drawImage(entityImages[0], x, y);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	@Override
	public boolean isIntersect(BaseGameEntity b) {
		Rectangle t = new Rectangle(x, y, width, length);
		return t.intersects(b.getHitBox().getBoundsInLocal());
	}

	public boolean isLive() { 
		return live;
	}

	public void setLive(boolean live) { 
		this.live = live;
	}

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x, y, width, length);
	}

}

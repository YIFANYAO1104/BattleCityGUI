package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.MapObject2D;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.backendServices;

/**
 * Home (Or something we need to protect, can be modified later)
 */
public class Home extends MapObject2D {
	/**
	 * Size Of Home
	 */
	public static int width = 2*Constants.TILE_WIDTH;
	public static int length = 2*Constants.TILE_WIDTH;

	private boolean live = true;

	public Home(int x, int y) {
		super(x,y);
		initImages();
	}

	@Override
	public void beHitBy(Bullet m) {
		if (m.isLive() && this.getHitBox().intersects(m.getHitBox().getBoundsInLocal())) {
			this.live = false;
			m.setLive(false);
		}
	}

	@Override
	public void collideWith(Tank t) {
		if (t.isLive() && this.getHitBox().intersects(t.getHitBox().getBoundsInLocal())) {
			backendServices.changToOldDir(t);
		}
	}

	private void initImages() {
		entityImages = new Image[] { new Image("file:src/main/resources/img/Map/home.bmp"), };
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

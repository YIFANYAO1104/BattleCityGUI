package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.components.environment.MapObject2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.centerController;

public class CommonWall extends MapObject2D {
	/**
	 * Size OF CommonWall (Common Wall can be destroyed )
	 */
	public static int width = 22;
	public static int length = 21;

	public CommonWall(int x, int y) {
		super(x,y);
		initImages();
	}

	@Override
	public void beHitBy(Bullet m) {
		if (m.isLive() && this.getHitBox().intersects(m.getHitBox().getBoundsInLocal())) {
			m.setLive(false);
			centerController.removeBullet(m);
			setToBeRemovedFromMap();
		}
	}

	@Override
	public void collideWith(Tank t) {
		if (t.isLive() && this.getHitBox().intersects(t.getHitBox().getBoundsInLocal())) {
			centerController.changToOldDir(t);
		}
	}

	private void initImages() {
		entityImages = new Image[] {new Image("file:src/main/resources/img/commonWall.gif"), };
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(entityImages[0], x, y);
	}

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x, y, width, length);
	}

	@Override
	public void update() {

	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
}

package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.centerController;
import static com.bham.bc.utils.messaging.MessageDispatcher.Dispatch;
import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interact;

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
			centerController.changToOldDir(t);
		}
	}

	@Override
	public void interactWith(int ID, int indexOfNode, Rectangle r1) {
		if(this.getHitBox().intersects(r1.getBoundsInLocal()))
			Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,this.ID(),ID,Msg_interact,indexOfNode);
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

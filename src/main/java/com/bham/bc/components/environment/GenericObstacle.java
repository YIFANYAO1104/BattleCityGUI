package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.maploaders.TILESET;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.utils.messaging.MessageDispatcher.Dispatch;
import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interact;

/**
 * Class defining common properties for any obstacle
 */
public abstract class GenericObstacle extends BaseGameEntity {
    protected boolean exists;
    protected int currentFrame;
    protected boolean renderTop;

    /**
     * Constructs an obstacle
     *
     * @param x position in x axis
     * @param y position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public GenericObstacle(int x, int y, TILESET tileset, int... tileIDs) {
        super(GetNextValidID(), x, y);
        exists = true;
        currentFrame = 0;
        entityImages = tileIDs.length == 0 ? getDefaultImage() : tileset.getTiles(tileIDs);
    }

    public boolean renderTop() { return renderTop; }

    public boolean exists() { return exists; }

    /**
     * gets default image of a tile
     * @return
     */
    abstract protected Image[] getDefaultImage();

    /**
     * handles bullet collision
     * @param b bullet to handle
     */
    public abstract void handleBullet(Bullet b);

    /**
     * handles tank collision
     * @param c character to handle
     */
    public abstract void handleCharacter(Character c);

    public void interactWith(int ID,int indexOfNode ,Rectangle r1) {
        if(this.getHitBox().intersects(r1.getBoundsInLocal()))
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY,this.getID(),ID,Msg_interact,indexOfNode);
    }

    @Override
    public void update() { if(entityImages.length > 1) currentFrame = (++currentFrame) % entityImages.length; }

    @Override
    public void render(GraphicsContext gc) { gc.drawImage(entityImages[currentFrame], x, y); }

    @Override
    public Rectangle getHitBox() { return new Rectangle(x, y, entityImages[0].getWidth(), entityImages[0].getHeight()); }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "obstacle"; }
}

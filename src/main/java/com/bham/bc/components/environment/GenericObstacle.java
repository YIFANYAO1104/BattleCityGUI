package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Character;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.maploaders.TILESET;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

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

    protected Color c = new Color(1, 1, 0, 0);

    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public GenericObstacle(int x, int y, TILESET tileset, int... tileIDs) {
        super(GetNextValidID(), x, y);
        exists = true;
        currentFrame = 0;
        entityImages = tileIDs.length == 0 ? getDefaultImage() : tileset.getTiles(tileIDs);
    }

    /**
     * Checks if the tile has to be rendered on top of all other entities
     * @return true if it needs to be in top layer and false otherwise
     */
    public boolean renderTop() { return renderTop; }

    /**
     * Checks if the tile exists. Only for Soft obstacles it is possible to not exist
     * @return true if obstacle exists and false otherwise
     */
    public boolean exists() { return exists; }

    /**
     * Gets default image of a tile
     * @return array of type Image with one image
     */
    abstract protected Image[] getDefaultImage();

    /**
     * Handles bullet collision
     * @param b bullet to handle
     */
    public abstract void handleBullet(Bullet b);

    /**
     * Handles character collision
     * @param c character to handle
     */
    public abstract void handleCharacter(Character c);

    @Deprecated
    /** TODO: check if it is necessary to have this */
    public void interactWith(int ID, int nodeID, Rectangle area) {
        if(getHitBox().intersects(area.getBoundsInLocal())) {
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), ID, Msg_interact, nodeID);
        }
    }

    public boolean handleHitBox(Shape hitBox) {
        if(hitBox.intersects(getHitBox().getBoundsInParent())) {
            System.out.println("Tile:" + getHitBox().getBoundsInParent());
            System.out.println("Line:" + hitBox.getBoundsInParent());
            c = new Color(1, 1, 0, 1);
            return true;
        }
        c = new Color(1, 1, 0, 0);
        return false;
    }

    @Override
    public void update() { if(entityImages.length > 1) currentFrame = (++currentFrame) % entityImages.length; }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(c);

        gc.drawImage(entityImages[currentFrame], x, y);
        gc.fillRect(x, y, 16, 16);
    }

    @Override
    public Rectangle getHitBox() { return new Rectangle(x, y, entityImages[0].getWidth(), entityImages[0].getHeight()); }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Obstacle"; }
}

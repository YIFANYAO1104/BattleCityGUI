package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.environment.obstacles.Attribute;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.maploaders.TILESET;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;
import java.util.List;

import static com.bham.bc.utils.messaging.MessageDispatcher.Dispatch;
import static com.bham.bc.utils.messaging.MessageDispatcher.SEND_MSG_IMMEDIATELY;
import static com.bham.bc.utils.messaging.MessageTypes.Msg_interact;

/**
 * Class defining common properties for any obstacle
 */
public abstract class GenericObstacle extends BaseGameEntity {
    protected boolean exists;
    protected int currentFrame;

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
     * Gets all the important attributes described in {@link Attribute} this obstacle has
     * @return EnumSet containing all the attributes this obstacle possesses
     */
    public EnumSet<Attribute> getAttributes() { return EnumSet.noneOf(Attribute.class); }

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
    public abstract void handleCharacter(GameCharacter c);

//    public void handleAll(List<BaseGameEntity> entities){
//        entities.forEach(entity -> {
//            try {
//                handleBullet((Bullet)entity);
//                handleCharacter((GameCharacter) entity);
//            }catch (Exception e){}
//        });
//    }

    @Deprecated
    /** TODO: check if it is necessary to have this */
    public void interactWith(int ID, int nodeID, Rectangle area) {
        if(getHitBox().intersects(area.getBoundsInLocal())) {
            Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), ID, Msg_interact, nodeID);
        }
    }

    public void decreaseHP(double hurt) {}

    @Override
    public void update() { if(entityImages.length > 1) currentFrame = (++currentFrame) % entityImages.length; }

    @Override
    public void render(GraphicsContext gc) { gc.drawImage(entityImages[currentFrame], x, y); }

    @Override
    public Rectangle getHitBox() { return new Rectangle(x, y, entityImages[0].getWidth(), entityImages[0].getHeight()); }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "Obstacle"; }
}

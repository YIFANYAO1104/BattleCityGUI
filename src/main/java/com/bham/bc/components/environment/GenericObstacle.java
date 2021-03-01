package com.bham.bc.components.environment;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * Class defining common properties for any obstacle
 */
public abstract class GenericObstacle extends BaseGameEntity {

    private int currentFrame;
    //temp
    int[] tileIDs = new int[]{};

    /**
     * Constructs an obstacle
     *
     * @param x position in x axis
     * @param y position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public GenericObstacle(int x, int y, TILESET tileset/*, int... tileIDs*/) {
        super(GetNextValidID(), x, y);
        currentFrame = 0;
        entityImages = tileIDs.length == 0 ? getDefaultImage() : tileset.getTiles(tileIDs);
    }

    /**
     * gets default image of a tile
     * @return
     */
    abstract protected Image[] getDefaultImage();

    /**
     * handles bullet collision
     * @param b bullet to handle
     */
    abstract public void handleBullet(Bullet b);

    /**
     * handles tank collision
     * @param t tank to handle
     */
    abstract public void handleCharacter(Tank t);

    @Override
    public void update() { if(entityImages.length > 1) currentFrame = (++currentFrame) % entityImages.length; }

    @Override
    public void render(GraphicsContext gc) { gc.drawImage(entityImages[currentFrame], x, y); }

    @Override
    public Rectangle getHitBox() { return new Rectangle(x, y, entityImages[0].getWidth(), entityImages[0].getHeight()); }

    @Override
    public boolean isIntersect(BaseGameEntity b) { return getHitBox().intersects(b.getHitBox().getBoundsInLocal()); }

    @Override
    public boolean handleMessage(Telegram msg) { return false; }

    @Override
    public String toString() { return "obstacle"; }
}

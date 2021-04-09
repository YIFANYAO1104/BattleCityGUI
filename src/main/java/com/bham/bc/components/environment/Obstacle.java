package com.bham.bc.components.environment;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.entity.graph.ExtraInfo;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.EnumSet;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import static com.bham.bc.utils.messaging.MessageTypes.*;

/**
 * Class defining common properties for any obstacle
 */
public class Obstacle extends BaseGameEntity{
    private double hp;
    private EnumSet<Attribute> attributes;
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
    public Obstacle(int x, int y, ArrayList<Attribute> attributes, Tileset tileset, int... tileIDs) {
        super(GetNextValidID(), x, y);
        hp = 50;
        exists = true;
        currentFrame = 0;
        this.attributes = EnumSet.noneOf(Attribute.class);
        this.attributes.addAll(attributes);
        entityImages = tileIDs.length == 0 ? getDefaultImage() : tileset.getTiles(tileIDs);
    }

    /**
     * Adds all the attributes described in {@link Attribute} this obstacle is defined by
     * @param attributes a list of attributes that should be possessed by this obstacle
     */
    public void addAttributes(ArrayList<Attribute> attributes) {
        this.attributes.addAll(attributes);
    }

    /**
     * Gets all the important attributes described in {@link Attribute} this obstacle has
     * @return EnumSet containing all the attributes this obstacle possesses
     */
    public EnumSet<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Checks if the tile exists. Only for breakable obstacles it is possible to not exist
     * @return true if obstacle exists and false otherwise
     */
    public boolean exists() {
        return exists;
    }

    /**
     * Gets default image of a tile
     * @return array of type Image with one image
     */
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/DefaultTiles/icewall.jpg") };
    }


    public void interactWith(int graphSystemID, int nodeID, Rectangle area) {
        if(getHitBox().intersects(area.getBoundsInLocal())) {
            if(attributes.contains(Attribute.BREAKABLE)) {
                Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), graphSystemID, Msg_interactWithSoft, nodeID);
            } else {
                Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), graphSystemID, Msg_interact, nodeID);
            }
        }
    }

    public void changeHp(double health) {
        if(attributes.contains(Attribute.BREAKABLE)) {
            hp += health;
            if (hp <= 0.0) {
                Dispatch.DispatchMessage(SEND_MSG_IMMEDIATELY, getID(), backendServices.getGraph().getID(), Msg_removeSoft, NO_ADDITIONAL_INFO);
                exists = false;
                entityManager.removeEntity(this);
            }
        }
    }

    @Override
    public void update() {
        if(entityImages.length > 1) currentFrame = (++currentFrame) % entityImages.length;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(entityImages[currentFrame], x, y);
    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, entityImages[0].getWidth(), entityImages[0].getHeight());
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return "Obstacle";
    }
}

package com.bham.bc.components.environment;

import com.bham.bc.audio.SoundEffect;
import com.bham.bc.components.triggers.effects.Dissolve;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.utils.messaging.MessageDispatcher.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.EnumSet;

import static com.bham.bc.components.Controller.services;
import static com.bham.bc.entity.EntityManager.entityManager;
import static com.bham.bc.utils.messaging.MessageTypes.*;

/**
 * Class defining common properties for any obstacle
 */
public class Obstacle extends BaseGameEntity {
    private final EnumSet<Attribute> ATTRIBUTES;
    private double hp;
    private int currentFrame;

    /**
     * Constructs an obstacle by default giving it 50 HP
     *
     * @param x          top-left position in x axis
     * @param y          top-left position in y axis
     * @param attributes array list of {@link Attribute} values which define the obstacle
     * @param tileset    type of tileset
     * @param tileIDs    IDs of tiles in case the obstacle is animated (must be 1 or more)
     */
    public Obstacle(int x, int y, ArrayList<Attribute> attributes, Tileset tileset, int... tileIDs) {
        super(getNextValidID(), x, y);
        hp = 50;
        currentFrame = 0;
        ATTRIBUTES = EnumSet.noneOf(Attribute.class);
        if(attributes != null) ATTRIBUTES.addAll(attributes);

        if(tileIDs.length == 0) throw new IllegalArgumentException("There must be at least one tile ID for the obstacle");
        entityImages = tileset.getTiles(tileIDs);
    }

    /**
     * Simulates the destruction effect and removes the obstacle from the entity list
     */
    private void destroy() {
        exists = false;
        entityManager.removeEntity(this);

        Dissolve dissolve = new Dissolve(getPosition(), entityImages[currentFrame], 0);
        services.addEffectTrigger(dissolve);
        audioManager.playEffect(SoundEffect.DESTROY_SOFT);
    }

    /**
     * Gets all the important attributes described in {@link Attribute} this obstacle has
     * @return EnumSet containing all the attributes this obstacle possesses
     */
    public EnumSet<Attribute> getAttributes() {
        return ATTRIBUTES;
    }

    /**
     * Notifies about obstacle's interaction with some rectangular area
     *
     * <p>This method is used by the graph system to mainly check the interaction of <i>BREAKABLE</i> obstacles</p>
     *
     * @param graphSystemID ID of the graph system to be notified about intersection
     * @param nodeID        ID of the node intersecting the obstacle
     * @param area          Rectangle representing an intersection area
     */
    public void interacts(int graphSystemID, int nodeID, Rectangle area) {
        if(getHitBox().intersects(area.getBoundsInLocal())) {
            if(ATTRIBUTES.contains(Attribute.BREAKABLE)) {
                Dispatcher.dispatchMessage(SEND_MSG_IMMEDIATELY, getID(), graphSystemID, Msg_interactWithSoft, nodeID);
            } else {
                Dispatcher.dispatchMessage(SEND_MSG_IMMEDIATELY, getID(), graphSystemID, Msg_interact, nodeID);
            }
        }
    }

    /**
     * Changes HP of this obstacle
     *
     * <p>HP is only changed if the obstacle has <i>BREAKABLE</i> attribute. If the obstacle is destroyed,
     * the graph system is notified about it to add free graph nodes.</p>
     *
     * @param health amount of health points that should be added/subtracted to this obstacle's HP
     */
    public void changeHp(double health) {
        if(ATTRIBUTES.contains(Attribute.BREAKABLE)) {
            hp += health;
            if (hp <= 0.0) {
                Dispatcher.dispatchMessage(SEND_MSG_IMMEDIATELY, getID(), services.getGraph().getID(), Msg_removeSoft, NO_ADDITIONAL_INFO);
                destroy();
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
    public double getHitBoxRadius() {
        return Math.hypot(getHitBox().getWidth()/2, getHitBox().getHeight()/2);
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

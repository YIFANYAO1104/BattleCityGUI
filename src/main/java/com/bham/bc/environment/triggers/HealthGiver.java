/**
 * Desc: If a bot runs over an instance of this class its health is increased.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.environment.triggers;

import com.bham.bc.common.BaseGameEntity;
import com.bham.bc.common.Messaging.Telegram;
import com.bham.bc.common.Triggers.RespawningTrigger;
import com.bham.bc.environment.Impl.CommonWall;
import com.bham.bc.tank.Tank;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.common.Constants.FrameRate;

public class HealthGiver extends RespawningTrigger<Tank> {



    /**
     * the amount of health an entity receives when it runs over this trigger
     */
    private int health;

    public HealthGiver(int x,int y,int width, int length, int health, int respawnSeconds) {

        super(BaseGameEntity.GetNextValidID(), x, y);
        this.health = health;

        //create this trigger's region of fluence
        addRectangularTriggerRegion(new Point2D(x, y), new Point2D(width, length));
        setRespawnDelay(respawnSeconds * FrameRate);
        initImages();
    }

    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/Images/hp.png"), };
    }

    //if triggered, the bot's health will be incremented
    @Override
    public void tryTrigger(Tank tank) {
        if (isActive() && isTouchingTrigger(tank.getPosition(), tank.getRadius())) {
            tank.increaseHealth(health);

            deactivate();
        }
    }

    //draws a box with a red cross at the trigger's location
    @Override
    public void render(GraphicsContext gc) {
        if (isActive()) {
            gc.drawImage(entityImages[0], this.x, this.y);
        }
    }

    @Override
    public Rectangle getHitBox() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
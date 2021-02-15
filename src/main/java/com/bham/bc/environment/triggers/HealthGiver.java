/**
 * Desc: If a bot runs over an instance of this class its health is increased.
 *
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.environment.triggers;

import com.bham.bc.common.BaseGameEntity;
import com.bham.bc.common.Messaging.Telegram;
import com.bham.bc.common.Triggers.RespawningTrigger;
import com.bham.bc.common.Triggers.Vector2D;
import com.bham.bc.environment.Impl.CommonWall;
import com.bham.bc.tank.Tank;

import java.awt.*;

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
        addRectangularTriggerRegion(new Vector2D(x, y), new Vector2D(width, length));
        setRespawnDelay(respawnSeconds * FrameRate);
        initImages();
    }

    private void initImages() {
        entityImags = new Image[] { tk.getImage(CommonWall.class
                .getResource("/Images/hp.png")), };
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
    public void render(Graphics g) {
        if (isActive()) {
            g.drawImage(entityImags[0], this.x, this.y, null);
        }
    }

    @Override
    public Rectangle getRect() {
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
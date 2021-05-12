/**
 *  Base class to create a trigger that is capable of respawning
 *            after a period of inactivity
 */
package com.bham.bc.components.triggers;


import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.utils.GeometryEnhanced;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The category of trigger that  it is triggered
 * but then becomes inactive for a specified amount of time. These values
 * control the amount of time required to pass before the trigger becomes
 * active once more.
 */
abstract public class RespawnTrigger extends Trigger {


    protected int cooldown;
    protected int timeTillRespawn;

    /**
     * sets the trigger to be inactive for m_iNumUpdatesBetweenRespawns
     * destroy non respawning triggers which has no cooling down value
     * update-steps
     */
    protected void deactivate() {
        active = false;
        if (cooldown == 0) destroy();
        else timeTillRespawn = cooldown;
    }

    /**
     * Construct a respawnTrigger in a specific position.
     * @param x the x coordinates of respawn trigger
     * @param y the y coordinates of respawn trigger
     */
    public RespawnTrigger(int x, int y) {
        super(x,y);
        cooldown = 0;
        timeTillRespawn = 0;
    }

    /**
     * this is called each game-tick to update the trigger's internal state
     */
    @Override
    public void update() {
        if ((--timeTillRespawn <= 0) && !active) {
            active = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // GeometryEnhanced.renderHitBox(gc,this.getHitBox());
        if(active) {
            gc.drawImage(entityImages[0], x, y);
        }
    }

    public void setCooldown(int numTicks) {
        cooldown = numTicks;
    }


    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, GameMap.getTileWidth(), GameMap.getTileHeight());
    }

    @Override
    public double getHitBoxRadius() {
        return Math.hypot(getHitBox().getWidth()/2, getHitBox().getHeight()/2);
    }
}
/**
 *  Desc:     base class to create a trigger that is capable of respawning
 *            after a period of inactivity
 */
package com.bham.bc.components.triggers;


import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.environment.Obstacle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

abstract public class RespawnTrigger extends Trigger {

    //When a bot comes within this trigger's area of influence it is triggered
    //but then becomes inactive for a specified amount of time. These values
    //control the amount of time required to pass before the trigger becomes 
    //active once more.
    protected int cooldown;
    protected int timeTillRespawn;

    /**
     * sets the trigger to be inactive for m_iNumUpdatesBetweenRespawns
     * update-steps
     */
    protected void deactivate() {
        active = false;
        timeTillRespawn = cooldown;
    }

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
    protected void renderRegion(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2.0);
        gc.strokeRect(getHitBox().getX(), getHitBox().getY(), getHitBox().getWidth(), getHitBox().getHeight());
    }


    @Override
    public void render(GraphicsContext gc) {
        if(active) {
            gc.drawImage(entityImages[0], x, y);
            renderRegion(gc);
        }
    }

    public void setCooldown(int numTicks) {
        cooldown = numTicks;
    }


    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, GameMap.getTileWidth(), GameMap.getTileHeight());
    }
}
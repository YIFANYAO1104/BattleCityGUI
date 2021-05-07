package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.shooting.BulletType;
import com.bham.bc.components.shooting.LaserType;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

/**
 * Represents a trigger that makes player to another appearance and armory system.
 */
public class StateTrigger extends RespawnTrigger {

    /**
     * Construct a stateTrigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param respawnCooldown the respawn time of trigger
     */
    public StateTrigger(int x, int y,int respawnCooldown) {
        super(x,y);
        setCooldown(respawnCooldown*FRAME_RATE);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{ new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/state1.png")) };
    }

    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).toState1();
            ((Player) entity).gunChange(BulletType.ICE);
            ((Player) entity).laserChange(LaserType.ThunderLaser);
            deactivate();
        }
    }
}


package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.Player;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

/**
 * Representing a Freeze Trap Trigger that will disable player to move for few seconds
 */
public class Freeze extends RespawnTrigger {
	private int activationTime;
	
	/**
     * Construct a Freeze trigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param activationTime activation time the trigger has an effect on the player
     * @param respawnCooldown the respawn time of trigger
     */
    public Freeze(int x, int y, int activationTime, int respawnCooldown) {

        super(x, y);
        this.activationTime = activationTime * FRAME_RATE;

        //create this trigger's region of fluence
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[] {new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/traps/snowflake.png")), };
    }

    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateFreeze(activationTime);
            deactivate();
        }
    }
}
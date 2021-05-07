package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.environment.GameMap;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

/**
 * Representing a Inverse Trap Trigger that will cause the player moving in a opposite direction
 */
public class InverseTrap extends RespawnTrigger{
	private int activationTime;

    /**
     * Construct a trap trigger  in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param respawnCooldown the respawn time of trigger
     */
    public InverseTrap(int x, int y, int activationTime, int respawnCooldown){
        super(x,y);
        this.activationTime = activationTime * FRAME_RATE;
        setCooldown(respawnCooldown*FRAME_RATE);
    }
    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[]{ new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/traps/trap.png"))};
    }
    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateInverse(activationTime);
            deactivate();
        }
    }
}
package com.bham.bd.components.triggers.powerups;

import com.bham.bd.components.triggers.RespawnTrigger;
import com.bham.bd.components.characters.Player;
import com.bham.bd.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bd.view.GameSession.FRAME_RATE;

/**
 * Representing a Triple Bullet Trigger that will split player bullets into 3
 */
public class TripleBullet extends RespawnTrigger {
	private int activationTime;
	
	/**
     * Construct a Triple Bullet trigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param activationTime activation time the trigger has an effect on the player
     * @param respawnCooldown the respawn time of trigger
     */
    public TripleBullet(int x,int y, int activationTime, int respawnCooldown) {
        super(x, y);
        this.activationTime = activationTime;
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[] {new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/three.png")), };
    }

    //if triggered, the bot's bullet will split into 3 for few seconds only

    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateTriple(activationTime * FRAME_RATE);
            deactivate();
        }
    }
}
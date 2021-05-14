package com.bham.bd.components.triggers.powerups;

import com.bham.bd.components.triggers.RespawnTrigger;
import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bd.view.GameSession.FRAME_RATE;

/**
 * Representing a speed Trigger that will change player's speed
 */
public class SpeedTrigger extends RespawnTrigger {
    private int speed;

    /**
     * Construct a speedTrigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param respawnCooldown the respawn time of trigger
     * @param speed the speed that trigger will set on the player
     */
    public SpeedTrigger(int x, int y, int respawnCooldown, int speed) {
        super(x,y);
        this.speed = speed;
        setCooldown(respawnCooldown*FRAME_RATE);
    }
    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/flash.png"))};
    }
    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter) entity).speedUp(speed);
            deactivate();
        }
    }
}

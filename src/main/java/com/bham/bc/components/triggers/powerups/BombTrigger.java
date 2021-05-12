package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

/**
 * Represents a trigger that enables player to use bomb (only once)
 */
public class BombTrigger extends RespawnTrigger {

	/**
     * Construct a Bomb Trigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param Respwan the respawn time of trigger
     */
    public BombTrigger(int x, int y, int Respwan){
        super(x,y);
        setCooldown(FRAME_RATE*Respwan);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[]{
                new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/clear.png"))
        };
    }

    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateBomb();
            deactivate();
        }
    }
}

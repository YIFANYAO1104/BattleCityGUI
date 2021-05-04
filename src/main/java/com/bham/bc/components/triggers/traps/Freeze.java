/**
 * Desc: If a player runs over an instance of this class it will freeze for few seconds.
 */
package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.Player;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

public class Freeze extends RespawnTrigger {

	private int activationTime;
	
    public Freeze(int x, int y, int activationTime, int respawnCooldown) {

        super(x, y);
        this.activationTime = activationTime * FRAME_RATE;

        //create this trigger's region of fluence
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/triggers/traps/snowflake.png"), };
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateFreeze(activationTime);
            deactivate();
        }
    }
}
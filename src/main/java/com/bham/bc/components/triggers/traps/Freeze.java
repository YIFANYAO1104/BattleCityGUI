/**
 * Desc: If a player runs over an instance of this class it will freeze for few seconds.
 */
package com.bham.bc.components.triggers.traps;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class Freeze extends RespawnTrigger {

	private int activationTime;
	
    public Freeze(int x, int y, int activationTime, int respawnCooldown) {

        super(x, y);
        this.activationTime = activationTime;

        //create this trigger's region of fluence
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/tiles/triggers/snowflake.png"), };
    }

    //if triggered, the bot will freeze for few seconds
    @Override
    public void handleCharacter(GameCharacter character) {
        if(active && intersects(character)) {
            character.toFreeze(activationTime * FRAME_RATE);
            deactivate();
        }
    }
}
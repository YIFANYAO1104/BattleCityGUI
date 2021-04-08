/**
 * Desc: If a bot runs over an instance of this class its bullet will split into 3 for few seconds only.
 */
package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class TripleBullet extends RespawnTrigger {

	private int activationTime;
	
    public TripleBullet(int x,int y, int activationTime, int respawnCooldown) {
        super(x, y);
        this.activationTime = activationTime;
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/tiles/triggers/three.png"), };
    }

    //if triggered, the bot's bullet will split into 3 for few seconds only

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter) entity).toTriple(activationTime * FRAME_RATE);
            deactivate();
        }
    }
}
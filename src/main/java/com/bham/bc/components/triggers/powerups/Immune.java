/**
 * Desc: If a bot runs over an instance of this class its health will never decrease for few seconds only.
 */
package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class Immune extends RespawnTrigger {
	private int activationTime;
	
    public Immune(int x,int y, int activationTime, int respawnCooldown) {

        super(x, y);
        this.activationTime = activationTime;
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/tiles/triggers/green_heart.png"), };
    }

    //if triggered, the bot's health will be never decrease for few seconds only
    @Override
    public void handleCharacter(GameCharacter character) {
        if (active && intersects(character)) {
            character.toImmune(activationTime * FRAME_RATE);
            deactivate();
        }
    }

    //draws a box with a red cross at the trigger's location
    @Override
    public void render(GraphicsContext gc) {
        if (active) {
            gc.drawImage(entityImages[0], this.x, this.y);
        }
    }
}
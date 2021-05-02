/**
 * Desc: If a bot runs over an instance of this class its health will never decrease for few seconds only.
 */
package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.Player;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

public class Immune extends RespawnTrigger {
	private int activationTime;
	
    public Immune(int x,int y, int activationTime, int respawnCooldown) {

        super(x, y);
        this.activationTime = activationTime;
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/triggers/powerups/green_heart.png"), };
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateImmune(activationTime * FRAME_RATE);
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
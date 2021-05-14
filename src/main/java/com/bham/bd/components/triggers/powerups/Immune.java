package com.bham.bd.components.triggers.powerups;

import com.bham.bd.components.triggers.RespawnTrigger;
import com.bham.bd.components.characters.Player;
import com.bham.bd.entity.BaseGameEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static com.bham.bd.view.GameSession.FRAME_RATE;

/**
 * Represents a trigger that makes player immune from enemy bullets
 */
public class Immune extends RespawnTrigger {
	private int activationTime;
	
	/**
     * Construct an Immune trigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param activationTime activation time the trigger has an effect on the player
     * @param respawnCooldown the respawn time of trigger
     */
    public Immune(int x,int y, int activationTime, int respawnCooldown) {

        super(x, y);
        this.activationTime = activationTime;
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[] {new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/green_heart.png")), };
    }

    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateImmune(activationTime * FRAME_RATE);
            deactivate();
        }
    }

    /**
     * draws a box with a red cross at the trigger's location 
     * @param gc graphic context
     */
    @Override
    public void render(GraphicsContext gc) {
        if (active) {
            gc.drawImage(entityImages[0], this.x, this.y);
        }
    }
}
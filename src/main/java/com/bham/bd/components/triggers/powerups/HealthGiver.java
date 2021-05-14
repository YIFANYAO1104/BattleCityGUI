package com.bham.bd.components.triggers.powerups;

import com.bham.bd.entity.BaseGameEntity;
import com.bham.bd.entity.ai.navigation.ItemType;
import com.bham.bd.components.triggers.RespawnTrigger;
import com.bham.bd.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bd.view.GameSession.FRAME_RATE;

/**
 * Represents a trigger that increases character health
 */
public class HealthGiver extends RespawnTrigger{
    private int health;

    /**
     * Construct a Health Giver Trigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param health the amount of health an entity receives when it runs over this trigger
     * @param respawnCooldown the respawn time of trigger
     */
    public HealthGiver(int x,int y, int health, int respawnCooldown) {

        super(x, y);
        this.health = health;

        //create this trigger's region of fluence
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[] {new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/hp.png")), };
    }

    /**
     * Handle the situation when character touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter) entity).changeHp(health);
            deactivate();
        }
    }

    /**
     * Returns the item type Health
     */
    @Override
    public ItemType getItemType() {
        return ItemType.HEALTH;
    }
}
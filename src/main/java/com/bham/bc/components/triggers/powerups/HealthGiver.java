/**
 * Desc: If a bot runs over an instance of this class its health is increased.
 */
package com.bham.bc.components.triggers.powerups;

import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class HealthGiver extends RespawnTrigger{
    /**
     * the amount of health an entity receives when it runs over this trigger
     */
    private int health;

    public HealthGiver(int x,int y, int health, int respawnCooldown) {

        super(x, y);
        this.health = health;

        //create this trigger's region of fluence
        setCooldown(respawnCooldown * FRAME_RATE);
    }

    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/tiles/triggers/hp.png"), };
    }

    //if triggered, the bot's health will be incremented
    @Override
    public void handleCharacter(GameCharacter character) {
        if (active && intersects(character)) {
            character.changeHP(health);

            deactivate();
        }
    }

    @Override
    public ItemType getItemType() {
        return ItemType.health;
    }
}
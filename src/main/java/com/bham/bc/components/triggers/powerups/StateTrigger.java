package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class StateTrigger extends RespawnTrigger {

    public StateTrigger(int x, int y,int respawnCooldown) {
        super(x,y);
        setCooldown(respawnCooldown*FRAME_RATE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{new Image("file:src/main/resources/img/tiles/triggers/state1.png")};
    }

    /**
     * If the speedBuff triggered, the tank will change its speed to new state
     * @return
     */
    @Override
    public void handleCharacter(GameCharacter character) {
        if(active && intersects(character)){
            if(character instanceof Player){
                ((Player) character).toState1();
                deactivate();
            }
        }
    }
}


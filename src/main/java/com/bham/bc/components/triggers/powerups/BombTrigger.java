/**
 * Desc: If player runs over an instance of this class it will be able to use bomb (only once)
 */
package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;

public class BombTrigger extends RespawnTrigger {

    public BombTrigger(int x, int y, int Respwan){
        super(x,y);
        setCooldown(FRAME_RATE*Respwan);
    }

    protected Image[] getDefaultImage() {
        return new Image[]{
                new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/clear.png"))
        };
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof Player && intersects(entity)) {
            ((Player) entity).activateBomb();
            deactivate();
        }
    }
}

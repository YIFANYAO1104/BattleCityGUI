package com.bham.bd.components.triggers.traps;

import com.bham.bd.components.triggers.RespawnTrigger;
import com.bham.bd.components.characters.GameCharacter;
import com.bham.bd.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bd.view.GameSession.FRAME_RATE;


public class LandmineTrigger extends RespawnTrigger{

    /**
     * Construct a Landmine Trigger in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param Respawn the respawn time of trigger
     */
    public LandmineTrigger(int x,int y,int Respawn){
        super(x,y);
        setCooldown(Respawn*FRAME_RATE);
    }
    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    protected Image[] getDefaultImage() {
        return new Image[]{ new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/traps/Landmine.png"))};
    }

    /**
     * Handle the situation when player touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter) entity).changeHp(-200);
            deactivate();
        }
    }
}

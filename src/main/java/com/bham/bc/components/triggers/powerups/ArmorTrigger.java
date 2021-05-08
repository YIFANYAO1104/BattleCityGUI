package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.entity.BaseGameEntity;
import javafx.scene.image.Image;

import static com.bham.bc.view.GameSession.FRAME_RATE;
/**
 * Representing a Trigger that will increase character health and maximum health limit
 */
public class ArmorTrigger extends  RespawnTrigger{
    private int HP;

    /**
     * Construct an armor trigger  in a position with respawn time
     * @param x x coordinates of the trigger
     * @param y y coordinates of the trigger
     * @param HP the new maximum health limit
     * @param Respawn the respawn time of trigger
     */
    public ArmorTrigger(int x, int y, int HP, int Respawn){
        super(x,y);
        this.HP = HP;
        setCooldown(Respawn*FRAME_RATE);
    }

    /**
     * Get the image array of the trigger
     * @return the image array of this trigger
     */
    @Override
    protected Image[] getDefaultImage() {
        return new Image[]{ new Image(getClass().getClassLoader().getResourceAsStream("img/triggers/powerups/armor.png"))};
    }

    /**
     * Handle the situation when character touches the trigger and the status that need to be changed
     * @param entity game entity on which the collision will be checked
     */
    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter)entity).armorUP(HP);
            deactivate();
        }
    }
}

package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;
public class TeleportTrigger extends RespawnTrigger{
    private double destX;
    private double destY;
    RespawnTrigger destination;
    public TeleportTrigger(int x, int y, int Respawn){
        super(x,y);
        setCooldown(FRAME_RATE*Respawn);
    }
    public void setDestination(RespawnTrigger dest){
        this.destination = dest;
        //this.destX = destination.getX();
        //this.destY = destination.getY();

    }

    protected Image[] getDefaultImage() {
        return new  Image[]{ new Image("file:src/main/resources/img/tiles/triggers/teleport.png")};
    }

    @Override
    public void handleCharacter(GameCharacter character) {
        if(character instanceof Player){
            if(active && intersects(character)){
                if(destination!=null){
                    character.teleport(destX,destY);
                }
                deactivate();
            }
        }
    }
}

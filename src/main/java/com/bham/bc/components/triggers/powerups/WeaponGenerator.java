package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.characters.Player;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.components.characters.GameCharacter;
import javafx.scene.image.Image;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class WeaponGenerator extends RespawnTrigger {
    private Weapon weapon;

    public WeaponGenerator(int x, int y,Weapon weapon,int width, int length,int respawnCooldown) {
        super(x, y);
        this.weapon = weapon;

        setCooldown(respawnCooldown * FRAME_RATE);
    }


    protected Image[] getDefaultImage() {
        return new Image[] {new Image("file:src/main/resources/img/tmp.jpg"), };
    }

    @Override
    public void handle(BaseGameEntity entity) {
        if(active && entity instanceof GameCharacter && intersects(entity)) {
            ((GameCharacter) entity).switchWeapon(weapon);
            deactivate();
        }
    }

    @Override
    public ItemType getItemType() {
        return ItemType.WEAPON;
    }
}

package com.bham.bc.components.triggers.powerups;

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
    public void handleCharacter(GameCharacter character) {
        if(active && intersects(character)){
            character.switchWeapon(this.weapon);
            deactivate();

        }
    }

    @Override
    public ItemType getItemType() {
        return ItemType.weapon;
    }
}

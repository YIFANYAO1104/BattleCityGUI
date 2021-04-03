package com.bham.bc.components.triggers.powerups;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.entity.ai.navigation.ItemType;
import com.bham.bc.components.triggers.RespawnTrigger;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.components.characters.GameCharacter;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class WeaponGenerator extends RespawnTrigger {
    private Weapon weapon;

    public WeaponGenerator(int x, int y,Weapon weapon,int width, int length,int respawnCooldown) {
        super(BaseGameEntity.GetNextValidID(), x, y);
        this.weapon = weapon;

        addRectangularTriggerRegion(new Point2D(x, y), new Point2D(width, length));
        setRespawnDelay(respawnCooldown * FRAME_RATE);
        initImages();
    }


    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/img/tmp.jpg"), };
    }

    @Override
    public void tryTriggerC(GameCharacter entity) {
        if(isActive()&& rectIsTouchingTrigger(entity.getPosition(),entity.getRadius())){
            entity.switchWeapon(this.weapon);
            deactivate();

        }

    }



    @Override
    public void tryTriggerO(Obstacle entity) {

    }

    @Override
    public void render(GraphicsContext gc) {
        if (isActive()) {
            gc.drawImage(entityImages[0], this.x, this.y);
        }

    }

    @Override
    public Rectangle getHitBox() {
        return null;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean intersects(BaseGameEntity b) {
        return false;
    }


    @Override
    public ItemType getItemType() {
        return ItemType.weapon;
    }
}

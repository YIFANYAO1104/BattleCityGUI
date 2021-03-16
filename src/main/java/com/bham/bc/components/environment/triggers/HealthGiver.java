/**
 * Desc: If a bot runs over an instance of this class its health is increased.
 */
package com.bham.bc.components.environment.triggers;

import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.triggers.RespawnTrigger;
import com.bham.bc.components.characters.Character;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.utils.Constants.FRAME_RATE;

public class HealthGiver extends RespawnTrigger<Character> {

    public static int width = Constants.TILE_WIDTH;
    public static int length = Constants.TILE_WIDTH;

    /**
     * the amount of health an entity receives when it runs over this trigger
     */
    private int health;

    public HealthGiver(int x,int y, int health, int respawnCooldown) {

        super(BaseGameEntity.GetNextValidID(), x, y);
        this.health = health;

        //create this trigger's region of fluence
        addRectangularTriggerRegion(new Point2D(x, y), new Point2D(width, length));
        setRespawnDelay(respawnCooldown * FRAME_RATE);
        initImages();
    }

    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/img/Map/hp.png"), };
    }

    //if triggered, the bot's health will be incremented
    @Override
    public void tryTrigger(Character character) {
        if (isActive() && isTouchingTrigger(character.getPosition(), character.getRadius())) {
            character.increaseHP(health);

            deactivate();
        }
    }

    //draws a box with a red cross at the trigger's location
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
}
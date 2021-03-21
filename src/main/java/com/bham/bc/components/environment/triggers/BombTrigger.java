package com.bham.bc.components.environment.triggers;


import com.bham.bc.components.environment.navigation.ItemType;
import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.entity.triggers.DelayTrigger;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import javafx.geometry.Point2D;
import com.bham.bc.components.characters.Character;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.backendServices;
import static com.bham.bc.utils.Constants.FRAME_RATE;

public class BombTrigger extends DelayTrigger<Character> {

    public static int width = 300;
    public static int length = 300;

    public BombTrigger(int x,int y, /*double range,*/ int lifeTime) {

        super(x, y, lifeTime * FRAME_RATE);

        initImages();
        //create and set this trigger's region of fluence
        addRectangularTriggerRegionSurrounded(new Point2D(x, y),
                new Point2D(entityImages[0].getWidth(),entityImages[0].getHeight()),
                new Point2D(width, length));

    }

    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/img/BombTrigger.png"), };
    }

    /**
     * when triggered this trigger adds the bot that made the source of the
     * sound to the triggering bot's perception.
     */
    @Override
    public void tryTrigger(Character character) {
        //is this bot within range of this sound
        if (isActive() && isTouchingTrigger(character.getPosition(), character.getRadius())) {
            character.increaseHealth(0);
            backendServices.addBombTank(new BombTank(character.getX(),character.getY()));
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(entityImages[0], this.x, this.y);
        renderRegion(gc);
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
    public ItemType getItemType() {
        return null;
    }

    @Override
    public void update() {
        //if the lifetime counter expires set this trigger to be removed from
        //the game
         --delayTime;
        if (delayTime == 0) {
            setActive();
        } else if (delayTime <-1) {
            setInactive();
            setToBeRemovedFromGame();
            backendServices.addBombTank(new BombTank(getX(),getY()));
        }
    }
}
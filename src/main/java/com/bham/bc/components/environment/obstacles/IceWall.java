package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.MapObject2D;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.centerController;

/**
 * We can design some properties for icewall
 * e.g Destroyed after specific damage amount
 */
public class IceWall extends MapObject2D {
    public static int width = Constants.TILE_WIDTH;
    public static int length = Constants.TILE_WIDTH;
    public int durability ;

    /**
     * The damage to durability is up to the damage of specific weapon(buller)
     * After durability ->0 ,remove wall
     * @param x
     * @param y
     */
    public IceWall(int x, int y){
        super(x,y);
        this.durability = 150;
        initImages();

    }

    @Override
    public void beHitBy(Bullet m) {
            if (m.isLive() && this.getHitBox().intersects(m.getHitBox().getBoundsInLocal())) {
                m.setLive(false);

                if(this.durability > 50) this.durability -=50;
                else {
                    centerController.removeBullet(m);
                    setToBeRemovedFromMap();
                }

            }
    }

    @Override
    public void collideWith(Tank t) {
            if(t.isLive()&& this.getHitBox().intersects(t.getHitBox().getBoundsInLocal())){
                centerController.changToOldDir(t);
            }

    }

    /**
     * Stilling looking for suitable images for this class
     */
    private void initImages() {
        entityImages = new Image[] {new Image("file:src/main/resources/img/Map/icewall.jpg"), };
    }


    @Override
    public void update() {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(entityImages[0],x,y);

    }

    @Override
    public Rectangle getHitBox() {

        return new Rectangle(x,y,width,length);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    public boolean isIntersect(BaseGameEntity b) {

        Rectangle t = new Rectangle(x, y, width, length);
        return t.intersects(b.getHitBox().getBoundsInLocal());
    }
}

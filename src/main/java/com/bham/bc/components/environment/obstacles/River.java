package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.armory.Bullet;
import com.bham.bc.components.characters.Tank;
import com.bham.bc.components.environment.MapObject2D;
import com.bham.bc.entity.BaseGameEntity;
import com.bham.bc.utils.Constants;
import com.bham.bc.components.environment.TILESET;
import com.bham.bc.utils.messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import static com.bham.bc.components.CenterController.centerController;

public class River extends MapObject2D {
    public static final int width = Constants.TILE_WIDTH;
    public static final int length = Constants.TILE_WIDTH;
    private void initImages() {
        entityImages = new Image[] {/*new Image("file:src/main/resources/img/Map/river_01.jpg")*/TILESET.TILES.getTile(567), };
    }


    public River(int x, int y){
        super(x,y);
        initImages();
    }

    @Override
    public void beHitBy(Bullet m) {
        if (m.isLive() && this.getHitBox().intersects(m.getHitBox().getBoundsInLocal())) {
            m.setLive(false);

            centerController.removeBullet(m);
        }
    }

    @Override
    public void collideWith(Tank t) {
        if(t.isLive()&& this.getHitBox().intersects(t.getHitBox().getBoundsInLocal())){
            centerController.changToOldDir(t);
        }
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
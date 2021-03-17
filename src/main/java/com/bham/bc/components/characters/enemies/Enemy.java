package com.bham.bc.components.characters.enemies;

import com.bham.bc.components.armory.DefaultBullet;
import com.bham.bc.components.characters.SIDE;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.characters.Character;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import static com.bham.bc.components.CenterController.backendServices;

public class Enemy extends Character {
    public static final String IMAGE_PATH = "file:src/main/resources/img/tankU.gif";
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    public static final int MAX_HP = 100;

    private int hp = 100;

    public Enemy(int x, int y) {
        super(x, y, 1, MAX_HP, SIDE.ENEMY);

        entityImages = new Image[] { new Image(IMAGE_PATH, WIDTH, HEIGHT, false, false) };
    }

    /**
     * Gets the HP of the enemy
     * @return integer representing current HP
     */
    public int getHp() { return hp; }

    /**
     * Increases HP for the player
     * @param health amount by which the enemy's HP is increased
     */
    public void increaseHP(int health) { hp = Math.min(hp + health, MAX_HP); }


    public DefaultBullet fire() {
        double centerBulletX = x + WIDTH/2;
        double centerBulletY = y - DefaultBullet.HEIGHT/2;

        Rotate rot = new Rotate(angle, x + WIDTH/2, y + HEIGHT/2);
        Point2D rotatedCenterXY = rot.transform(centerBulletX, centerBulletY);

        double topLeftBulletX = rotatedCenterXY.getX() - DefaultBullet.WIDTH/2;
        double topLeftBulletY = rotatedCenterXY.getY() - DefaultBullet.HEIGHT/2;

        DefaultBullet b = new DefaultBullet(topLeftBulletX, topLeftBulletY, angle, side);
        backendServices.addBullet(b);
        return b;
    }
    /**
     * Since the tanks on the map was put into a list, we need to check if the this.tank collide
     * with any of those tanks, if intersects, change both of tanks's coordinate to previous value
     * so they can not go further
     */
    public boolean collideWithTanks(java.util.List<Enemy> be) {
        for (int i = 0; i < be.size(); i++) {
            MovingEntity t = be.get(i);
            if (this != t) {
                if (this.exists && t.exists()
                        && this.getHitBox().intersects(t.getHitBox().getBoundsInLocal())) {
                    //this.changToOldDir();
                    //t.changToOldDir();
                    return true;
                }
            }
        }
        return false;
    }

    /** TODO: replace this method */
    @Deprecated
    public boolean isPlayerClose() {
        /*
        double rx = x - 15 < 0 ? 0 : x - 15;
        double ry = y - 15 < 0 ? 0 : y - 15;

        Rectangle detectRegion = new Rectangle(rx, ry,60,60);
        if (this.exists && detectRegion.intersects(backendServices.getHomeHitBox().getBoundsInLocal())) return true;
        */

        return false;
    }

    /** TODO: replace this method */
    @Deprecated
    private void aimAtAndShoot(){
        /*
         //Enemy tank switch direction after every 'step' times
         //After the tank changes direction, generate another random steps

        if (step == 0) {
            DIRECTION[] directons = DIRECTION.values();
            //[3,14]
            step = r.nextInt(12) + 3;
            //[0,8]
            int mod=r.nextInt(9);


             //Condition: If Enemy Tank finds Player tank around
             //Logic: check if Player tank is in the same horizontal or vertical line of Enemy Tank
             //If Player tank is found in the line, switch enemy tank's direction and chase Player Tank
             //Else randomly choose direction to move forward

            if (playertankaround()){
                BackendServices cC = backendServices;
                if(x==cC.getPlayerX()){
                    if(y>cC.getPlayerY()){
                        direction=directons[1];
                    } else if (y<cC.getPlayerY()){
                        direction=directons[3];
                    }
                }else if(y==cC.getPlayerY()){
                    if(x>cC.getPlayerX()) {
                        direction=directons[0];
                    } else if (x<cC.getPlayerX()) {
                        direction=directons[2];
                    }
                } else{ //change my direction
                    int rn = r.nextInt(directons.length);
                    direction = directons[rn];
                }
                rate=2;
            } else {
                if (1<=mod&&mod<=3) {
                    rate=1;
                } else {
                    int rn = r.nextInt(directons.length);
                    direction = directons[rn];
                    rate=1;
                }
            }
        }
        step--;


        //If Player Tank is near around, having a specific probability to fire (low probability)
        if(rate==2){
            if (r.nextInt(40) > 35) this.fire();
        }else if (r.nextInt(40) > 38) this.fire();
        */
    }

    @Override
    public void update() { move(); }

    @Override
    public void render(GraphicsContext gc) { drawRotatedImage(gc, entityImages[0], angle); }

    @Override
    public Shape getHitBox() {
        Rectangle hitBox = new Rectangle(x, y, WIDTH, HEIGHT);
        hitBox.getTransforms().add(new Rotate(angle, x + WIDTH/2,y + HEIGHT/2));

        return hitBox;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.Msg.id){
            case 0:
                System.out.println("you are deafeated by id "+ msg.Sender);
                return true;
            case 3:
                //this.direction = (DIRECTION) msg.ExtraInfo;
                System.out.println("chang the direction");
                return true;
            default:
                System.out.println("no match");
                return false;
        }

    }

    @Override
    public String toString() { return "Enemy"; }
}

package com.bham.bc.components.armory;

import com.bham.bc.entity.physics.BombTank;
import com.bham.bc.entity.Direction;
import com.bham.bc.utils.messaging.Telegram;
import com.bham.bc.entity.MovingEntity;
import com.bham.bc.components.characters.enemies.Enemy;
import com.bham.bc.components.characters.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static com.bham.bc.components.CenterController.backendServices;


abstract public class Bullet extends MovingEntity {
    /**
     * The Id of Player Tank that ownes the bullet
     */
    public int ownerTank;

/**
 * Constructor Of Tank_Bullet,Using an int ID to indicate which tank this Tank_bullet belongs to(OwnerTank)
 * @param owner
 * @param x
 * @param y
 * @par
 * */
    public Bullet(int owner,
                  int speedX, int speedY,
                  int x, int y,
                  int width, int length,
                  Direction dir) {

        super(speedX,speedY,
                x,y,
                width,length,
                dir);
        this.ownerTank = owner;

    }

    /**
     * A method to indicate if the Bullet has hit any tanks in the List Of tanks(enemyTanks)
     * If it hit at least one tank then return True
     * @param tanks
     * @return
     */
    public boolean hitTanks(List<Enemy> tanks) {
        for (int i = 0; i < tanks.size(); i++) {
            if (hitEnemyTank(tanks.get(i))) {
                return true;
            }
        }
        return false;
    }
    /**
     * A method to indicate if the Bullet has hit any specific Enemy Tank
     * The prerequisites of HIT is :
     * 1. The Bullet is alive
     * 2. The Enemy Tank is alive
     * 3. The bullet 's occupied size intersects with enemy's occupied size(Using getRect() to get size)
     * When it hits, we need to dispatch message (to Enemy Tank)
     * @param t
     * @return
     */
    public boolean hitEnemyTank(Enemy t) {

        //子弹活着
        //与敌人碰撞到了
        //敌人活着
        //子弹只作用在对方身上
        if (this.isAlive && this.intersects(t) && t.isAlive()) {

            BombTank e = new BombTank(t.getX(), t.getY());
            backendServices.addBombTank(e);
            t.setAlive(false);
            this.isAlive = false;

            return true;
        }
        return false;
    }

    /**
     * A method to indicate if the Bullet has hit any specific HomeTank Tank
     * If hit , the life of home tank minus 50.
     * @param t
     * @return
     */
    public boolean hitTank(Player t) {

        if (this.isAlive && this.intersects(t) && t.isAlive()) {

            BombTank e = new BombTank(t.getX(), t.getY());

            backendServices.addBombTank(e);

            if (t.isUser()) {
                t.setHp(t.getHp() - 50);
                if (t.getHp() <= 0)
                    t.setAlive(false);
            } else {
                t.setAlive(false);

            }

            this.isAlive = false;

            return true;
        }
        return false;
    }

    /**
     * A method to indicate if the bullet hits the bullet
     * If bullet hits the other bullet then it should be removed from bullet list(centerController will do that)
     * @param w
     * @return
     */
    public boolean hitBullet(Bullet w){
        if (this.isAlive && this.intersects(w)){
            this.isAlive =false;
            backendServices.removeBullet(w);
            return true;
        }
        return false;
    }

    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, length);
    }



    public boolean isAlive() {
        return isAlive;
    }

    /**
     * If bullet is not alive, then Entity manager should remove this
     */


    @Override
    public void render(GraphicsContext gc) {

    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    @Override
    protected void move() {
    }

    @Override
    public String toString() {
        return "Bullet type";
    }
}
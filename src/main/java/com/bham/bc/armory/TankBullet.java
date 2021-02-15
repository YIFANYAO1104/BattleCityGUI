package com.bham.bc.armory;

import com.bham.bc.BombTank;
import com.bham.bc.common.Direction;
import com.bham.bc.common.Messaging.Telegram;
import com.bham.bc.common.MovingEntity;
import com.bham.bc.environment.Impl.CommonWall;
import com.bham.bc.environment.Impl.Home;
import com.bham.bc.tank.Impl.Enemy;
import com.bham.bc.tank.Impl.HomeTank;

import java.awt.*;
import java.util.List;

import static com.bham.bc.CenterController.centerController;
import static com.bham.bc.EntityManager.EntityMgr;

abstract public class TankBullet extends MovingEntity {
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
    public TankBullet(int owner,
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
        if (this.live && this.getRect().intersects(t.getRect()) && t.isLive()) {

            BombTank e = new BombTank(t.getX(), t.getY());
            centerController.addBombTank(e);
            t.setLive(false);
            this.live = false;

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
    public boolean hitTank(HomeTank t) {

        if (this.live && this.getRect().intersects(t.getRect()) && t.isLive()) {

            BombTank e = new BombTank(t.getX(), t.getY());

            centerController.addBombTank(e);

            if (t.isUser()) {
                t.setLife(t.getLife() - 50);
                if (t.getLife() <= 0)
                    t.setLive(false);
            } else {
                t.setLive(false);

            }

            this.live = false;

            return true;
        }
        return false;
    }

    /**
     * A method to indicate if the bullet hits the wall
     * If bullet hits the wall then we should remove the wall from centerController
     * @param w
     * @return
     */
    public boolean hitWall(CommonWall w) {
        if (this.live && this.getRect().intersects(w.getRect())) {
            this.live = false;
            centerController.removeWall(w);
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
    public boolean hitBullet(TankBullet w){
        if (this.live && this.getRect().intersects(w.getRect())){
            this.live=false;
            centerController.removeBullet(w);
            return true;
        }
        return false;
    }
    /**
     * A method to indicate if the bullet hits  Home
     * If bullet hits Home then Home should be set to dead
     * @param h
     * @return
     */
    public boolean hitHome(Home h) {
        if (this.live && this.getRect().intersects(h.getRect())) {
            this.live = false;
            h.setLive(false);
            return true;
        }
        return false;
    }


    public Rectangle getRect() {
        return new Rectangle(x, y, width, length);
    }


    public boolean isLive() {
        return live;
    }

    /**
     * If bullet is not alive, then Entity manager should remove this
     */
    @Override
    public void update() {
        if(!live){
            EntityMgr.RemoveEntity(this);

        }

    }

    @Override
    public void render(Graphics g) {

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
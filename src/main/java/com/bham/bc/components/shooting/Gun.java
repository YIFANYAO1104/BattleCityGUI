package com.bham.bc.components.shooting;

import com.bham.bc.audio.SoundEffect;
import com.bham.bc.components.characters.GameCharacter;
import com.bham.bc.components.characters.Player;
import com.bham.bc.utils.GeometryEnhanced;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bham.bc.audio.AudioManager.audioManager;
import static com.bham.bc.components.Controller.services;
import static com.bham.bc.utils.Timer.CLOCK;

/**
 * This class controls the way the bullet can be fired with respect to the game timer. It must be bind to a character.
 */
public class Gun {
    private final GameCharacter CHARACTER;

    public BulletType getBulletType() {
        return bulletType;
    }

    public double getBulletSpeed() {
        return DefaultBullet.SPEED;
    }

    private BulletType bulletType;
    private LaserType laserType;
    private double damageFactor;
    private long lastTick;
    private long rate;

    /**
     * A default constructor to create gun with no bullets
     *
     * @param character {@link GameCharacter} the gun belongs to
     */
    public Gun(GameCharacter character) {
        CHARACTER = character;
        bulletType = null;
        damageFactor = 1;
        lastTick = CLOCK.getCurrentTime();
        rate = 0;
    }

    /**
     * Alternate constructor to create a gun with the specified bullet type
     *
     * @param character  {@link GameCharacter} the gun belongs to
     * @param bulletType type of bullet the gun should control
     */
    public Gun(GameCharacter character, BulletType bulletType,LaserType laserType) {
        CHARACTER = character;
        this.bulletType = bulletType;
        //this.laserType=BulletType.DefaultLaser;
        this.laserType=laserType;
        damageFactor = 1;
        lastTick = CLOCK.getCurrentTime();
        rate = bulletType.getMinRate();
    }

    /**
     * <p>Creates a new bullet object that depends on this class' <b>bulletType</b> parameter. If angle offset is set to 0,
     * this method creates a bullet straightly in front of the character just touching its "nose". Otherwise, the bullet is
     * rotated with respect to <i>its</i> center.</p>
     *
     * <p><b>Note:</b> the bullet is always spawned aligned with the center of the character's width</p>
     *
     * @param angleOffset offset by which the bullet should be rotated around its center
     * @return the created {@link Bullet} object with respect to the character's location
     */
    private Bullet spawnBullet(double angleOffset) {
        // Define the bullet's center coordinates
        double centerBulletX = CHARACTER.getCenterPosition().getX();
        double centerBulletY = CHARACTER.getPosition().getY() - bulletType.getHeight() / 2.0;

        // Rotate bullet's center point around character
        Rotate rotateAroundCharacter = new Rotate(CHARACTER.getAngle()+angleOffset, CHARACTER.getCenterPosition().getX(), CHARACTER.getCenterPosition().getY());
        Point2D transformedCenterXY = rotateAroundCharacter.transform(centerBulletX, centerBulletY);

        // Define the bullet's top left coordinates
        double topLeftBulletX = transformedCenterXY.getX() - bulletType.getWidth() / 2.0;
        double topLeftBulletY = transformedCenterXY.getY() - bulletType.getHeight() / 2.0;

        // Return an instance which corresponds to the bullet type
        switch(bulletType) {
            case ICE:
                return  new IceBullet(topLeftBulletX, topLeftBulletY, GeometryEnhanced.rotate(CHARACTER.getHeading(),angleOffset), CHARACTER.getSide());
            case EXPLOSIVE:
                return new ExplosiveBullet(topLeftBulletX, topLeftBulletY, GeometryEnhanced.rotate(CHARACTER.getHeading(),angleOffset), CHARACTER.getSide());
            default:
                return new DefaultBullet(topLeftBulletX, topLeftBulletY, GeometryEnhanced.rotate(CHARACTER.getHeading(),angleOffset), CHARACTER.getSide());
        }
    }

    /**
     * test the laser
     * @return the {@link LaserType} of the current laser
     */
    public LaserType testLaser(){
        return this.laserType;
    }
    /**
     * test the Bullet
     * @return the {@link Bullet} of the current Bullet
     */
    public BulletType testBullet(){
        return this.bulletType;
    }

    /**
     * <p>Creates a new laser object that depends on this class' <b>laserType</b> parameter. If angle offset is set to 0,
     * this method creates a bullet straightly in front of the character just touching its "nose". Otherwise, the laser is
     * rotated with respect to <i>its</i> center.</p>
     *
     * <p><b>Note:</b> the laser is always spawned aligned with the center of the character's width</p>
     *
     * @param angleOffset offset by which the laser should be rotated around its center
     * @return the created {@link Bullet} object with respect to the character's location
     */
    private Bullet spawnLaser(double angleOffset) {
        // Define the bullet's center coordinates
        double centerBulletX = CHARACTER.getCenterPosition().getX()-10;
        double centerBulletY = CHARACTER.getPosition().getY()-100;
        System.out.println("Thee center position is "+CHARACTER.getCenterPosition().getX());
        System.out.println("Thee center position is "+CHARACTER.getCenterPosition().getY());
        //- bulletType.getHeight() / 100.0;

        // Rotate bullet's center point around character
        Rotate rotateAroundCharacter = new Rotate(CHARACTER.getAngle(), CHARACTER.getCenterPosition().getX()-10, CHARACTER.getCenterPosition().getY()-120);
        Point2D transformedCenterXY = rotateAroundCharacter.transform(centerBulletX,centerBulletY-120);

        double centerBulletXT = CHARACTER.getCenterPosition().getX()-16;
        double centerBulletYT = CHARACTER.getPosition().getY()-150;
        Rotate rotateAroundCharacterThunder = new Rotate(CHARACTER.getAngle(), CHARACTER.getCenterPosition().getX()-16, CHARACTER.getCenterPosition().getY()-80);
        Point2D transformedCenterXYThunder = rotateAroundCharacterThunder.transform(centerBulletXT,centerBulletYT);

        // Define the bullet's top left coordinates
        double topLeftBulletX = transformedCenterXY.getX() ;//- //bulletType.getWidth() / 100.0;
        double topLeftBulletY = transformedCenterXY.getY() ;//-// //bulletType.getHeight() / 100.0;
        double topLeftBulletXT = transformedCenterXYThunder.getX() ;//- //bulletType.getWidth() / 100.0;
        double topLeftBulletYT = transformedCenterXYThunder.getY() ;

        // Return an instance which corresponds to the bullet type
        switch (laserType){
            case ThunderLaser:
                return new ThunderLaser(topLeftBulletXT, topLeftBulletYT, CHARACTER.getHeading(), CHARACTER.getSide());
            default:
                return new LaserGun(topLeftBulletX, topLeftBulletY, CHARACTER.getHeading(), CHARACTER.getSide());


        }

//        switch(bulletType) {
//            case IceBullet:
//                return  new IceBullet(topLeftBulletX, topLeftBulletY, CHARACTER.getHeading(), CHARACTER.getSide());
//            case EXPLOSIVE:
//                return new ExplosiveBullet(topLeftBulletX, topLeftBulletY, CHARACTER.getHeading(), CHARACTER.getSide());
//
//            default:
//                return new DefaultBullet(topLeftBulletX, topLeftBulletY, CHARACTER.getHeading(), CHARACTER.getSide());
//        }
    }


    /**
     * Shoots exactly 1 bullet in front of the character
     */
    public void shoot() {
        if(bulletType == null || CLOCK.getCurrentTime() - lastTick < rate) return;

        Bullet bullet = spawnBullet(0);
        bullet.setDamage(bullet.getDamage() * damageFactor);

        services.addBullet(bullet);
        audioManager.playEffect(getShotSoundEffect());
        lastTick = CLOCK.getCurrentTime();
    }
    /**
     * Shoots the dynamic laser  in front of the character
     */
    public void shootLaser() {
        if(laserType == null || CLOCK.getCurrentTime() - lastTick < rate) return;
        if(CHARACTER instanceof Player && !((Player) CHARACTER).laserFlag){
            return;
        }
        Bullet bullet = spawnLaser(0);
        bullet.setDamage(bullet.getDamage() * damageFactor*3);
        bullet.setTime();
        services.addBullet(bullet);
        lastTick = CLOCK.getCurrentTime();
    }

    /**
     * Shoots multiple bullets at the specified angles. The center spawn point is the same for all bullets.
     * @param angles any number or angles for each of which the bullet will be fired
     */
    public void shoot(double ...angles) {
        if(bulletType == null || CLOCK.getCurrentTime() - lastTick < rate) return;

        List<Bullet> bullets = Arrays.stream(angles).mapToObj(this::spawnBullet).collect(Collectors.toList());
        bullets.forEach(bullet -> {
            bullet.setDamage(bullet.getDamage() * damageFactor);
            services.addBullet(bullet);
            audioManager.playEffect(getShotSoundEffect());
        });
        lastTick = CLOCK.getCurrentTime();
    }

    /**
     * Updates the bullet type this gun is shooting
     * @param bulletType the type of bullet this gun will handle
     */
    public void setBulletType(BulletType bulletType) {
        this.bulletType = bulletType;
    }
    /**
     * Updates the laser type this gun is shooting
     * @param laser the type of laser this gun will handle
     */
    public void setLaserType(LaserType laser){
        this.laserType=laser;
    }

    /**
     * Sets this weapon's rate assuring it is not faster than the minimum rate
     * @param rate the milliseconds this gun should wait before allowing to shoot the next bullet
     */
    public void setRate(long rate) {
        if(bulletType != null) this.rate = Math.max(bulletType.getMinRate(), rate);
    }

    /**
     * Sets this gun's damage factor
     * @param factor value by which each bullet's damage will be multiplied upon spawn
     */
    public void setDamageFactor(double factor) {
        if(bulletType != null) damageFactor = factor;
    }

    /**
     * Gets the sound effect mapped to a requested type of bullet this gun will make when spawning a bullet
     * @return {@link SoundEffect} value which will provide audio object to play
     */
    public SoundEffect getShotSoundEffect() {
        switch(bulletType) {
            default:
                return SoundEffect.SHOT_DEFAULT;
        }
    }


    public double getMaxDamage() {
        List<Double> damageList = new ArrayList<>();
        damageList.add(IceBullet.DAMAGE);
        damageList.add(ExplosiveBullet.DAMAGE);
        damageList.add(DefaultBullet.DAMAGE);

        double max = Double.MIN_VALUE;
        for (Double d : damageList) {
            if (d>max){
                max=d;
            }
        }

        return max*damageFactor;
    }
}

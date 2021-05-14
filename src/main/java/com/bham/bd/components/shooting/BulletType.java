package com.bham.bd.components.shooting;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Enum class defining unmodifiable properties of any bullet type. These are used by bullet classes and the {@link Gun} class.
 */

public enum BulletType {
    DEFAULT(200, 6, 12, "img/shooting/default-bullet.png"),
    EXPLOSIVE(2000, 6, 12, "img/shooting/default-bullet.png"),
    ICE(2000, 6, 12, "img/shooting/2.png"),
    LASER(200, 20, 10, "img/shooting/l0.png");

    /** The cool-down (in milliseconds) before the next bullet can be fired */
    public final long MIN_RATE;

    /** The width of the bullet's image used for rendering */
    public final int WIDTH;

    /** The height of the bullet's image used for rendering */
    public final int HEIGHT;

    /** Path to the location of the bullet's image */
    private final String PATH_TO_IMAGE;

    /**
     * Constructs a specific bullet with the provided image.
     *
     * <p>The width and height properties will be used to render a correct image on graphics context. The size for the
     * hit-box is configured withing the bullet class itself. We use rate because we don't have a class for weapons so
     * each bullet is responsible for its own shooting rate.</p>
     *
     * @param minRate   minimum milliseconds gap between each bullet that is shot
     * @param width     width of the bullet image
     * @param height    height of the bullet image
     * @param imagePath path to the image the bullet will use for its picture
     */
    BulletType(long minRate, int width, int height, String imagePath) {
        this.MIN_RATE = minRate;
        this.WIDTH = width;
        this.HEIGHT = height;
        PATH_TO_IMAGE = imagePath;
    }

    /**
     * Gets bullet's rate
     * @return the cool-down (in milliseconds) before the next bullet can be fired
     */
    public long getMinRate() {
        return MIN_RATE;
    }

    /**
     * Gets the bullet's image
     * @return {@link Image} object that can be rendered on graphics context or {@code null} if it cannot be constructed
     */
    public Image getImage() {
        try {
            return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(PATH_TO_IMAGE)), WIDTH, HEIGHT, false, false);
        } catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }
}

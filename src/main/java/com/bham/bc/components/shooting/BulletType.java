package com.bham.bc.components.shooting;

import javafx.scene.image.Image;

/**
 * Enum class defining unmodifiable properties of any bullet type. These are used by bullet classes and the {@link Gun} class.
 */
public enum BulletType {
    DEFAULT(200, 6, 12, "file:src/main/resources/img/shooting/defaultBullet.png"),
    EXPLOSIVE(2000, 6, 12, "file:src/main/resources/img/shooting/defaultBullet.png"),
    IceBullet(2000, 6, 12, "file:src/main/resources/img/shooting/2.png"),
    DefaultLaser(200,20,10,
        new Image[]{
                (new Image("file:src/main/resources/img/shooting/l0.png")),
                (new Image("file:src/main/resources/img/shooting/l1.png")),
                (new Image("file:src/main/resources/img/shooting/l2.png")),
                (new Image("file:src/main/resources/img/shooting/l3.png")),
                (new Image("file:src/main/resources/img/shooting/l4.png")),
                (new Image("file:src/main/resources/img/shooting/l5.png")),
                (new Image("file:src/main/resources/img/shooting/l6.png")),
                (new Image("file:src/main/resources/img/shooting/l7.png")),
                (new Image("file:src/main/resources/img/shooting/l6.png")),
                (new Image("file:src/main/resources/img/shooting/l5.png")),
                (new Image("file:src/main/resources/img/shooting/l4.png")),
                (new Image("file:src/main/resources/img/shooting/l3.png")),
                (new Image("file:src/main/resources/img/shooting/l2.png")),
                (new Image("file:src/main/resources/img/shooting/l2.png")),
                (new Image("file:src/main/resources/img/shooting/l0.png")),}
            );

    private long minRate;
    private int width;
    private int height;
    private Image image;
    private  Image[] images;

    /**
     * <p>Constructs a specific bullet with the provided image. The width and height properties will be used to render
     * a correct image on graphics context. The size for the hit-box is configured withing the bullet class itself. We
     * use rate because we don't have a class for weapons so each bullet is responsible for its own shooting rate.</p>
     *
     * @param minRate   minimum milliseconds gap between each bullet that is shot
     * @param width     width of the bullet image
     * @param height    height of the bullet image
     * @param imagePath path to the image the bullet will use for its picture
     */
    BulletType(long minRate, int width, int height, String imagePath) {
        this.minRate = minRate;
        this.width = width;
        this.height = height;
        image = new Image(imagePath, width, height, false, false);
    }
    BulletType(long minRate, int width, int height, Image[] images) {
        this.minRate = minRate;
        this.width = width;
        this.height = height;
        images = images;
    }

    /**
     * Gets bullet's rate
     * @return the cool-down (in milliseconds) before the next bullet can be fired
     */
    public long getMinRate() {
        return minRate;
    }

    /**
     * Gets bullet's width
     * @return the width of the bullet's image used for rendering
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets bullet's height
     * @return the height of the bullet's image used for rendering
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the bullet's image
     * @return the Image object that can be rendered on graphics context
     */
    public Image getImage() {
        return image;
    }

    public Image[] getImages(){
        return images;
    }
}

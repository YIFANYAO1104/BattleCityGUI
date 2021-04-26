package com.bham.bc.components.shooting;

import javafx.scene.image.Image;

/**
 * Enum class defining unmodifiable properties of any bullet type. These are used by bullet classes and the {@link Gun} class.
 */
public enum LaserType {

    Default(2000,10,200,
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
            ),
    ThunderLaser(2000,10,200,
            new Image[]{
                    (new Image("file:src/main/resources/img/shooting/tl0.png")),
                    (new Image("file:src/main/resources/img/shooting/tl1.png")),
                    (new Image("file:src/main/resources/img/shooting/tl2.png")),
                    (new Image("file:src/main/resources/img/shooting/tl3.png")),
                    (new Image("file:src/main/resources/img/shooting/tl4.png")),
                    (new Image("file:src/main/resources/img/shooting/tl5.png")),
                    (new Image("file:src/main/resources/img/shooting/tl6.png")),
                    (new Image("file:src/main/resources/img/shooting/tl7.png")),
                    (new Image("file:src/main/resources/img/shooting/tl8.png")),
                    (new Image("file:src/main/resources/img/shooting/tl9.png")),
                    (new Image("file:src/main/resources/img/shooting/tl10.png")),
                    (new Image("file:src/main/resources/img/shooting/tl11.png")),
                    (new Image("file:src/main/resources/img/shooting/tl12.png")),

    });
    private long minRate;
    private int width;
    private int height;
    private Image[] image;

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
    LaserType(long minRate, int width, int height, Image[] image) {
        this.minRate = minRate;
        this.width = width;
        this.height = height;
        this.image = image;
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
    public Image[] getImage() {
        return image;
    }
}

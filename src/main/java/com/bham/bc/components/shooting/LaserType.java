package com.bham.bc.components.shooting;

import javafx.scene.image.Image;

/**
 * Enum class defining unmodifiable properties of any laser type. These are used by laser classes and the {@link Gun} class.
 */
public enum LaserType {


    Default(10,200,
                new Image[]{
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l0.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l1.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l4.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l5.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l6.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l7.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l6.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l5.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l4.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l3.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l2.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/l0.png"))),}
            ),
    ThunderLaser(10,200,
            new Image[]{
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl0.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl1.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl2.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl3.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl4.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl5.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl6.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl7.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl8.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl9.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl10.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl11.png"))),
                    (new Image(LaserType.class.getClassLoader().getResourceAsStream("img/shooting/tl12.png"))),

    });
    private long minRate;
    private int width;
    private int height;
    private Image[] image;

    /**
     * <p>Constructs a specific type of laser with the provided images. The width and height properties will be used to render
     * a correct image on graphics context. The size for the hit-box is configured withing the laser class itself.</p>
     *
     * @param width     width of the laser image
     * @param height    height of the laser image
     * @param image path to the image the bullet will use for its picture
     */
    LaserType( int width, int height, Image[] image) {
        this.width = width;
        this.height = height;
        this.image = image;
    }



    /**
     * Gets laser 's width
     * @return the width of the laser's image used for rendering
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets laser's height
     * @return the height of the laser's image used for rendering
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the laser's image
     * @return the Image object that can be rendered on graphics context
     */
    public Image[] getImage() {
        return image;
    }
}

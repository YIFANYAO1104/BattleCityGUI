package com.bham.bc.components.environment;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Arrays;

public enum Tileset {
    ASHLANDS("img/tilesets/ashlands.png", 16,16),
    ATLANTIS("img/tilesets/atlantis.png", 16,16),
    BASIC("img/tilesets/basic.png", 16,16),
    BASIC_BLUE("img/tilesets/basic-blue.png", 16,16),
    ZWATER("img/tilesets/Zwater1(80x32).png", 80,32),
    GREEN_IRON("img/tilesets/greenIron.png", 16,16),
    BRICKS2("img/tilesets/bricks2.png", 16,16),
    MOCKUP("img/tilesets/mockup.png", 16,16),
    OLDTILESET("img/tilesets/oldTileset.png", 16,16),
    FLOOR("img/tilesets/floor.png", 16,16),
    ROCKS("img/tilesets/rocks.png", 16,16),
    PIMPLES("img/tilesets/pimples.png",16,16);

    private final Image[] TILES;
    private final int OFFSET_Y;

    /**
     * Constructs Tileset enum
     * @param path       path to tileset
     * @param tileWidth  width of one tile
     * @param tileHeight height of one tile
     */
    Tileset(String path, int tileWidth, int tileHeight) {
        OFFSET_Y = tileHeight-16;
        Image tileset = new Image(getClass().getClassLoader().getResourceAsStream(path));
        TILES = toSubImages(tileset, tileWidth, tileHeight);
    }

    /**
     * This method takes image and splits it to array of smaller images based on requested size
     * @param image          picture to be converted to array of sub-images
     * @param subImageWidth  requested width for every sub-image
     * @param subImageHeight requested height for every sub-image
     * @return Image array that is acquired from converting a given image to smaller ones
     */
    public static Image[] toSubImages(Image image, int subImageWidth, int subImageHeight) {
        int rows = (int) image.getHeight() / subImageHeight;
        int cols = (int) image.getWidth() / subImageWidth;

        Image[] subImages = new Image[rows * cols];
        PixelReader pr = image.getPixelReader();

        for(int i = 0; i < subImages.length; i++) {
            WritableImage subImage = new WritableImage(subImageWidth, subImageHeight);
            PixelWriter pw = subImage.getPixelWriter();

            int y0 = (i / cols) * subImageHeight;
            int x0 = (i % cols) * subImageWidth;

            for(int y = y0; y < y0 + subImageHeight; y++) {
                for(int x = x0; x < x0 + subImageWidth; x++) {
                    Color color = pr.getColor(x, y);
                    pw.setColor(x - x0, y - y0, color);
                }
            }
            subImages[i] = subImage;
        }
        return subImages;
    }

    /**
     * Gets the specified tile
     * @param i index of tile
     * @return image of tile at the specified index
     */
    public Image getTile(int i) {
        return TILES[i];
    }

    /**
     * Gets the specified tiles
     * @param a array of indexes
     * @return array of images at the specified indexes
     */
    public Image[] getTiles(int[] a) {
        return Arrays.stream(a).mapToObj(this::getTile).toArray(Image[]::new);
    }

    /**
     * Gets y offset
     * @return y offset
     */
    public int getOffsetY() {
        return OFFSET_Y;
    }
}

package com.bham.bc.components.environment;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Arrays;

public enum Tileset {
    ASHLANDS("file:src/main/resources/img/tiles/tilesets/ashlands.png", 16,16),
    ATLANTIS("file:src/main/resources/img/tiles/tilesets/atlantis.png", 16,16),
    BASIC("file:src/main/resources/img/tiles/tilesets/basic.png", 16,16),
    ZWATER("file:src/main/resources/img/tiles/tilesets/Zwater1(80x32).png", 80,32);

    private Image[] tiles;

    private int yOffset;

    /**
     * Constructs TILESET enum
     * @param path path to tileset
     * @param tileWidth width and height of one tile
     */
    Tileset(String path, int tileWidth,int tileHeight) {
        yOffset = tileHeight-16;
        Image tileset = new Image(path);
        int rows = (int) tileset.getHeight() / tileHeight;
        int cols = (int) tileset.getWidth() / tileWidth;

        tiles = new Image[rows * cols];

        PixelReader pr = tileset.getPixelReader();

        for(int i = 0; i < tiles.length; i++) {
            WritableImage tile = new WritableImage(tileWidth, tileHeight);
            PixelWriter pw = tile.getPixelWriter();

            int y0 = (i / cols) * tileHeight;
            int x0 = (i % cols) * tileWidth;

            for(int y = y0; y < y0 + tileHeight; y++) {
                for(int x = x0; x < x0 + tileWidth; x++) {
                    Color color = pr.getColor(x, y);
                    pw.setColor(x - x0, y - y0, color);
                }
            }
            tiles[i] = tile;
        }
    }

    /**
     * gets the specified tile
     * @param i index of tile
     * @return image of tile at the specified index
     */
    public Image getTile(int i) { return tiles[i]; }

    /**
     * gets the specified tiles
     * @param a array of indexes
     * @return array of images at the specified indexes
     */
    public Image[] getTiles(int[] a) { return Arrays.stream(a).mapToObj(this::getTile).toArray(Image[]::new); }

    public int getyOffset() {
        return yOffset;
    }
}

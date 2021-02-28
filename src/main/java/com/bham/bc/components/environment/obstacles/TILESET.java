package com.bham.bc.components.environment.obstacles;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public enum TILESET {
    TILES("file:src/main/resources/img/tiles/ashlands.png", 16);

    private Image[] tiles;

    /**
     * Constructs TILESET enum
     * @param path path to tileset
     * @param tileSize width and height of one tile
     */
    TILESET(String path, int tileSize) {
        Image tileset = new Image(path);
        int rows = (int) tileset.getHeight() / tileSize;
        int cols = (int) tileset.getWidth() / tileSize;

        tiles = new Image[rows * cols];

        PixelReader pr = tileset.getPixelReader();

        for(int i = 0; i < tiles.length; i++) {
            WritableImage tile = new WritableImage(tileSize, tileSize);
            PixelWriter pw = tile.getPixelWriter();

            int y0 = (i / cols) * tileSize;
            int x0 = (i % cols) * tileSize;

            for(int y = y0; y < y0 + tileSize; y++) {
                for(int x = x0; x < x0 + tileSize; x++) {
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
}

package com.bham.bc.components.environment.obstacles;

import com.bham.bc.components.environment.Obstacle;
import com.bham.bc.components.environment.maploaders.Tileset;
import javafx.scene.image.Image;

import java.util.EnumSet;

/**
 * Tile which represents the center of the area surrounded by tiles of type {@link EnemySpawnArea}
 */
public class EnemySpawnCenter extends Obstacle {
    /**
     * Constructs an obstacle
     *
     * @param x       position in x axis
     * @param y       position in y axis
     * @param tileset type of tileset
     * @param tileIDs IDs of tiles in case the obstacle is animated
     */
    public EnemySpawnCenter(int x, int y, Tileset tileset, int... tileIDs) {
        super(x, y, tileset, tileIDs);
    }

    @Override
    public EnumSet<Attribute> getAttributes() {
        return EnumSet.of(Attribute.ENEMY_SPAWN_CENTER, Attribute.ENEMY_SPAWN_AREA, Attribute.WALKABLE);
    }

    @Override
    protected Image[] getDefaultImage() {
        return new Image[] { new Image("file:src/main/resources/img/tiles/DefaultTiles/icewall.jpg") };
    }
}

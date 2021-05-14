package com.bham.bd.components.environment;

import javafx.embed.swing.JFXPanel;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static junit.framework.TestCase.fail;

/**
 * <h1>Tests for loading a map</h1>
 * <ul>
 *     <li>Tests the construction of {@link Obstacle}</li>
 *     <li>Tests the construction of every value of {@link Tileset}</li>
 * </ul>
 */
@RunWith(JUnitParamsRunner.class)
public class MapLoaderTest {
    private Object[] illegalTileIDs() {
        return new Object[][]{
                { null },
                { new int[]{} },
                { new int[]{ 999999 } },
                { new int[]{ -1 }}
        };
    }

    @Test
    @Parameters(method = "illegalTileIDs")
    @TestCaseName("[{index}] Illegal IDs test: {params}")
    public void shouldNotCreateObstaclesWithIllegalIDs(int... tileIDs) {
        new JFXPanel();

        try {
            new Obstacle(0, 0, null, Tileset.ASHLANDS, tileIDs);
            fail();
        } catch (Exception e) {
            assert(e instanceof IllegalArgumentException
                || e instanceof ArrayIndexOutOfBoundsException
                || e instanceof NullPointerException);
        }
    }

    @Test
    public void shouldParseTilesetToArrayOfTiles() {
        Arrays.stream(Tileset.values()).forEach(tileset -> {
            try {
                tileset.getTile(0); // Check if a tile can be acquired from an array of tiles
            } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
                fail();
            }
        });
    }
}

package com.bham.bc.components;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Interface defining the required frontend services for a mode to properly work
 */
public interface FrontendServices {
    // GAME PROGRESS
    /**
     * Checks if the game has ended
     * @return true if the game ended and false otherwise
     */
    boolean isGameOver();

    /**
     * Gets player HP
     * @return current HP
     */
    double getPlayerHP();

    // UI
    /**
     * Monitors the keyboard button presses and creates a corresponding GUI response
     * @param e key to handle
     */
    void keyPressed(KeyEvent e);

    /**
     * Monitors the keyboard button releases and creates a corresponding GUI response
     * @param e key to handle
     */
    void keyReleased(KeyEvent e);

    // OTHER
    /**
     * Renders backend content
     * @param gc graphics context where things will be rendered
     */
    void render(GraphicsContext gc);


    // TEMPORARY
}

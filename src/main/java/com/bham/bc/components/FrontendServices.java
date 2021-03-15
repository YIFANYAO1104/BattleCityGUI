package com.bham.bc.components;

import com.bham.bc.components.characters.TrackableCharacter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public interface FrontendServices {

    TrackableCharacter getHomeTank();
    void update();
    void render(GraphicsContext gc);
    void renderHitBoxes(AnchorPane gamePane);


    boolean isWin();

    /**
     * Status to indicates defeat
     * Home destroyed OR Player Tank dead
     * @return
     */
    boolean isLoss();

    /**
     * Get the number of enemy tanks from enemy tank list
     * @return
     */
    int getEnemyNumber();
    /**
     *
     * @return Current HP of Player Tank
     */
    int getLife();
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is released and use as Parameter to determine Player Tank's Action
     * @param e
     */
    void keyReleased(KeyEvent e);
    /**
     * A method to monitor the actions from keyBoard
     * Which Key is pressed and use as Parameter to determine Player Tank's Action
     * @param e
     */
    void keyPressed(KeyEvent e);



    /**
     * Clear all objects on tht map
     */
    void clear();
}

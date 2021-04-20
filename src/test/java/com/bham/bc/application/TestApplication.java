package com.bham.bc.application;

import com.bham.bc.view.GameSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * <h1>Test Application</h1>
 *
 * <p>Represents a dummy application for testing purposes. It allows to set up a custom pane to test things which require
 * user interaction and verification. It has an additional bottom pane which allows the tester themselves to either fail
 * or pass the test.</p>
 *
 * <p><b>Note:</b> as this is a dummy application, we do not need to test it. If something in this class does not behave
 * expectedly, things can just be altered on the spot or another test application fitting the needs can be created.</p>
 */
public class TestApplication extends Application {
    static boolean failed = false;
    static Pane testPane = new Pane();

    /**
     * Constructs this test application
     */
    public TestApplication() {
        super();
    }

    /**
     * Sets a custom pane the tester will be presented with
     * @param pane pane containing different UI elements to test
     */
    public void setTestPane(Pane pane) {
        testPane = pane;

        if(testPane.getPrefWidth() <= 0) {
            testPane.setPrefWidth(Math.max(testPane.getMinWidth(), testPane.getMaxWidth()));
            testPane.setPrefWidth(Math.max(testPane.getPrefWidth(), testPane.getWidth()));
        }

        if(testPane.getPrefHeight() <= 0) {
            testPane.setPrefHeight(Math.max(testPane.getMinHeight(), testPane.getMaxHeight()));
            testPane.setPrefWidth(Math.max(testPane.getPrefHeight(), testPane.getHeight()));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set up the pass/fail buttons
        Button passBtn = new Button("Pass");
        Button failBtn = new Button("Fail");

        passBtn.setStyle("-fx-pref-width: 200; -fx-pref-height: 30; -fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold");
        failBtn.setStyle("-fx-pref-width: 200; -fx-pref-height: 30; -fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold");

        passBtn.setOnMouseClicked(e -> Platform.exit());
        failBtn.setOnMouseClicked(e -> { failed = true; Platform.exit(); });

        // Set up the layout for the pass/fail buttons
        HBox validationPane = new HBox();
        validationPane.setAlignment(Pos.CENTER);
        validationPane.setSpacing((testPane.getPrefWidth() - (passBtn.getPrefWidth() + failBtn.getPrefWidth())) / 3);
        validationPane.setPrefHeight(70);
        validationPane.getChildren().addAll(passBtn, failBtn);

        // Add the test pane and the validation pane with pass/fail buttons
        VBox container = new VBox();
        container.getChildren().addAll(testPane, validationPane);

        // Set up scene and stage
        Scene testScene = new Scene(container);
        primaryStage.setScene(testScene);
        primaryStage.show();
    }

    /**
     * Launches the test application
     */
    public void run() {
        launch();
    }

    /**
     * Gets the status of the test
     * @return {@code true} if the tester clicked "Fail" and {@code false} otherwise
     */
    public boolean getFailed() {
        return failed;
    }
}

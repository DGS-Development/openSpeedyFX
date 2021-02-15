package de.dgs.apps.osfxe.scenes;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.layout.Region;

/**
 * Helper class to create {@link Scene} or {@link SubScene} objects.
 */
public class SceneCreator {
    /**
     * Creates a {@link SubScene} unless the {@link Parent} element is a {@link Group}.
     * @param gameController The {@link GameController} containing the {@link Parent} element.
     * @return The created {@link SubScene} or null if the {@link Parent} element isn't a {@link Region} or {@link Control}.
     */
    public static SubScene createSubScene(GameController gameController) {
        double sceneWidth = 0;
        double sceneHeight = 0;

        Parent rootNode = gameController.getRootNode();

        if(rootNode instanceof Region) {
            Region region = (Region) rootNode;
            sceneWidth = region.getPrefWidth();
            sceneHeight = region.getPrefHeight();
        }
        else if(rootNode instanceof Control) {
            Control control = (Control) rootNode;
            sceneWidth = control.getWidth();
            sceneHeight = control.getHeight();
        }
        else {
            return null;
        }

        return new SubScene(rootNode, sceneWidth, sceneHeight);
    }

    /**
     * Creates a new {@link Scene}, based on a {@link Parent} element.
     * @param gameController The {@link GameController} containing the {@link Parent} element.
     * @return The created {@link Scene}.
     */
    public static Scene createScene(GameController gameController) {
        return new Scene(gameController.getRootNode());
    }
}

package de.dgs.apps.osfxe.scenes;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.layout.Region;

/*
Copyright 2021 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

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

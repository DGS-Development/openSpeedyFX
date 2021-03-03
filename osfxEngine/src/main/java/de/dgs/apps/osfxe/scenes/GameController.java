package de.dgs.apps.osfxe.scenes;

import javafx.fxml.FXML;
import javafx.scene.Parent;

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
 * The base class for all scenes.
 */
public abstract class GameController {
    private boolean initialized = false;
    private Parent rootNode;

    protected GameController() {
        //Ignore...
    }

    protected void setRootNode(Parent rootNode) {
        this.rootNode = rootNode;
        onRootSet();
    }

    /**
     * Returns the loaded {@link Parent} root node.
     * @return the loaded {@link Parent} root node or null, if it wasn't set.
     */
    public Parent getRootNode() {
        return rootNode;
    }

    /**
     * Returns the FXML path coressponding to the {@link GameController}.
     * @return The path of the FXML-resource.
     */
    public abstract String getFxmlPath();

    /**
     * Gets executed if the FXML elements of the controller were initialized.
     */
    public abstract void onInitialized();

    /**
     * Gets executed if the root node was set.
     */
    public void onRootSet() {
        //Ignore...
    }

    @FXML
    public void initialize() {
        initialized = true;
        onInitialized();
    }

    /**
     * Returns true if controller was initialized.
     * @return True if the FXML elements of the controller were initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
}

package de.dgs.apps.osfxe.scenes;

import javafx.fxml.FXML;
import javafx.scene.Parent;

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

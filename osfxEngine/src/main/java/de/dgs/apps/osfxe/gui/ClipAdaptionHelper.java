package de.dgs.apps.osfxe.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ClipAdaptionHelper {
    private final DoubleProperty widthChangeProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightChangeProperty = new SimpleDoubleProperty();

    public void bindClipAdaption(Pane rootNode) {
        widthChangeProperty.bind(rootNode.widthProperty());
        heightChangeProperty.bind(rootNode.heightProperty());

        widthChangeProperty.addListener((observable, oldValue, newValue) -> {
            rootNode.setClip(new Rectangle(newValue.doubleValue(), heightChangeProperty.get()));
        });

        heightChangeProperty.addListener((observable, oldValue, newValue) -> {
            rootNode.setClip(new Rectangle(widthChangeProperty.get(), newValue.doubleValue()));
        });
    }

    public void unbindClipAdaption(Pane rootNode) {
        widthChangeProperty.unbind();
        heightChangeProperty.unbind();
    }

    public double getWidthChangeProperty() {
        return widthChangeProperty.get();
    }

    public double getHeightChangeProperty() {
        return heightChangeProperty.get();
    }
}

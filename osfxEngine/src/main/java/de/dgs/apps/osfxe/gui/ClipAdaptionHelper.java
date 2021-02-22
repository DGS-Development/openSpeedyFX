package de.dgs.apps.osfxe.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

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
 * Helper class to adapt the clip-property for a {@link Pane} element, by observing its width- and height-property.
 */
public class ClipAdaptionHelper {
    private final DoubleProperty widthChangeProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightChangeProperty = new SimpleDoubleProperty();

    /**
     * Causes the adaption of the clip-property for a {@link Pane} element, if its width or height gets changed.
     * @param rootNode The {@link Pane} element to adapt the clip-property for.
     */
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

    /**
     * Removes the adaption of the clip-property for the bound {@link Pane} element.
     */
    public void unbindClipAdaption() {
        widthChangeProperty.unbind();
        heightChangeProperty.unbind();
    }

    /**
     * Returns the width-property-value for the bound {@link Pane} element.
     * @return The current width.
     */
    public double getWidthChangeProperty() {
        return widthChangeProperty.get();
    }

    /**
     * Returns the height-property-value for the bound {@link Pane} element.
     * @return The current height.
     */
    public double getHeightChangeProperty() {
        return heightChangeProperty.get();
    }
}

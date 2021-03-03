package de.dgs.apps.osfxe.gui.zoomingpane;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

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
 * {@link Pane} class to facilitate zooming.
 */
public class ZoomingPane extends Pane {
    public static final int DEFAULT_PREFERRED_WIDTH = 600;
    public static final int DEFAULT_PREFERRED_HEIGHT = 600;

    private DoubleProperty scaleProperty;

    /**
     * Creates a new {@link ZoomingPane} with a preferred size.
     * @param prefWidth The preferred width.
     * @param prefHeight The preferred height.
     */
    public ZoomingPane(int prefWidth, int prefHeight) {
        setPrefSize(prefWidth, prefHeight);
        setupScaleProperty();
    }

    /**
     * Creates a new {@link ZoomingPane} using the computed size.
     */
    public ZoomingPane() {
        setMinWidth(USE_COMPUTED_SIZE);
        setMinHeight(USE_COMPUTED_SIZE);

        setPrefWidth(USE_COMPUTED_SIZE);
        setPrefHeight(USE_COMPUTED_SIZE);

        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        setupScaleProperty();
    }

    private void setupScaleProperty() {
        scaleProperty = new SimpleDoubleProperty(1.0);

        scaleXProperty().bind(scaleProperty);
        scaleYProperty().bind(scaleProperty);
    }

    /**
     * Returns the current zoom-factor (scale) of the {@link ZoomingPane}.
     * @return The current scale.
     */
    public double getScale() {
        return scaleProperty.get();
    }

    /**
     * Sets the current zoom-factor (scale) of the {@link ZoomingPane}.
     */
    public void setScale(double scale) {
        scaleProperty.set(scale);
    }

    /**
     * Sets to pivot-values for the {@link ZoomingPane}.
     * @param x The new translate-x-value (current value minus x).
     * @param y The new translate-y-value (current value minus y).
     */
    public void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }
}
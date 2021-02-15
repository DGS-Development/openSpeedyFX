package de.dgs.apps.osfxe.gui.zoomingpane;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

public class ZoomingPane extends Pane {
    public static final int DEFAULT_PREFERRED_WIDTH = 600;
    public static final int DEFAULT_PREFERRED_HEIGHT = 600;

    private DoubleProperty scaleProperty;

    public ZoomingPane(int prefWidth, int prefHeight) {
        setPrefSize(prefWidth, prefHeight);
        setupScaleProperty();
    }

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

    public double getScale() {
        return scaleProperty.get();
    }

    public void setScale(double scale) {
        scaleProperty.set(scale);
    }

    public void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }
}
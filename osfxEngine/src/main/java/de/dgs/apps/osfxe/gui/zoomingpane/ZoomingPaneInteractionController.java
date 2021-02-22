package de.dgs.apps.osfxe.gui.zoomingpane;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
 * Helper class to manipulate a {@link ZoomingPane}. It supports zooming (by scrolling) and moving (by dragging).
 */
public class ZoomingPaneInteractionController {
    public static final double DEFAULT_MOUSE_DELTA_FACTOR = 1.2;
    public static final double DEFAULT_MIN_SCALE = 0.1d;
    public static final double DEFAULT_MAX_SCALE = 10.0d;

    private final DragContext sceneDragContext = new DragContext();

    private final ZoomingPane zoomingPane;
    private boolean usePrimaryMouseButton;
    private boolean useZoom;
    private final double mouseDeltaFactor;
    private final double minScale;
    private final double maxScale;
    private boolean disabled;

    /**
     * Creates a new {@link ZoomingPaneInteractionController}, using the default zooming parameters and default mouse delta factor.
     * @param zoomingPane The {@link ZoomingPane} to manipulate.
     * @param usePrimaryMouseButton True if the primary mouse button is used for dragging. False for the secondary button.
     * @param useZoom True if zooming by scrolling is enabled.
     */
    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom) {
        this(zoomingPane, usePrimaryMouseButton, useZoom, DEFAULT_MIN_SCALE, DEFAULT_MAX_SCALE);
    }

    /**
     * Creates a new {@link ZoomingPaneInteractionController}, using the default mouse delta factor.
     * @param zoomingPane The {@link ZoomingPane} to manipulate.
     * @param usePrimaryMouseButton True if the primary mouse button is used for dragging. False for the secondary button.
     * @param useZoom True if zooming by scrolling is enabled.
     * @param minScale The minimal zooming scale.
     * @param maxScale The maximal zooming scale.
     */
    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom, double minScale, double maxScale) {
        this(zoomingPane, usePrimaryMouseButton, useZoom, minScale, maxScale, DEFAULT_MOUSE_DELTA_FACTOR);
    }

    /**
     * Creates a new {@link ZoomingPaneInteractionController}.
     * @param zoomingPane The {@link ZoomingPane} to manipulate.
     * @param usePrimaryMouseButton True if the primary mouse button is used for dragging. False for the secondary button.
     * @param useZoom True if zooming by scrolling is enabled.
     * @param minScale The minimal zooming scale.
     * @param maxScale The maximal zooming scale.
     * @param mouseDeltaFactor The mouse delta factor to zoom in and out.
     */
    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom, double minScale, double maxScale,
                                            double mouseDeltaFactor) {
        this(zoomingPane, usePrimaryMouseButton, useZoom, minScale, maxScale, mouseDeltaFactor, false);
    }

    /**
     * Creates a new {@link ZoomingPaneInteractionController}.
     * @param zoomingPane The {@link ZoomingPane} to manipulate.
     * @param usePrimaryMouseButton True if the primary mouse button is used for dragging. False for the secondary button.
     * @param useZoom True if zooming by scrolling is enabled.
     * @param minScale The minimal zooming scale.
     * @param maxScale The maximal zooming scale.
     * @param mouseDeltaFactor The mouse delta factor to zoom in and out.
     * @param disabled True if the interaction is disabled by default.
     */
    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom, double minScale, double maxScale,
                                            double mouseDeltaFactor, boolean disabled) {
        this.zoomingPane = zoomingPane;
        this.usePrimaryMouseButton = usePrimaryMouseButton;
        this.useZoom = useZoom;
        this.mouseDeltaFactor = mouseDeltaFactor;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.disabled = disabled;

        registerEventFilters(zoomingPane);
    }

    private void registerEventFilters(Pane pane) {
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        pane.addEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<>() {
        public void handle(MouseEvent event) {
            if (disabled)
                return;

            if (usePrimaryMouseButton) {
                if (!event.isPrimaryButtonDown())
                    return;
            } else {
                if (!event.isSecondaryButtonDown())
                    return;
            }

            sceneDragContext.setMouseAnchorX(event.getSceneX());
            sceneDragContext.setMouseAnchorY(event.getSceneY());

            sceneDragContext.setTranslateAnchorX(zoomingPane.getTranslateX());
            sceneDragContext.setTranslateAnchorY(zoomingPane.getTranslateY());
        }
    };

    private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<>() {
        public void handle(MouseEvent event) {
            if (disabled)
                return;

            if (usePrimaryMouseButton) {
                if (!event.isPrimaryButtonDown())
                    return;
            } else {
                if (!event.isSecondaryButtonDown())
                    return;
            }

            zoomingPane.setTranslateX(sceneDragContext.getTranslateAnchorX() + event.getSceneX() - sceneDragContext.getMouseAnchorX());
            zoomingPane.setTranslateY(sceneDragContext.getTranslateAnchorY() + event.getSceneY() - sceneDragContext.getMouseAnchorY());

            event.consume();
        }
    };

    private final EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {
            if (disabled || !useZoom)
                return;

            double newScale = zoomingPane.getScale();
            double oldScale = newScale;

            if (event.getDeltaY() < 0) {
                newScale /= mouseDeltaFactor;
            } else {
                newScale *= mouseDeltaFactor;
            }

            newScale = snapToNextScale(newScale, minScale, maxScale);
            zoomingPane.setScale(newScale);

            double pivotFactor = (newScale / oldScale) - 1;
            double deltaX = (event.getSceneX() - (zoomingPane.getBoundsInParent().getWidth() / 2 + zoomingPane.getBoundsInParent().getMinX()));
            double deltaY = (event.getSceneY() - (zoomingPane.getBoundsInParent().getHeight() / 2 + zoomingPane.getBoundsInParent().getMinY()));

            zoomingPane.setPivot(pivotFactor * deltaX, pivotFactor * deltaY);

            event.consume();
        }
    };

    private static double snapToNextScale(double scaleToSet, double minimumScale, double maximumScale) {
        if(Double.compare(scaleToSet, minimumScale) < 0)
            return minimumScale;

        if(Double.compare(scaleToSet, maximumScale) > 0)
            return maximumScale;

        return scaleToSet;
    }

    /**
     * Returns the {@link MouseEvent} handler, triggered if a mouse button is pressed.
     * @return The {@link EventHandler}.
     */
    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    /**
     * Returns the {@link MouseEvent} handler, triggered if a mouse drag event is started.
     * @return The {@link EventHandler}.
     */
    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    /**
     * Returns the {@link ScrollEvent} handler, triggered if a mouse scroll event occurs.
     * @return The {@link EventHandler}.
     */
    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    /**
     * Returns true if the primary mouse button is used for dragging. False for the secondary button.
     * @return True if the primary mouse button is used.
     */
    public boolean isUsePrimaryMouseButton() {
        return usePrimaryMouseButton;
    }

    /**
     * Sets the mouse button used for dragging.
     * @param usePrimaryMouseButton True if the primary mouse button is used for dragging. False for the secondary button.
     */
    public void setUsePrimaryMouseButton(boolean usePrimaryMouseButton) {
        this.usePrimaryMouseButton = usePrimaryMouseButton;
    }

    /**
     * Returns true if zooming by scrolling is enabled.
     * @return True if zooming is enabled.
     */
    public boolean isUseZoom() {
        return useZoom;
    }

    /**
     * Enables or disables zooming.
     * @param useZoom True if zooming is enabled.
     */
    public void setUseZoom(boolean useZoom) {
        this.useZoom = useZoom;
    }

    /**
     * True if all interaction is disabled.
     * @return True if all interaction is disabled.
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Enables or disables all interaction.
     * @param disabled True if all interaction is disabled.
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}

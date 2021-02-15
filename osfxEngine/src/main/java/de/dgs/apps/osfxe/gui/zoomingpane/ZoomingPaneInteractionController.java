package de.dgs.apps.osfxe.gui.zoomingpane;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class ZoomingPaneInteractionController {
    public static final double DEFAULT_MOUSE_DELTA_FACTOR = 1.2;
    public static final double DEFAULT_MIN_SCALE = 0.1d;
    public static final double DEFAULT_MAX_SCALE = 10.0d;

    private DragContext sceneDragContext = new DragContext();

    private ZoomingPane zoomingPane;
    private boolean usePrimaryMouseButton;
    private boolean useZoom;
    private double mouseDeltaFactor;
    private double minScale;
    private double maxScale;
    private boolean disabled;

    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom) {
        this(zoomingPane, usePrimaryMouseButton, useZoom, DEFAULT_MIN_SCALE, DEFAULT_MAX_SCALE);
    }

    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom, double minScale, double maxScale) {
        this(zoomingPane, usePrimaryMouseButton, useZoom, minScale, maxScale, DEFAULT_MOUSE_DELTA_FACTOR);
    }

    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom, double minScale, double maxScale,
                                            double mouseDeltaFactor) {
        this(zoomingPane, usePrimaryMouseButton, useZoom, minScale, maxScale, mouseDeltaFactor, false);
    }

    public ZoomingPaneInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton, boolean useZoom, double minScale, double maxScale,
                                            double mouseDeltaFactor, boolean disabled) {
        this.zoomingPane = zoomingPane;
        this.usePrimaryMouseButton = usePrimaryMouseButton;
        this.useZoom = useZoom;
        this.mouseDeltaFactor = mouseDeltaFactor;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.disabled = disabled;
    }

    public void registerEventFilters(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    public void registerEventFilters(Pane pane) {
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        pane.addEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    public void registerEventFilters(Node node) {
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        node.addEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    public void unregisterEventFilters(Scene scene) {
        scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        scene.removeEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    public void unregisterEventFilters(Pane pane) {
        pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        pane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        pane.removeEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    public void unregisterEventFilters(Node node) {
        node.removeEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        node.removeEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
        node.removeEventFilter(ScrollEvent.ANY, getOnScrollEventHandler());
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            if(disabled)
                return;

            if(usePrimaryMouseButton) {
                if(!event.isPrimaryButtonDown())
                    return;
            }
            else {
                if(!event.isSecondaryButtonDown())
                    return;
            }

            sceneDragContext.setMouseAnchorX(event.getSceneX());
            sceneDragContext.setMouseAnchorY(event.getSceneY());

            sceneDragContext.setTranslateAnchorX(zoomingPane.getTranslateX());
            sceneDragContext.setTranslateAnchorY(zoomingPane.getTranslateY());
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            if(disabled)
                return;

            if(usePrimaryMouseButton) {
                if(!event.isPrimaryButtonDown())
                    return;
            }
            else {
                if(!event.isSecondaryButtonDown())
                    return;
            }

            zoomingPane.setTranslateX(sceneDragContext.getTranslateAnchorX() + event.getSceneX() - sceneDragContext.getMouseAnchorX());
            zoomingPane.setTranslateY(sceneDragContext.getTranslateAnchorY() + event.getSceneY() - sceneDragContext.getMouseAnchorY());

            event.consume();
        }
    };

    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            if(disabled || !useZoom)
                return;

            double newScale = zoomingPane.getScale();
            double oldScale = newScale;

            if(event.getDeltaY() < 0) {
                newScale /= mouseDeltaFactor;
            }
            else {
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

    public static double snapToNextScale(double scaleToSet, double minimumScale, double maximumScale) {
        if(Double.compare(scaleToSet, minimumScale) < 0)
            return minimumScale;

        if(Double.compare(scaleToSet, maximumScale) > 0)
            return maximumScale;

        return scaleToSet;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    public boolean isUsePrimaryMouseButton() {
        return usePrimaryMouseButton;
    }

    public boolean isUseZoom() {
        return useZoom;
    }

    public void setUsePrimaryMouseButton(boolean usePrimaryMouseButton) {
        this.usePrimaryMouseButton = usePrimaryMouseButton;
    }

    public void setUseZoom(boolean useZoom) {
        this.useZoom = useZoom;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}

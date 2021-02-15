package de.dgs.apps.osfxe.gui.zoomingpane;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class NodeInteractionController {
    private DragContext nodeDragContext = new DragContext();

    private ZoomingPane zoomingPane;
    private boolean usePrimaryMouseButton;

    public NodeInteractionController(ZoomingPane zoomingPane, boolean usePrimaryMouseButton) {
        this.zoomingPane = zoomingPane;
        this.usePrimaryMouseButton = usePrimaryMouseButton;
    }

    public void registerNode(Node node) {
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
    }

    public void unregisterNode(Node node) {
        node.removeEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        node.removeEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
        if(usePrimaryMouseButton) {
            if(!event.isPrimaryButtonDown())
                return;
        }
        else {
            if(!event.isSecondaryButtonDown())
                return;
        }

        nodeDragContext.setMouseAnchorX(event.getSceneX());
        nodeDragContext.setMouseAnchorY(event.getSceneY());

        Object eventSource = event.getSource();

        if(eventSource instanceof Node) {
            Node node = (Node) eventSource;
            nodeDragContext.setTranslateAnchorX(node.getTranslateX());
            nodeDragContext.setTranslateAnchorY(node.getTranslateY());
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<>() {
        public void handle(MouseEvent event) {
            if(usePrimaryMouseButton) {
                if(!event.isPrimaryButtonDown())
                    return;
            }
            else {
                if(!event.isSecondaryButtonDown())
                    return;
            }

            Object eventSource = event.getSource();

            if(eventSource instanceof Node) {
                double scale = zoomingPane.getScale();

                Node node = (Node) eventSource;
                node.setTranslateX(nodeDragContext.getTranslateAnchorX() + ((event.getSceneX() - nodeDragContext.getMouseAnchorX()) / scale));
                node.setTranslateY(nodeDragContext.getTranslateAnchorY() + ((event.getSceneY() - nodeDragContext.getMouseAnchorY()) / scale));

                event.consume();
            }
        }
    };

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public boolean isUsePrimaryMouseButton() {
        return usePrimaryMouseButton;
    }

    public void setUsePrimaryMouseButton(boolean usePrimaryMouseButton) {
        this.usePrimaryMouseButton = usePrimaryMouseButton;
    }
}

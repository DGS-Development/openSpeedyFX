package de.dgs.apps.osfxe.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to manipulate {@link QuadCurve} elements, by displaying a {@link Circle} element to manipulate the "control"-property.
 */
public class QuadCurveManipulationHelper {
    private final ObservableList<Node> rootChildren;

    private final Map<QuadCurve, List<Node>> curveNodesMap = new HashMap<>();

    /**
     * Creates an new {@link QuadCurveManipulationHelper}.
     * @param rootChildren The list to containing the {@link QuadCurve} elements.
     */
    public QuadCurveManipulationHelper(ObservableList<Node> rootChildren) {
        this.rootChildren = rootChildren;
    }

    /**
     * Adds a control-element for a certain {@link QuadCurve} element.
     * @param quadCurve The {@link QuadCurve} to manipulate.
     */
    public void add(QuadCurve quadCurve) {
        add(quadCurve, Color.rgb(0,0,0, 0.5), 2, 7, Color.WHITE);
    }

    /**
     * Adds a control-element for a certain {@link QuadCurve} element.
     * @param quadCurve The {@link QuadCurve} to manipulate.
     * @param strokeColor The {@link Color} of the lines to the {@link Circle} control element.
     * @param strokeWidth The width of the lines to the {@link Circle} control element.
     * @param circleRadius The radius of the {@link Circle} control element.
     * @param circleColor The color of the {@link Circle} control element.
     */
    public void add(QuadCurve quadCurve, Color strokeColor, double strokeWidth, double circleRadius, Color circleColor) {
        Line startLine = new Line();
        startLine.setStroke(strokeColor);
        startLine.setStrokeWidth(strokeWidth);
        startLine.startXProperty().bind(quadCurve.startXProperty());
        startLine.endXProperty().bind(quadCurve.controlXProperty());
        startLine.startYProperty().bind(quadCurve.startYProperty());
        startLine.endYProperty().bind(quadCurve.controlYProperty());

        Line endLine = new Line();
        endLine.setStroke(strokeColor);
        endLine.setStrokeWidth(strokeWidth);
        endLine.startXProperty().bind(quadCurve.endXProperty());
        endLine.endXProperty().bind(quadCurve.controlXProperty());
        endLine.startYProperty().bind(quadCurve.endYProperty());
        endLine.endYProperty().bind(quadCurve.controlYProperty());

        Circle circle = new Circle(circleRadius, circleColor);
        circle.setStroke(strokeColor);
        circle.setStrokeWidth(strokeWidth);
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.centerXProperty().bindBidirectional(quadCurve.controlXProperty());
        circle.centerYProperty().bindBidirectional(quadCurve.controlYProperty());

        circle.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if(!event.isPrimaryButtonDown())
                return;

            circle.setCenterX(event.getX());
            circle.setCenterY(event.getY());
        });

        ArrayList<Node> curveNodes = new ArrayList<>(3);
        curveNodes.add(startLine);
        curveNodes.add(endLine);
        curveNodes.add(circle);

        curveNodesMap.put(quadCurve, curveNodes);
        rootChildren.addAll(curveNodes);
    }

    /**
     * Removes a control-element for a certain {@link QuadCurve} element.
     * @param quadCurve The {@link QuadCurve} to remove the manipulation.
     */
    public void remove(QuadCurve quadCurve) {
        if(curveNodesMap.containsKey(quadCurve)) {
            rootChildren.removeAll(curveNodesMap.get(quadCurve));
            curveNodesMap.remove(quadCurve);
        }
    }

    /**
     * Removes all control-elements for all registered {@link QuadCurve} elements.
     */
    public void removeAll() {
        curveNodesMap.keySet().forEach(quadCurve -> rootChildren.removeAll(curveNodesMap.get(quadCurve)));
        curveNodesMap.clear();
    }
}

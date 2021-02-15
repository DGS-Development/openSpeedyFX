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

public class QuadCurveManipulationHelper {
    private ObservableList<Node> rootChildren;

    private Map<QuadCurve, List<Node>> curveNodesMap = new HashMap<>();

    public QuadCurveManipulationHelper(ObservableList<Node> rootChildren) {
        this.rootChildren = rootChildren;
    }

    public void add(QuadCurve quadCurve) {
        add(quadCurve, Color.rgb(0,0,0, 0.5), 2, 7, Color.WHITE);
    }

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

    public void remove(QuadCurve quadCurve) {
        if(curveNodesMap.containsKey(quadCurve)) {
            rootChildren.removeAll(curveNodesMap.get(quadCurve));
            curveNodesMap.remove(quadCurve);
        }
    }

    public void removeAll() {
        curveNodesMap.keySet().forEach(quadCurve -> rootChildren.removeAll(curveNodesMap.get(quadCurve)));
        curveNodesMap.clear();
    }
}

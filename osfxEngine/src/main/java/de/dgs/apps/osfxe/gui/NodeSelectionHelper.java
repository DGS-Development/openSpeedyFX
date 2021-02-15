package de.dgs.apps.osfxe.gui;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class NodeSelectionHelper<FxNode extends Node> {
    public interface OnNodeSelectedCallback<FxNode extends Node> {
        void onNodeSelected(FxNode node);
    }

    private List<FxNode> nodesToKeep = new LinkedList<>();
    private List<FxNode> selectableNodes;
    private boolean isActive = true;

    private Color selectionColor;
    private double startOpacity;
    private double selectionOpacity;

    public NodeSelectionHelper(Color selectionColor, double startOpacity, double selectionOpacity) {
        this.selectionColor = selectionColor;
        this.startOpacity = startOpacity;
        this.selectionOpacity = selectionOpacity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void reset() {
        isActive = false;
        resetSelectableNodes();
        resetNodesToKeep();
    }

    private void resetNodesToKeep() {
        nodesToKeep.forEach(tmpNode -> resetSelectableNode(tmpNode));
    }

    private void resetSelectableNodes() {
        if(this.selectableNodes != null)
            this.selectableNodes.forEach(tmpNode -> resetSelectableNode(tmpNode));
    }

    public void getSelectableNode(List<FxNode> selectableNodes, boolean keepSelection, OnNodeSelectedCallback<FxNode> onSelectionCallback) {
        isActive = true;
        this.selectableNodes = selectableNodes;

        selectableNodes.forEach(tmpNode -> {
            if(!nodesToKeep.contains(tmpNode)) {
                DropShadow dropShadow = createSelectionDropShadow(selectionColor);

                tmpNode.setOnMouseEntered(mouseEvent -> {
                    tmpNode.setEffect(dropShadow);
                    tmpNode.setOpacity(selectionOpacity);
                });

                tmpNode.setOnMouseExited(mouseEvent -> {
                    tmpNode.setEffect(null);
                    tmpNode.setOpacity(startOpacity);
                });

                tmpNode.setOnMouseClicked(mouseEvent -> {
                    selectableNodes.forEach(tmpOtherNode -> {
                        if(keepSelection) {
                            if(!nodesToKeep.contains(tmpOtherNode)) {
                                if(tmpOtherNode != tmpNode) {
                                    tmpNode.setOpacity(startOpacity);
                                    resetSelectableNode(tmpOtherNode);
                                }
                                else {
                                    nodesToKeep.add(tmpOtherNode);
                                    resetSelectableNodeEvents(tmpOtherNode);
                                }
                            }
                        }
                        else {
                            reset();
                        }
                    });

                    onSelectionCallback.onNodeSelected(tmpNode);
                });
            }
        });
    }

    private DropShadow createSelectionDropShadow(Color selectionColor) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(selectionColor);
        dropShadow.setOffsetX(0f);
        dropShadow.setOffsetY(0f);
        dropShadow.setWidth(100);
        dropShadow.setHeight(100);
        dropShadow.setRadius(50);
        dropShadow.setSpread(0.5);

        return dropShadow;
    }

    private void resetSelectableNode(Node node) {
        resetSelectableNodeEvents(node);
        resetSelectableNodeEffects(node);
        node.setOpacity(startOpacity);
    }

    private void resetSelectableNodeEvents(Node node) {
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
        node.setOnMouseClicked(null);
    }

    private void resetSelectableNodeEffects(Node node) {
        node.setEffect(null);
    }
}

package de.dgs.apps.osfxe.gui;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

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
 * Helper class to select a certain amount of {@link Node} elements, by highlighting the selections.
 * @param <FxNode> The {@link Node} element-type to select.
 */
public class NodeSelectionHelper<FxNode extends Node> {
    /**
     * Callback to signalize that a {@link FxNode} was selected.
     * @param <FxNode> The selected node.
     */
    public interface OnNodeSelectedCallback<FxNode extends Node> {
        void onNodeSelected(FxNode node);
    }

    private List<FxNode> nodesToKeep = new LinkedList<>();
    private List<FxNode> selectableNodes;
    private boolean isActive = true;

    private Color selectionColor;
    private double startOpacity;
    private double selectionOpacity;

    /**
     * Creates a new {@link NodeSelectionHelper}.
     * @param selectionColor The highlighting {@link Color} of the selected nodes.
     * @param startOpacity The opacity of all {@link FxNode} elements before their selection.
     * @param selectionOpacity The opacity of a selected {@link FxNode}.
     */
    public NodeSelectionHelper(Color selectionColor, double startOpacity, double selectionOpacity) {
        this.selectionColor = selectionColor;
        this.startOpacity = startOpacity;
        this.selectionOpacity = selectionOpacity;
    }

    /**
     * Indicates if a selection was started.
     * @return True if a node selection was enabled.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Resets all selected {@link FxNode} elements.
     */
    public void reset() {
        isActive = false;
        resetSelectableNodes();
        resetNodesToKeep();
    }

    private void resetNodesToKeep() {
        nodesToKeep.forEach(this::resetSelectableNode);
    }

    private void resetSelectableNodes() {
        if(selectableNodes != null)
            selectableNodes.forEach(this::resetSelectableNode);
    }

    /**
     * Enables a node selection for all selectable {@link FxNode} elements.
     * @param selectableNodes The selectable nodes.
     * @param keepSelection True if the selected node remains highlighted, after its selection.
     * @param onSelectionCallback Callback returning the selected {@link FxNode} element.
     */
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

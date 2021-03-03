package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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

import javafx.geometry.Point2D;

import java.util.List;

/**
 * Callback to receive information about roll events.
 */
public interface RollCallback {
    void onRollIsReady();
    void onRollCompleted(CollectablesCount collectablesCount);
    void onRollException(Exception exception);

    void onHedgehogUpdate(Point2D coordinate, double angle);
    void onCollectablesSet(List<CollectableItem> collectableApples, List<CollectableItem> collectableLeaves, List<CollectableItem> collectableMushrooms);
    void onCollectableRemoved(CollectableItem collectableItem);
    void onAppleCollected(int applesCount);
    void onLeafCollected(int leavesCount);
    void onMushroomCollected(int mushroomsCount);
    void onWallCollision();
    void onPowerUpdate(double percentage);
    void onArrowUpdate(boolean showArrow, Point2D coordinate, double angle);
}

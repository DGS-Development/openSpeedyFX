package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

import javafx.geometry.Point2D;

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
 * Callback to receive information about roll events. Ignores almost all events unless the subclass overrides the desired methods.
 */
public interface SelectiveRollCallback extends RollCallback {
    @Override
    default void onHedgehogUpdate(Point2D coordinate, double angle) {
        //Ignore
    }

    @Override
    default void onCollectablesSet(List<CollectableItem> collectableApples, List<CollectableItem> collectableLeaves, List<CollectableItem> collectableMushrooms) {
        //Ignore
    }

    @Override
    default void onCollectableRemoved(CollectableItem collectableItem) {
        //Ignore
    }

    @Override
    default void onAppleCollected(int applesCount) {
        //Ignore
    }

    @Override
    default void onLeafCollected(int leavesCount) {
        //Ignore
    }

    @Override
    default void onMushroomCollected(int mushroomsCount) {
        //Ignore
    }

    @Override
    default void onWallCollision() {
        //Ignore
    }

    @Override
    default void onPowerUpdate(double percentage) {
        //Ignore
    }

    @Override
    default void onArrowUpdate(boolean showArrow, Point2D coordinate, double angle) {
        //Ignore
    }
}

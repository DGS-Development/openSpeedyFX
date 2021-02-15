package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

import javafx.geometry.Point2D;

import java.util.List;

/**
 * Callback to receive information about roll events. Ignores almost all events unless the subclass overrides the desired methods.
 */
public interface SelectiveRollCallback extends RollCallback {
    @Override
    default void onHedgehogUpdate(Point2D coordinate, double angle) {
        //Ignore
    }

    @Override
    default void onCollectablesSet(List<CollectableItem> collectableApples, List<CollectableItem> collectableLeafs, List<CollectableItem> collectableMushrooms) {
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
    default void onLeafCollected(int leafsCount) {
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

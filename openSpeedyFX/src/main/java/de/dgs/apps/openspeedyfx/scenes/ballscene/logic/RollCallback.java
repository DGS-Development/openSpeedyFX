package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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
    void onCollectablesSet(List<CollectableItem> collectableApples, List<CollectableItem> collectableLeafs, List<CollectableItem> collectableMushrooms);
    void onCollectableRemoved(CollectableItem collectableItem);
    void onAppleCollected(int applesCount);
    void onLeafCollected(int leafsCount);
    void onMushroomCollected(int mushroomsCount);
    void onWallCollision();
    void onPowerUpdate(double percentage);
    void onArrowUpdate(boolean showArrow, Point2D coordinate, double angle);
}

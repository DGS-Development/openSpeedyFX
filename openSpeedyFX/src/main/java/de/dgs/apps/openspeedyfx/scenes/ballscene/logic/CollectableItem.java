package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

import javafx.geometry.Point2D;

/**
 * POJO class containing positioning information about a {@link CollectableItem}.
 */
public class CollectableItem {
    private final Point2D coordinate;
    private final double angle;

    public CollectableItem(Point2D coordinate, double angle) {
        this.coordinate = coordinate;
        this.angle = angle;
    }

    public Point2D getCoordinate() {
        return coordinate;
    }

    public double getAngle() {
        return angle;
    }
}

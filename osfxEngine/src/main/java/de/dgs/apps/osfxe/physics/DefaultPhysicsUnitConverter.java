package de.dgs.apps.osfxe.physics;

import javafx.geometry.Point2D;
import org.jbox2d.common.Vec2;

/**
 * The default class to convert units between JBox2d (meters) and JavaFX (pixels).
 */
public class DefaultPhysicsUnitConverter implements PhysicsUnitConverter {
    private final double pixelsPerMeter;
    private final double metersPerPixels;

    private final int appHeight;

    public DefaultPhysicsUnitConverter(int appHeight, double ppm) {
        this.appHeight = appHeight;

        pixelsPerMeter = ppm;
        metersPerPixels = 1 / pixelsPerMeter;
    }

    @Override
    public double pixelsToMetersDouble(double pixels) {
        return pixels * metersPerPixels;
    }

    @Override
    public double metersToPixelsDouble(double meters) {
        return meters * pixelsPerMeter;
    }

    @Override
    public Vec2 pointToVector(Point2D point) {
        return new Vec2(pixelsToMetersFloat(point.getX()), pixelsToMetersFloat(appHeight - point.getY()));
    }

    @Override
    public Point2D vectorToPoint(Vec2 vector) {
        return new Point2D(metersToPixelsDouble(vector.x), metersToPixelsDouble(pixelsToMetersDouble(appHeight) - vector.y));
    }
}

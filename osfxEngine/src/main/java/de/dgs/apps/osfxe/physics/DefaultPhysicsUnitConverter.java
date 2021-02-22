package de.dgs.apps.osfxe.physics;

import javafx.geometry.Point2D;
import org.jbox2d.common.Vec2;

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

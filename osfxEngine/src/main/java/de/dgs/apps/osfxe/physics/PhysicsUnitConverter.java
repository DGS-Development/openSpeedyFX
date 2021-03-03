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
 * A utility interface to convert units between JBox2d (meters) and JavaFX (pixels).
 */
public interface PhysicsUnitConverter {
    default float pixelsToMetersFloat(double pixels) {
        return (float) pixelsToMetersDouble(pixels);
    }

    double pixelsToMetersDouble(double pixels);

    default float metersToPixelsFloat(double meters) {
        return (float) metersToPixelsDouble(meters);
    }

    double metersToPixelsDouble(double meters);

    default Vec2 pointToVector(Point2D point) {
        return new Vec2(pixelsToMetersFloat(point.getX()), pixelsToMetersFloat(-point.getY()));
    }

    default Point2D vectorToPoint(Vec2 vector) {
        return new Point2D(metersToPixelsDouble(vector.x), metersToPixelsDouble(-vector.y));
    }

    /**
     * Converts an JavaFX angle to a JBox2d force {@link Vec2}.
     * @param angle The angle starting from 0.
     * @param pushFactor Factor to multiply the force vector "pushFactor" times.
     * @return The corresponding force vector.
     */
    default Vec2 angleToVector(double angle, float pushFactor) {
        angle *= -1;

        double cosAngle;
        double sinAngle;

        if(angle == 0) {
            cosAngle = 1 * pushFactor;
            sinAngle = 0;
        }
        else {
            cosAngle = (pushFactor * Math.cos(angle * Math.PI / 180));
            sinAngle = (pushFactor * Math.sin(angle * Math.PI / 180));
        }

        return new Vec2((float) cosAngle, (float) sinAngle);
    }

    /**
     * Converts an JavaFX angle to a JBox2d force {@link Vec2}. The default push factor is 1.
     * @param angle The angle starting from 0.
     * @return The corresponding force vector.
     */
    default Vec2 angleToVector(double angle) {
        return angleToVector(angle, 1);
    }
}


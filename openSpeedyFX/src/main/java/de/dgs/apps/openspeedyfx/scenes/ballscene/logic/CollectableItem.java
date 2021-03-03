package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

import javafx.geometry.Point2D;

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

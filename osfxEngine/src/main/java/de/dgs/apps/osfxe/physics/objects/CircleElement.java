package de.dgs.apps.osfxe.physics.objects;

import javafx.scene.Node;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;

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
 * A specialized {@link PhysicsElement}, representing a circle shape.
 */
public class CircleElement extends PhysicsElement {
    private float metersRadius;
    private double pixelRadius;

    public CircleElement(Node node, float metersX, float metersY, float metersRadius, double pixelRadius) {
        super(node, metersX, metersY);
        this.metersRadius = metersRadius;
        this.pixelRadius = pixelRadius;
    }

    public float getMetersRadius() {
        return metersRadius;
    }

    public double getPixelRadius() {
        return pixelRadius;
    }

    @Override
    public void moveNodeToCoordinates(double x, double y) {
        getNode().setLayoutX(x - pixelRadius);
        getNode().setLayoutY(y - pixelRadius);
    }

    @Override
    public Shape getShape() {
        CircleShape circleShape = new CircleShape();
        circleShape.m_radius = metersRadius;

        return circleShape;
    }
}

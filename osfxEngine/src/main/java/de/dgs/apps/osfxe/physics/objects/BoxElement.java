package de.dgs.apps.osfxe.physics.objects;

import javafx.scene.Node;
import org.jbox2d.collision.shapes.PolygonShape;
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
 * A specialized {@link PhysicsElement}, representing a box shape.
 */
public class BoxElement extends PhysicsElement {
    private final float metersWidth;
    private final float metersHeight;

    public BoxElement(Node node, float metersX, float metersY, float metersWidth, float metersHeight) {
        super(node, metersX, metersY);
        this.metersWidth = metersWidth;
        this.metersHeight = metersHeight;
    }

    public float getMetersWidth() {
        return metersWidth;
    }

    public float getMetersHeight() {
        return metersHeight;
    }

    @Override
    public void moveNodeToCoordinates(double x, double y) {
        getNode().setLayoutY(x);
        getNode().setLayoutY(y);
    }

    @Override
    public Shape getShape() {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(metersWidth, metersHeight);

        return polygonShape;
    }
}

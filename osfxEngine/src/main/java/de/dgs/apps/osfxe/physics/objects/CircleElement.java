package de.dgs.apps.osfxe.physics.objects;

import javafx.scene.Node;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;

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

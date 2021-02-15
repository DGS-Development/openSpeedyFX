package de.dgs.apps.osfxe.physics.objects;

import javafx.scene.Node;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;

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

package de.dgs.apps.osfxe.physics.objects;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract class representing a JavaFX physics element, combined with the JBox2d world.
 */
public abstract class PhysicsElement {
    /**
     * A callback class to listen for certain physics events.
     */
    public static class PhysicsCallbacks {
        public interface ElementMoveCallback {
            /**
             * Gets executed when a movable object changes its position, inside the physics world.
             * @param pixelPoint The new pixel coordinates.
             * @param metersVector The new world coordinates.
             */
            void onElementMove(Point2D pixelPoint, Vec2 metersVector);
        }

        public interface ElementCollisionCallback {
            /**
             * Gets executed when a collision with another object occurs.
             * @param selfElement The {@link PhysicsElement} of the listener.
             * @param collisionElement The {@link PhysicsElement} colliding with the listener's {@link PhysicsElement}.
             * @param selfIsElementA True if the listener's {@link PhysicsElement} is element A inside the {@link Contact}.
             * @param contact All information about the collision, inside the physics world.
             */
            void onElementCollision(PhysicsElement selfElement, PhysicsElement collisionElement, boolean selfIsElementA, Contact contact);
        }

        private ElementMoveCallback elementMoveCallback;
        private ElementCollisionCallback elementCollisionCallback;
        private ElementCollisionCallback elementCollisionEndCallback;

        /**
         * Returns the move-callback if set.
         * @return The callback or null.
         */
        public ElementMoveCallback getElementMoveCallback() {
            return elementMoveCallback;
        }

        /**
         * Overrides the set move-callback.
         * @param elementMoveCallback The new callback.
         */
        public void setElementMoveCallback(ElementMoveCallback elementMoveCallback) {
            this.elementMoveCallback = elementMoveCallback;
        }

        /**
         * Returns the collision-callback if set. It gets executed when a collision starts.
         * @return The callback or null.
         */
        public ElementCollisionCallback getElementCollisionCallback() {
            return elementCollisionCallback;
        }

        /**
         * Overrides the set collision-callback. It gets executed when a collision starts.
         * @param elementCollisionCallback The new callback.
         */
        public void setElementCollisionCallback(ElementCollisionCallback elementCollisionCallback) {
            this.elementCollisionCallback = elementCollisionCallback;
        }

        /**
         * Returns the collision-callback if set. It gets executed when a collision ends.
         * @return The callback or null.
         */
        public ElementCollisionCallback getElementCollisionEndCallback() {
            return elementCollisionEndCallback;
        }

        /**
         * Overrides the set collision-callback. It gets executed when a collision ends.
         * @param elementCollisionEndCallback The new callback.
         */
        public void setElementCollisionEndCallback(ElementCollisionCallback elementCollisionEndCallback) {
            this.elementCollisionEndCallback = elementCollisionEndCallback;
        }
    }

    private final Node node;
    private float metersX;
    private float metersY;

    private final List<PhysicsCallbacks> physicsCallbacks;

    private Body body;

    /**
     * Creates a new physics element.
     * @param node The corresponding JavaFX node.
     * @param metersX The X coordinate for the element in meters, inside the physics world.
     * @param metersY The Y coordinate for the element in meters, inside the physics world.
     */
    public PhysicsElement(Node node, float metersX, float metersY) {
        this.node = node;
        this.metersX = metersX;
        this.metersY = metersY;

        physicsCallbacks = new LinkedList<>();
    }

    /**
     * Returns the corresponding JavaFX node.
     * @return The node connected with the {@link PhysicsElement}.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns the X coordinate for the element in meters, inside the physics world.
     * @return The X coordinate of the element.
     */
    public float getMetersX() {
        return metersX;
    }

    /**
     * Returns the Y coordinate for the element in meters, inside the physics world.
     * @return The Y coordinate of the element.
     */
    public float getMetersY() {
        return metersY;
    }

    /**
     * Sets a new X coordinate for the physics element, inside the physics world.
     * @param metersX The new X coordinate.
     */
    protected void setMetersX(float metersX) {
        this.metersX = metersX;
    }

    /**
     * Sets a new Y coordinate for the physics element, inside the physics world.
     * @param metersY The new Y coordinate.
     */
    protected void setMetersY(float metersY) {
        this.metersY = metersY;
    }

    /**
     * Adds a new {@link PhysicsCallbacks} to the {@link PhysicsElement}.
     * @param physicsCallback The callback to add.
     * @return Success if the new callback was added.
     */
    public boolean addPhysicsCallback(PhysicsCallbacks physicsCallback) {
        return physicsCallbacks.add(physicsCallback);
    }

    /**
     * Removes a added {@link PhysicsCallbacks} from the {@link PhysicsElement}.
     * @param physicsCallback The callback to remove.
     * @return Success if the new callback was removed.
     */
    public boolean removePhysicsCallback(PhysicsCallbacks physicsCallback) {
        return physicsCallbacks.remove(physicsCallback);
    }

    /**
     * Returns a read-only list of all registered callbacks.
     * @return The registered callbacks.
     */
    public List<? extends PhysicsCallbacks> getPhysicsCallbacks() {
        return physicsCallbacks;
    }

    /**
     * Gets called when the JavaFX {@link Node} should move to another pixel coordinate.
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     */
    public abstract void moveNodeToCoordinates(double x, double y);

    /**
     * Returns if a {@link Body} was set.
     * If set, the {@link PhysicsElement} was added to the physics world.
     * @return True if the element was added to the physics world.
     */
    public boolean isBodySet() {
        return body != null;
    }

    /**
     * Sets the {@link Body}, which means that the {@link PhysicsElement} was added to the physics world.
     * @param body The body to set.
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Returns the {@link Body} created for the {@link PhysicsElement}.
     * @return Null if the {@link PhysicsElement} isn't registered in the physics world.
     */
    public Body getBody() {
        return body;
    }

    /**
     * The {@link Shape} of the {@link PhysicsElement} inside the physics simulation.
     * @return The {@link Shape} representation of the {@link PhysicsElement}.
     */
    public abstract Shape getShape();
}

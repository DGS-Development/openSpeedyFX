package de.dgs.apps.osfxe.physics;

import de.dgs.apps.osfxe.physics.objects.BoxElement;
import de.dgs.apps.osfxe.physics.objects.CircleElement;
import de.dgs.apps.osfxe.physics.objects.PhysicsElement;
import de.dgs.apps.osfxe.physics.objects.PhysicsElement.PhysicsCallbacks;
import de.dgs.apps.osfxe.physics.objects.PhysicsElement.PhysicsCallbacks.ElementCollisionCallback;
import de.dgs.apps.osfxe.physics.objects.PhysicsElement.PhysicsCallbacks.ElementMoveCallback;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.*;

/**
 * Class to run JBox2d simulations, combined with JavaFX elements.
 */
public class FxPhysicsSimulation {
    private static final float SIMULATION_TIMING = 1.0f / 60.0f;
    private static final byte SIMULATION_VELOCITY_ITERATIONS = 3;
    private static final byte SIMULATION_POSITION_ITERATIONS = 8;

    private final PhysicsUnitConverter unitConverter;
    private final World world;

    private Timeline timeline;
    private boolean isSimulationRunning = false;
    private boolean isContactListenerSet = false;

    private final Map<Node, PhysicsElement> nodeElementMap;
    private final Map<PhysicsElement, PhysicsProperties> elementPropertiesMap;
    private final Map<Body, PhysicsElement> bodyElementMap;
    private final List<PhysicsElement> movableElementsList;
    private final Map<PhysicsElement, Vec2> elementPositionMap;

    private final List<Body> bodiesToDestroy;

    /**
     * Creates a new physics simulation. The default gravity is 0.
     * @param unitConverter The unit converter for conversion between meters and pixels.
     */
    public FxPhysicsSimulation(PhysicsUnitConverter unitConverter) {
        this(unitConverter, new Vec2(0.0f, 0.0f));
    }

    /**
     * Creates a new physics simulation.
     * @param unitConverter The unit converter for conversion between meters and pixels.
     * @param gravityVector The default world-gravity vector.
     */
    public FxPhysicsSimulation(PhysicsUnitConverter unitConverter, Vec2 gravityVector) {
        this.unitConverter = unitConverter;
        world = new World(gravityVector);

        nodeElementMap = new HashMap<>();
        elementPropertiesMap = new HashMap<>();
        bodyElementMap = new HashMap<>();
        movableElementsList = new LinkedList<>();
        elementPositionMap = new HashMap<>();

        bodiesToDestroy = new LinkedList<>();
    }

    /**
     * Helper function to get an existing physics element for a given JavaFX-Node.
     * @param node JavaFX-Node mapped to a {@link BoxElement}.
     * @return The corresponding {@link BoxElement} or null.
     */
    public BoxElement getBoxRepresentation(Node node) {
        PhysicsElement physicsElement = nodeElementMap.get(node);
        return physicsElement instanceof BoxElement ? (BoxElement) physicsElement : null;
    }

    /**
     * Helper function to get an existing physics element for a given JavaFX-Node.
     * @param node JavaFX-Node mapped to a {@link CircleElement}.
     * @return The corresponding {@link CircleElement} or null.
     */
    public CircleElement getCircleRepresentation(Node node) {
        PhysicsElement physicsElement = nodeElementMap.get(node);
        return physicsElement instanceof CircleElement ? (CircleElement) physicsElement : null;
    }

    /**
     * Helper function to get the {@link PhysicsProperties} for an existing {@link PhysicsElement}.
     * @param physicsElement {@link PhysicsElement} mapped to the corresponding {@link PhysicsProperties}.
     * @return The corresponding {@link PhysicsProperties}.
     */
    public PhysicsProperties getPhysicsProperties(PhysicsElement physicsElement) {
        return elementPropertiesMap.get(physicsElement);
    }

    /**
     * Adds an {@link ImageView} as {@link BoxElement} to the physics simulation.
     * @param node The {@link ImageView}.
     * @param elementProperties The {@link PhysicsProperties} for the {@link ImageView}.
     * @return The {@link BoxElement} representing the {@link ImageView}.
     */
    public BoxElement addBox(ImageView node, PhysicsProperties elementProperties) {
        Vec2 positionVector = calculateBoxPosition(node, node.getFitWidth(), node.getFitHeight());

        float metersWidth = unitConverter.pixelsToMetersFloat(node.getFitWidth());
        float metersHeight = unitConverter.pixelsToMetersFloat(node.getFitHeight());

        return addBox(node, elementProperties, metersWidth, metersHeight, positionVector.x, positionVector.y);
    }

    /**
     * Adds a {@link Pane} as {@link BoxElement} to the physics simulation.
     * @param node The {@link Pane}.
     * @param elementProperties The {@link PhysicsProperties} for the {@link Pane}.
     * @return The {@link BoxElement} representing the {@link Pane}.
     */
    public BoxElement addBox(Pane node, PhysicsProperties elementProperties) {
        Vec2 positionVector = calculateBoxPosition(node, node.getPrefWidth(), node.getPrefHeight());

        float metersWidth = unitConverter.pixelsToMetersFloat(node.getPrefWidth());
        float metersHeight = unitConverter.pixelsToMetersFloat(node.getPrefHeight());

        return addBox(node, elementProperties, metersWidth, metersHeight, positionVector.x, positionVector.y);
    }

    /**
     * Adds a {@link Node} as {@link BoxElement} to the physics simulation.
     * @param node The node to add.
     * @param elementProperties The {@link PhysicsProperties} for the {@link Node}.
     * @param metersWidth The width of the {@link BoxElement} for the simulation.
     * @param metersHeight The height of the {@link BoxElement} for the simulation.
     * @param metersX The X-coordinate of the {@link BoxElement} for the simulation.
     * @param metersY The Y-coordinate of the {@link BoxElement} for the simulation.
     * @return The {@link BoxElement} representing the {@link Node}.
     */
    public BoxElement addBox(Node node, PhysicsProperties elementProperties, float metersWidth, float metersHeight,
                             float metersX, float metersY) {
        BoxElement box = new BoxElement(node, metersX, metersY, metersWidth, metersHeight);
        setupBox(box, elementProperties);

        return box;
    }

    private Vec2 calculateBoxPosition(Node boxElement, double pixelWidth, double pixelHeight) {
        double xValue = boxElement.getLayoutX();
        double yValue = boxElement.getLayoutY();

        if(boxElement.getLayoutX() > 0)
            xValue += pixelWidth;

        if(boxElement.getLayoutY() > 0)
            yValue += pixelHeight;

        return unitConverter.pointToVector(new Point2D(xValue, yValue));
    }

    private void setupBox(BoxElement box, PhysicsProperties elementProperties) {
        setPhysicsElementBody(box, elementProperties);
        nodeElementMap.put(box.getNode(), box);
        elementPropertiesMap.put(box, elementProperties);
        bodyElementMap.put(box.getBody(), box);

        if(elementProperties.getBodyDef().type != BodyType.STATIC)
            movableElementsList.add(box);
    }

    /**
     * Adds a {@link ImageView} as {@link CircleElement} to the physics simulation.
     * @param node The node to add.
     * @param elementProperties The {@link PhysicsProperties} for the {@link ImageView}.
     * @return The {@link CircleElement} representing the {@link ImageView}.
     */
    public CircleElement addCircle(ImageView node, PhysicsProperties elementProperties) {
        Point2D tmpPoint = new Point2D(
                node.getLayoutX() + (node.getFitHeight() / 2),
                node.getLayoutY() + (node.getFitHeight() / 2));

        Vec2 positionVector = unitConverter.pointToVector(tmpPoint);

        double pixelRadius = node.getFitWidth() / 2;
        float metersRadius = unitConverter.pixelsToMetersFloat(pixelRadius);

        return addCircle(node, elementProperties, positionVector.x, positionVector.y, metersRadius, pixelRadius);
    }

    /**
     * Adds a {@link Node} as {@link CircleElement} to the physics simulation.
     * @param node The node to add.
     * @param elementProperties The {@link PhysicsProperties} for the {@link Node}.
     * @param metersX The X-coordinate of the {@link CircleElement} for the simulation.
     * @param metersY The Y-coordinate of the {@link CircleElement} for the simulation.
     * @param metersRadius The meters-radius of the {@link CircleElement} for the simulation.
     * @param pixelRadius The pixel-radius of the {@link CircleElement} for the simulation.
     * @return The {@link CircleElement} representing the {@link Node}.
     */
    public CircleElement addCircle(Node node, PhysicsProperties elementProperties, float metersX,
                                   float metersY, float metersRadius, double pixelRadius) {
        CircleElement circle = new CircleElement(node, metersX, metersY, metersRadius, pixelRadius);
        setPhysicsElementBody(circle, elementProperties);

        nodeElementMap.put(node, circle);
        elementPropertiesMap.put(circle, elementProperties);
        bodyElementMap.put(circle.getBody(), circle);

        if(elementProperties.getBodyDef().type != BodyType.STATIC)
            movableElementsList.add(circle);

        return circle;
    }

    /**
     * Removes a supported {@link PhysicsElement} from the physics simulation.
     * @param physicsElement The {@link PhysicsElement} to remove.
     */
    public void remove(PhysicsElement physicsElement) {
        nodeElementMap.remove(physicsElement.getNode());
        elementPropertiesMap.remove(physicsElement);
        bodyElementMap.remove(physicsElement.getBody());
        movableElementsList.remove(physicsElement);
        elementPositionMap.remove(physicsElement);

        bodiesToDestroy.add(physicsElement.getBody());
    }

    private void setPhysicsElementBody(PhysicsElement physicsElement, PhysicsProperties elementProperties) {
        BodyDef bodyDef = elementProperties.getBodyDef();
        bodyDef.position.set(physicsElement.getMetersX(), physicsElement.getMetersY());

        FixtureDef fixtureDef = elementProperties.getFixtureDef();
        fixtureDef.shape = physicsElement.getShape();

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        physicsElement.setBody(body);
    }

    /**
     * Helper function to perform a linear push for an existing {@link PhysicsElement}.
     * @param physicsElement The element to push.
     * @param impulse The impulse force as vector.
     * @return True if the element exists in the simulation and it isn't static.
     */
    public boolean pushElementLinear(PhysicsElement physicsElement, Vec2 impulse) {
        if(physicsElement.isBodySet()) {
            PhysicsProperties physicsProperties = elementPropertiesMap.get(physicsElement);

            if(physicsProperties == null)
                return false;

            if(physicsProperties.getBodyDef().type == BodyType.STATIC)
                return false;

            physicsElement.getBody().applyLinearImpulse(impulse, physicsElement.getBody().getPosition());
            return true;
        }

        return false;
    }

    private void onElementCollision(Contact contact, boolean isEndCollision) {
        PhysicsElement collisionElementA = bodyElementMap.get(contact.getFixtureA().getBody());
        PhysicsElement collisionElementB = bodyElementMap.get(contact.getFixtureB().getBody());

        //Ignore already removed elements.
        if(collisionElementA == null || collisionElementB == null)
            return;


        for(PhysicsCallbacks tmpPhysicsCallback : collisionElementA.getPhysicsCallbacks()) {
            ElementCollisionCallback collisionCallback;

            if(isEndCollision) {
                collisionCallback = tmpPhysicsCallback.getElementCollisionEndCallback();
            }
            else {
                collisionCallback = tmpPhysicsCallback.getElementCollisionCallback();
            }

            if(collisionCallback != null)
                collisionCallback.onElementCollision(collisionElementA, collisionElementB, true, contact);
        }

        for(PhysicsCallbacks tmpPhysicsCallback : collisionElementB.getPhysicsCallbacks()) {
            ElementCollisionCallback collisionCallback;

            if(isEndCollision) {
                collisionCallback = tmpPhysicsCallback.getElementCollisionEndCallback();
            }
            else {
                collisionCallback = tmpPhysicsCallback.getElementCollisionCallback();
            }

            if(collisionCallback != null)
                collisionCallback.onElementCollision(collisionElementB, collisionElementA, false, contact);
        }
    }

    private void setupContactListener() {
        if(!isContactListenerSet) {
            isContactListenerSet = true;

            world.setContactListener(new ContactListener() {
                @Override
                public void beginContact(Contact contact) {
                    onElementCollision(contact, false);
                }

                @Override
                public void endContact(Contact contact) {
                    onElementCollision(contact, true);
                }

                @Override
                public void preSolve(Contact contact, Manifold oldManifold) {

                }

                @Override
                public void postSolve(Contact contact, ContactImpulse impulse) {

                }
            });
        }
    }

    private void setupTimeline() {
        if(timeline == null) {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);

            Duration duration = Duration.seconds(SIMULATION_TIMING);

            EventHandler<ActionEvent> actionEventEventHandler = actionEvent -> {
                removeBodiesToDestroy();

                //Simulate a world step.
                world.step(SIMULATION_TIMING, SIMULATION_VELOCITY_ITERATIONS, SIMULATION_POSITION_ITERATIONS);

                for(PhysicsElement tmpPhysicsElement : movableElementsList) {
                    Body body = tmpPhysicsElement.getBody();

                    //Check if the object was actually moved.
                    Vec2 newPositionVector = new Vec2(body.getPosition().x, body.getPosition().y);

                    if(!elementPositionMap.containsKey(tmpPhysicsElement)) {
                        elementPositionMap.put(tmpPhysicsElement, newPositionVector);
                        continue;
                    }

                    Vec2 lastPositionVector = elementPositionMap.get(tmpPhysicsElement);
                    if(lastPositionVector.x == newPositionVector.x && lastPositionVector.y == newPositionVector.y)
                        continue;

                    elementPositionMap.put(tmpPhysicsElement, newPositionVector);

                    //Notify listeners.
                    Point2D newPositionPoint = unitConverter.vectorToPoint(newPositionVector);

                    for(PhysicsCallbacks tmpPhysicsCallback : tmpPhysicsElement.getPhysicsCallbacks()) {
                        ElementMoveCallback moveCallback = tmpPhysicsCallback.getElementMoveCallback();

                        if(moveCallback != null)
                            moveCallback.onElementMove(newPositionPoint, newPositionVector);
                    }

                    //Update JavaFX element position.
                    tmpPhysicsElement.moveNodeToCoordinates(newPositionPoint.getX(), newPositionPoint.getY());
                }
            };

            KeyFrame keyFrame = new KeyFrame(duration, actionEventEventHandler, null, null);
            timeline.getKeyFrames().add(keyFrame);
        }
    }

    /**
     * Helper function to determine if the simulation is running.
     * @return The simulation status.
     */
    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }

    /**
     * Starts or resumes the physics simulation.
     * @return True if the simulation was started or restarted.
     */
    public boolean startSimulation() {
        if(isSimulationRunning)
            return false;

        isSimulationRunning = true;

        setupContactListener();
        setupTimeline();

        timeline.play();

        return true;
    }

    /**
     * Pauses the physics simulation.
     * @return True if the simulation was paused.
     */
    public boolean pauseSimulation() {
        if(!isSimulationRunning)
            return false;

        timeline.stop();
        isSimulationRunning = false;

        return true;
    }

    /**
     * Pauses the simulation and removes all registered elements.
     */
    public void reset() {
        pauseSimulation();

        ArrayList<PhysicsElement> elementsList = new ArrayList(nodeElementMap.values());

        for(PhysicsElement tmpElement : elementsList)
            remove(tmpElement);

        removeBodiesToDestroy();
    }

    private void removeBodiesToDestroy() {
        //Destroy all removable elements, before simulation a world step. This avoids avoid errors.
        for(Body tmpBody : bodiesToDestroy)
            world.destroyBody(tmpBody);

        bodiesToDestroy.clear();
    }
}

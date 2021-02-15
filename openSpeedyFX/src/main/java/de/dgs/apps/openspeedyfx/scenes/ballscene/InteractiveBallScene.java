package de.dgs.apps.openspeedyfx.scenes.ballscene;

import animatefx.animation.Flash;
import de.dgs.apps.osfxe.audio.SoundAudioPlayer;
import de.dgs.apps.osfxe.physics.*;
import de.dgs.apps.osfxe.physics.MovementSlowdownDetector.MovementStoppedCallback;
import de.dgs.apps.osfxe.physics.objects.BoxElement;
import de.dgs.apps.osfxe.physics.objects.CircleElement;
import de.dgs.apps.osfxe.physics.objects.PhysicsElement.PhysicsCallbacks;
import de.dgs.apps.openspeedyfx.game.resourcepacks.ResourcepackPaths;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class InteractiveBallScene extends AbstractBallScene {
    /**
     * POJO class representing a roll.
     */
    public static class Roll {
        private final RollProperties rollProperties;
        private final RollCallback rollCallback;

        public Roll(RollProperties rollProperties, RollCallback rollCallback) {
            this.rollProperties = rollProperties;
            this.rollCallback = rollCallback;
        }

        public RollProperties getRollProperties() {
            return rollProperties;
        }

        public RollCallback getRollCallback() {
            return rollCallback;
        }
    }

    private static final int HEDGEHOG_SLOWDOWN_MINIMAL_DIFFERENCES_COUNT = 4;
    private static final double HEDGEHOG_SLOWDOWN_MINIMAL_DIFFERENCES_VALUE = 0.5;

    private DefaultPhysicsUnitConverter physicsUnitConverter;
    private FxPhysicsSimulation physicsSimulation;

    private Roll initialRoll = null;

    private CircleElement ballElement;
    private BallPushingController ballPushingController;

    private final List<Node> floorPaneChildrenToRemove = new LinkedList<>();

    @Override
    public void onInitialized() {
        super.onInitialized();

        //Conversion between pixels and meters.
        int screenHeight = (int) paneFloor.getPrefHeight();
        physicsUnitConverter = new DefaultPhysicsUnitConverter(screenHeight, 50);

        //Physics simulation for all FX-objects.
        physicsSimulation = new FxPhysicsSimulation(physicsUnitConverter);

        if(initialRoll != null)
            doRoll(initialRoll);
    }

    /**
     * Conducts a {@link Roll}.
     * @param roll The roll properties.
     */
    public void doRoll(Roll roll) {
        if(!isInitialized()) {
            initialRoll = roll;
        }
        else {
            initialRoll = null;
            resetSimulation();

            try {
                //Setup scene.
                setupLocalization(roll.getRollProperties().getResourceBundle());
                setupSceneFont(roll.getRollProperties().getResourcepack());
                setupSceneTextures(roll.getRollProperties().getResourcepack());

                //Setup simulation.
                Random simulationRandom = new Random(roll.getRollProperties().getRandomSeed());

                CoordinateBlockedDetector blockedDetector = new CoordinateBlockedDetector();

                setupSimulationWalls(roll, blockedDetector, simulationRandom);
                setupSimulationCollectables(roll, blockedDetector, simulationRandom);
                setupSimulationBall(roll, blockedDetector);

                //Start simulation.
                physicsSimulation.startSimulation();
                ballPushingController.setActive(true);

                roll.getRollCallback().onRollIsReady();
            }
            catch (Exception exception) {
                roll.getRollCallback().onRollException(exception);
            }
        }
    }

    private void resetSimulation() {
        if(ballElement != null) {
            //Reset labels and count.
            lblCollectedApplesCountProperty.set(0);
            lblCollectedLeafsCountProperty.set(0);
            lblCollectedMushroomsCountProperty.set(0);

            //Remove collectable items.
            paneFloor.getChildren().removeAll(floorPaneChildrenToRemove);

            //Reset the actual simulation.
            physicsSimulation.reset();

            //Reset ball.
            ballElement.getNode().setRotate(0);
        }
    }

    private void setupSimulationBall(Roll roll, CoordinateBlockedDetector blockedDetector) {
        //Physics properties.
        BodyDef ballBodyDef = new BodyDef();
        ballBodyDef.type = BodyType.DYNAMIC;
        ballBodyDef.linearDamping = roll.getRollProperties().getHedgehogPhysicsProperties().getLinearDamping();

        FixtureDef ballFixtureDef = new FixtureDef();
        ballFixtureDef.density = roll.getRollProperties().getHedgehogPhysicsProperties().getDensity();
        ballFixtureDef.friction = roll.getRollProperties().getHedgehogPhysicsProperties().getFriction();
        ballFixtureDef.restitution = roll.getRollProperties().getHedgehogPhysicsProperties().getRestitution();

        PhysicsProperties ballPhysicsProperties = new PhysicsProperties(ballBodyDef, ballFixtureDef);

        //Setup game controller.
        ballPushingController = new BallPushingController(new BallPushingController.BallPushingCallback() {
            @Override
            public void onStateChange(BallPushingController.BallPushingState ballPushingState) {
                //Enable direction arrow.
                boolean setArrowVisible = ballPushingState == BallPushingController.BallPushingState.SET_HEDGEHOG_PUSH_DIRECTION ||
                        ballPushingState == BallPushingController.BallPushingState.SET_HEDGEHOG_PUSH_FORCE;

                imgDirectionArrow.setVisible(setArrowVisible);
                roll.getRollCallback().onArrowUpdate(setArrowVisible, new Point2D(imgDirectionArrow.getLayoutX(), imgDirectionArrow.getLayoutY()), imgDirectionArrow.getRotate());

                //Update according to state.
                if(ballPushingState == BallPushingController.BallPushingState.SET_HEDGEHOG_POSITION) {
                    lblInstructions.setText(roll.getRollProperties().getResourceBundle().getString(LOCALIZATION_HEDGEHOG_POSITION));
                }
                else if(ballPushingState == BallPushingController.BallPushingState.SET_HEDGEHOG_PUSH_DIRECTION) {
                    lblInstructions.setText(roll.getRollProperties().getResourceBundle().getString(LOCALIZATION_HEDGEHOG_PUSH_DIRECTION));

                    //Calculate arrow position.
                    double xDestination = imgHedgehog.getLayoutX() + (imgHedgehog.getFitWidth() / 2);
                    double yDestination = imgHedgehog.getLayoutY() + (imgHedgehog.getFitHeight() / 2);

                    double xSelf = imgDirectionArrow.getLayoutX() + (imgDirectionArrow.getFitWidth() / 2);
                    double ySelf = imgDirectionArrow.getLayoutY() + (imgDirectionArrow.getFitHeight() / 2);

                    if(xDestination != xSelf) {
                        if(xDestination > xSelf) {
                            imgDirectionArrow.setLayoutX(imgDirectionArrow.getLayoutX() + (xDestination - xSelf));
                        }
                        else {
                            imgDirectionArrow.setLayoutX(imgDirectionArrow.getLayoutX() - (xSelf - xDestination));
                        }
                    }

                    if(yDestination != ySelf) {
                        if(yDestination > ySelf) {
                            imgDirectionArrow.setLayoutY(imgDirectionArrow.getLayoutY() + (yDestination - ySelf));
                        }
                        else {
                            imgDirectionArrow.setLayoutY(imgDirectionArrow.getLayoutY() - (ySelf - yDestination));
                        }
                    }
                }
                else if(ballPushingState == BallPushingController.BallPushingState.SET_HEDGEHOG_PUSH_FORCE) {
                    lblInstructions.setText(roll.getRollProperties().getResourceBundle().getString(LOCALIZATION_HEDGEHOG_PUSH_FORCE));
                }
            }

            @Override
            public void onBallPushDegreeUpdate(short pushDegree) {
                imgDirectionArrow.setRotate(pushDegree);
                roll.getRollCallback().onArrowUpdate(true, new Point2D(imgDirectionArrow.getLayoutX(), imgDirectionArrow.getLayoutY()), pushDegree);
            }

            @Override
            public void onBallPush(short pushDegree, float pushPercentage) {
                lblInstructions.setText(roll.getRollProperties().getResourceBundle().getString(LOCALIZATION_HEDGEHOG_WOOSH));

                float pushFactor = 25 * pushPercentage;

                Vec2 impulseVector = physicsUnitConverter.angleToVector(
                        imgDirectionArrow.getRotate(),
                        pushFactor);

                physicsSimulation.pushElementLinear(ballElement, impulseVector);
            }

            @Override
            public void onBallPushPercentageUpdate(double percentage) {
                pbPower.setProgress(percentage);
                roll.getRollCallback().onPowerUpdate(percentage);
            }

            @Override
            public void onBallPositionCandidateUpdate(Point2D coordinate, boolean isBlocked) {
                imgHedgehog.setLayoutX(coordinate.getX() - (imgHedgehog.getFitWidth() / 2));
                imgHedgehog.setLayoutY(coordinate.getY() - (imgHedgehog.getFitHeight() / 2));

                if(isBlocked) {
                    imgHedgehog.setOpacity(0.5);
                }
                else {
                    imgHedgehog.setOpacity(1);
                }
            }

            @Override
            public void onBallPositionSet(Point2D coordinate) {
                //Set hedgehog control position.
                imgHedgehog.setOpacity(1);

                imgHedgehog.setLayoutX(coordinate.getX() - (imgHedgehog.getFitWidth() / 2));
                imgHedgehog.setLayoutY(coordinate.getY() - (imgHedgehog.getFitHeight() / 2));

                ballElement = physicsSimulation.addCircle(imgHedgehog, ballPhysicsProperties);

                //Detect stop.
                PhysicsCallbacks physicsCallbacks = new PhysicsCallbacks();

                MovementStoppedCallback stoppedCallback = () -> {
                    if(ballElement.isBodySet()) {
                        //Stop ball and simulation.
                        ballElement.getBody().setLinearVelocity(new Vec2(0,0));
                        ballElement.getBody().setAngularVelocity(0);

                        physicsSimulation.pauseSimulation();

                        //Wait until all animation and layout processing is done.
                        Platform.runLater(() -> {
                            roll.getRollCallback().onRollCompleted(new CollectablesCount(
                                    lblCollectedApplesCountProperty.get(),
                                    lblCollectedLeafsCountProperty.get(),
                                    lblCollectedMushroomsCountProperty.get()));
                        });
                    }
                };

                MovementSlowdownDetector slowdownDetector = new MovementSlowdownDetector(
                        HEDGEHOG_SLOWDOWN_MINIMAL_DIFFERENCES_COUNT,
                        HEDGEHOG_SLOWDOWN_MINIMAL_DIFFERENCES_VALUE,
                        stoppedCallback, 1000 * 5);

                physicsCallbacks.setElementMoveCallback((pixelPoint, metersVector) -> {
                    //Update detector.
                    slowdownDetector.update(pixelPoint.getX(), pixelPoint.getY());

                    //Update hedgehog rotation.
                    double newAngle = ballElement.getBody().getAngle() * -1;
                    imgHedgehog.setRotate(newAngle);

                    //Update callback.
                    roll.getRollCallback().onHedgehogUpdate(pixelPoint, newAngle);
                });

                ballElement.addPhysicsCallback(physicsCallbacks);
            }
        }, imgHedgehog.getFitWidth(), blockedDetector);

        //Register game controls. Listen to global scene events to avoid stuttering.
        paneFloor.setFocusTraversable(true);
        paneFloor.addEventFilter(KeyEvent.KEY_PRESSED, ballPushingController.getKeyEventHandler());

        apRoot.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getY() >= paneFloor.getLayoutY() &&
                    event.getY() <= paneFloor.getLayoutY() + paneFloor.getPrefHeight() &&
                    event.getX() >= paneFloor.getLayoutX() &&
                    event.getX() <= paneFloor.getLayoutX() + paneFloor.getPrefWidth()) {
                ballPushingController.getMouseEventHandler().handle(event);
            }
        });
    }

    private void setupSimulationWalls(Roll roll, CoordinateBlockedDetector blockedDetector, Random random) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        //Physics properties.
        BodyDef wallBodyDef = new BodyDef();
        wallBodyDef.type = BodyType.STATIC;

        PhysicsProperties wallPhysicsProperties = new PhysicsProperties(wallBodyDef);

        //Add walls.
        List<BoxElement> wallElements = new LinkedList<>();

        wallElements.add(physicsSimulation.addBox(paneBorderTop, wallPhysicsProperties));
        wallElements.add(physicsSimulation.addBox(paneBorderBottom, wallPhysicsProperties));
        wallElements.add(physicsSimulation.addBox(paneBorderLeft, wallPhysicsProperties));
        wallElements.add(physicsSimulation.addBox(paneBorderRight, wallPhysicsProperties));

        blockedDetector.addRectangle(paneBorderTop);
        blockedDetector.addRectangle(paneBorderBottom);
        blockedDetector.addRectangle(paneBorderLeft);
        blockedDetector.addRectangle(paneBorderRight);

        //Setup collision audio.
        SoundAudioPlayer collisionSoundPlayer = new SoundAudioPlayer(roll.getRollProperties().getEffectsBaseVolume(), random);

        collisionSoundPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.WALLCOLLISION_1_WAV));
        collisionSoundPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.WALLCOLLISION_2_WAV));
        collisionSoundPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.WALLCOLLISION_3_WAV));
        collisionSoundPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.WALLCOLLISION_4_WAV));

        //Add listeners for collisions.
        PhysicsCallbacks wallPhysicsCallback = new PhysicsCallbacks();

        wallPhysicsCallback.setElementCollisionCallback((selfElement, collisionElement, selfIsElementA, contact) -> {
            if(ballElement == collisionElement) {
                collisionSoundPlayer.playRandomSound();
                roll.getRollCallback().onWallCollision();
            }
        });

        for(BoxElement tmpBoxElement : wallElements)
            tmpBoxElement.addPhysicsCallback(wallPhysicsCallback);
    }

    private void setupSimulationCollectables(Roll roll, CoordinateBlockedDetector blockedDetector, Random random) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        //Add elements.
        List<CollectableItem> collectableApples = setupAppleCollectables(roll, random, blockedDetector);
        List<CollectableItem> collectableLeafs = setupLeafCollectables(roll, random, blockedDetector);
        List<CollectableItem> collectableMushrooms = setupMushroomCollectables(roll, random, blockedDetector);

        roll.getRollCallback().onCollectablesSet(collectableApples, collectableLeafs, collectableMushrooms);
    }

    private double getCollectablesRangeStartX() {
        return paneBorderLeft.getLayoutX() + paneBorderLeft.getPrefWidth() + COLLECTABLES_BORDER_PADDING + (COLLECTABLES_ITEM_FIT_WIDTH / 2f);
    }

    private double getCollectablesRangeEndX() {
        return paneBorderRight.getLayoutX() - COLLECTABLES_BORDER_PADDING - (COLLECTABLES_ITEM_FIT_WIDTH / 2f);
    }

    private double getCollectablesRangeStartY() {
        return paneBorderTop.getLayoutY() + paneBorderTop.getPrefHeight() + COLLECTABLES_BORDER_PADDING + (COLLECTABLES_ITEM_FIT_WIDTH / 2f);
    }

    private double getCollectablesRangeEndY() {
        return paneBorderBottom.getLayoutY() - COLLECTABLES_BORDER_PADDING - (COLLECTABLES_ITEM_FIT_WIDTH / 2f);
    }

    private List<CollectableItem> setupAppleCollectables(Roll roll, Random random, CoordinateBlockedDetector blockedDetector) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        SoundAudioPlayer soundAudioPlayer = new SoundAudioPlayer(roll.getRollProperties().getEffectsBaseVolume(), random);
        soundAudioPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.APPLE_1_WAV));
        soundAudioPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.APPLE_2_WAV));

        PhysicsCallbacks generalPhysicsCallbacks = new PhysicsCallbacks();

        generalPhysicsCallbacks.setElementCollisionCallback((selfElement, collisionElement, selfIsElementA, contact) -> {
            irritateHedgehog(random, roll.getRollProperties().getHedgehogIrritation());

            int newItemCount = lblCollectedApplesCountProperty.getValue() + 1;
            lblCollectedApplesCountProperty.set(newItemCount);
            new Flash(imgCollectedApples).play();
            soundAudioPlayer.playRandomSound();
            roll.getRollCallback().onAppleCollected(newItemCount);

            paneFloor.getChildren().remove(selfElement.getNode());
            physicsSimulation.remove(selfElement);
        });

        List<CollectableItem> collectableItems = new LinkedList<>();

        for(int itemsLeft = roll.getRollProperties().getCollectablesCount().getApplesCount(); itemsLeft != 0; itemsLeft--) {
            CircleElement circleElement = setupCollectableCircleElement(
                    random, blockedDetector, roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Fields.APPLE_FIELD_PNG));

            circleElement.addPhysicsCallback(generalPhysicsCallbacks);
            collectableItems.add(setupCollectableItem(roll.getRollCallback(), circleElement));
        }

        return collectableItems;
    }

    private List<CollectableItem> setupLeafCollectables(Roll roll, Random random, CoordinateBlockedDetector blockedDetector) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        SoundAudioPlayer soundAudioPlayer = new SoundAudioPlayer(roll.getRollProperties().getEffectsBaseVolume(), random);
        soundAudioPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.LEAFS_1_WAV));
        soundAudioPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.LEAFS_2_WAV));

        PhysicsCallbacks generalPhysicsCallbacks = new PhysicsCallbacks();

        generalPhysicsCallbacks.setElementCollisionCallback((selfElement, collisionElement, selfIsElementA, contact) -> {
            irritateHedgehog(random, roll.getRollProperties().getHedgehogIrritation());

            int newItemCount = lblCollectedLeafsCountProperty.getValue() + 1;
            lblCollectedLeafsCountProperty.set(newItemCount);
            new Flash(imgCollectedLeafs).play();
            soundAudioPlayer.playRandomSound();
            roll.getRollCallback().onLeafCollected(newItemCount);

            paneFloor.getChildren().remove(selfElement.getNode());
            physicsSimulation.remove(selfElement);
        });

        List<CollectableItem> collectableItems = new LinkedList<>();

        for(int itemsLeft = roll.getRollProperties().getCollectablesCount().getLeafsCount(); itemsLeft != 0; itemsLeft--) {
            CircleElement circleElement = setupCollectableCircleElement(
                    random, blockedDetector, roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Fields.LEAF_FIELD_PNG));

            circleElement.addPhysicsCallback(generalPhysicsCallbacks);
            collectableItems.add(setupCollectableItem(roll.getRollCallback(), circleElement));
        }

        return collectableItems;
    }

    private List<CollectableItem> setupMushroomCollectables(Roll roll, Random random, CoordinateBlockedDetector blockedDetector) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        SoundAudioPlayer soundAudioPlayer = new SoundAudioPlayer(roll.getRollProperties().getEffectsBaseVolume(), random);
        soundAudioPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.MUSHROOM_1_WAV));
        soundAudioPlayer.addSound(roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Sounds.Ballscene.MUSHROOM_1_WAV));

        PhysicsCallbacks generalPhysicsCallbacks = new PhysicsCallbacks();

        generalPhysicsCallbacks.setElementCollisionCallback((selfElement, collisionElement, selfIsElementA, contact) -> {
            irritateHedgehog(random, roll.getRollProperties().getHedgehogIrritation());

            int newItemCount = lblCollectedMushroomsCountProperty.getValue() + 1;
            lblCollectedMushroomsCountProperty.set(newItemCount);
            new Flash(imgCollectedMushrooms).play();
            soundAudioPlayer.playRandomSound();
            roll.getRollCallback().onMushroomCollected(newItemCount);

            paneFloor.getChildren().remove(selfElement.getNode());
            physicsSimulation.remove(selfElement);
        });

        List<CollectableItem> collectableItems = new LinkedList<>();

        for(int itemsLeft = roll.getRollProperties().getCollectablesCount().getMushroomsCount(); itemsLeft != 0; itemsLeft--) {
            CircleElement circleElement = setupCollectableCircleElement(
                    random, blockedDetector, roll.getRollProperties().getResourcepack().getResourceAsStream(ResourcepackPaths.Fields.MUSHROOM_FIELD_PNG));

            circleElement.addPhysicsCallback(generalPhysicsCallbacks);
            collectableItems.add(setupCollectableItem(roll.getRollCallback(), circleElement));
        }

        return collectableItems;
    }

    private CollectableItem setupCollectableItem(RollCallback rollCallback, CircleElement circleElement) {
        PhysicsCallbacks collisionCallbacks = new PhysicsCallbacks();

        CollectableItem collectableItem = new CollectableItem(
                new Point2D(circleElement.getNode().getLayoutX(), circleElement.getNode().getLayoutY()),
                circleElement.getNode().getRotate());

        collisionCallbacks.setElementCollisionCallback((selfElement, collisionElement, selfIsElementA, contact) -> {
            rollCallback.onCollectableRemoved(collectableItem);
        });

        circleElement.addPhysicsCallback(collisionCallbacks);
        return collectableItem;
    }

    private CircleElement setupCollectableCircleElement(Random random, CoordinateBlockedDetector blockedDetector, InputStream imageStream) {
        BodyDef itemBodyDef = new BodyDef();
        itemBodyDef.type = BodyType.STATIC;

        FixtureDef itemFixtureDef = new FixtureDef();
        itemFixtureDef.isSensor = true;

        PhysicsProperties itemPhysicsProperties = new PhysicsProperties(itemBodyDef, itemFixtureDef);

        ImageView itemImageView = new ImageView(new Image(imageStream, COLLECTABLES_ITEM_FIT_WIDTH, COLLECTABLES_ITEM_FIT_WIDTH, true, true));
        itemImageView.setFitWidth(COLLECTABLES_ITEM_FIT_WIDTH);
        itemImageView.setFitHeight(COLLECTABLES_ITEM_PADDING);

        while(true) {
            double randomX = getCollectablesRangeStartX() + random.nextInt((int) (getCollectablesRangeEndX() - getCollectablesRangeStartX()));
            double randomY = getCollectablesRangeStartY() + random.nextInt((int) (getCollectablesRangeEndY() - getCollectablesRangeStartY()));

            boolean isItemBlocked = blockedDetector.isCoordinateBlocked(randomX, randomY, COLLECTABLES_ITEM_PADDING);

            if(!isItemBlocked) {
                //Calculate coordinates and angle.
                randomX = randomX - (itemImageView.getFitWidth() / 2);
                randomY = randomY - (itemImageView.getFitHeight() / 2);

                itemImageView.setLayoutX(randomX);
                itemImageView.setLayoutY(randomY);
                itemImageView.setRotate(random.nextInt(360));

                //Block element position.
                blockedDetector.addCircle(itemImageView);

                //Add element.
                floorPaneChildrenToRemove.add(itemImageView);
                paneFloor.getChildren().add(itemImageView);

                return physicsSimulation.addCircle(itemImageView, itemPhysicsProperties);
            }
        }
    }

    private void irritateHedgehog(Random random, HedgehogIrritation hedgehogIrritation) {
        float imbalanceFactorLeft = 0;
        float imbalanceFactorRight = 0;

        if(hedgehogIrritation.isImbalanceEnabled()) {
            float imbalanceFactor = (random.nextInt(3)) / 10f;

            if(random.nextBoolean()) {
                imbalanceFactorLeft = imbalanceFactor;
            }
            else {
                imbalanceFactorRight = imbalanceFactor;
            }
        }

        float slowdownFactor = hedgehogIrritation.getSlowdownFactor();

        Vec2 linearVelocityVec2 = ballElement.getBody().getLinearVelocity();

        linearVelocityVec2 = new Vec2(
                linearVelocityVec2.x * -(slowdownFactor + imbalanceFactorLeft),
                linearVelocityVec2.y * -(slowdownFactor + imbalanceFactorRight));

        ballElement.getBody().applyLinearImpulse(linearVelocityVec2, ballElement.getBody().getPosition());
    }
}
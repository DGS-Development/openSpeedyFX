package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

import de.dgs.apps.osfxe.physics.CoordinateBlockedDetector;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

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
 * Helper class to control and conduct a ball push.
 */
public class BallPushingController {
    public interface BallPushingCallback {
        void onStateChange(BallPushingState state);

        void onBallPushDegreeUpdate(short pushDegree);
        void onBallPush(short pushDegree, float pushPercentage);
        void onBallPushPercentageUpdate(double percentage);

        void onBallPositionCandidateUpdate(Point2D coordinate, boolean isBlocked);
        void onBallPositionSet(Point2D coordinate);
    }

    public enum BallPushingState {
        INACTIVE,
        SET_HEDGEHOG_POSITION,
        SET_HEDGEHOG_PUSH_DIRECTION,
        SET_HEDGEHOG_PUSH_FORCE
    }

    private BallPushingState state = BallPushingState.INACTIVE;
    private final EventHandler<KeyEvent> keyEventHandler;
    private final EventHandler<MouseEvent> mouseEventHandler;

    private BallPushingCallback ballPusherCallback;

    private short pushDegree = 0;

    private volatile boolean runPushThread;
    private volatile byte pushPercentage;
    private Thread pushThread;

    private volatile int powerStepSleepInterval = 10;
    private short keyboardPushDegreeStep = 4;

    public int getPowerStepSleepInterval() {
        return powerStepSleepInterval;
    }

    public void setPowerStepSleepInterval(int powerStepSleepInterval) {
        this.powerStepSleepInterval = powerStepSleepInterval;
    }

    public short getKeyboardPushDegreeStep() {
        return keyboardPushDegreeStep;
    }

    public void setKeyboardPushDegreeStep(short keyboardPushDegreeStep) {
        this.keyboardPushDegreeStep = keyboardPushDegreeStep;
    }

    private final Runnable pushThreadRunnable = new Runnable() {
        private boolean add = false;

        @SuppressWarnings({"BusyWait", "NonAtomicOperationOnVolatileField"})
        @Override
        public void run() {
            runPushThread = true;
            pushPercentage = 0;

            while (runPushThread) {
                if(add) {
                    if(pushPercentage == 100) {
                        pushPercentage--;
                        add = false;
                    }
                    else {
                        pushPercentage++;
                    }
                }
                else {
                    if(pushPercentage == 0) {
                        pushPercentage++;
                        add = true;
                    }
                    else {
                        pushPercentage--;
                    }
                }

                ballPusherCallback.onBallPushPercentageUpdate(pushPercentage / 100f);

                try {
                    Thread.sleep(powerStepSleepInterval);
                }
                catch (Exception exception) {
                    //Ignore...
                }
            }
        }
    };

    public void setActive(boolean activate) {
        if(activate) {
            pushDegree = 0;
            state = BallPushingState.SET_HEDGEHOG_POSITION;
        }
        else {
            state = BallPushingState.INACTIVE;
        }

        ballPusherCallback.onStateChange(state);
    }

    public BallPushingController(BallPushingCallback ballPusherCallback, double hedgehogWidth, CoordinateBlockedDetector blockedDetector) {
        this.ballPusherCallback = ballPusherCallback;

        mouseEventHandler = event -> {
            if(state == BallPushingState.SET_HEDGEHOG_POSITION) {
                Point2D coordinate = new Point2D(event.getX(), event.getY());
                boolean isBlocked = blockedDetector.isCoordinateBlocked(coordinate.getX(), coordinate.getY(), (hedgehogWidth / 2) + 7);

                if(event.isPrimaryButtonDown() && !isBlocked) {
                    ballPusherCallback.onBallPositionSet(coordinate);

                    state = BallPushingState.SET_HEDGEHOG_PUSH_DIRECTION;
                    ballPusherCallback.onStateChange(state);
                }
                else {
                    ballPusherCallback.onBallPositionCandidateUpdate(coordinate, isBlocked);
                }
            }
        };

        keyEventHandler = event -> {
            if (state == BallPushingState.INACTIVE)
                return;

            if (state == BallPushingState.SET_HEDGEHOG_PUSH_DIRECTION) {
                if (event.getCode() == KeyCode.SPACE) {
                    state = BallPushingState.SET_HEDGEHOG_PUSH_FORCE;

                    ballPusherCallback.onStateChange(state);

                    pushThread = new Thread(pushThreadRunnable);
                    pushThread.setDaemon(true);
                    pushThread.start();

                    return;
                }

                if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                    if (pushDegree <= -179) {
                        pushDegree = 180;
                    } else {
                        pushDegree -= keyboardPushDegreeStep;
                    }

                    ballPusherCallback.onBallPushDegreeUpdate(pushDegree);
                } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                    if (pushDegree == 180) {
                        pushDegree -= keyboardPushDegreeStep;
                        pushDegree *= -1;
                    } else {
                        pushDegree += keyboardPushDegreeStep;
                    }

                    ballPusherCallback.onBallPushDegreeUpdate(pushDegree);
                }
            }
            else if (state == BallPushingState.SET_HEDGEHOG_PUSH_FORCE) {
                if (event.getCode() == KeyCode.SPACE) {
                    runPushThread = false;

                    if(pushPercentage < 1)
                        pushPercentage = 1;

                    ballPusherCallback.onBallPush(pushDegree, pushPercentage / 100f);

                    state = BallPushingState.INACTIVE;
                    pushDegree = 0;

                    ballPusherCallback.onStateChange(state);
                    ballPusherCallback.onBallPushDegreeUpdate(pushDegree);
                    ballPusherCallback.onBallPushPercentageUpdate(0);
                }
            }
        };
    }

    public void setBallPusherCallback(BallPushingCallback ballPusherCallback) {
        this.ballPusherCallback = ballPusherCallback;
    }

    public BallPushingState getState() {
        return state;
    }

    public EventHandler<KeyEvent> getKeyEventHandler() {
        return keyEventHandler;
    }

    public EventHandler<MouseEvent> getMouseEventHandler() {
        return mouseEventHandler;
    }
}

package de.dgs.apps.osfxe.physics;

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
 * Helper class to detect a slowdown, by analyzing the latest coordinates.
 */
public class MovementSlowdownDetector {
    public interface MovementStoppedCallback {
        void onMovementStopped();
    }

    private double lastX = 0;
    private double lastY = 0;

    private int invalidDifferencesCount;
    private final int minimalDifferencesCount;

    private final double minimalDifferenceValue;
    private final MovementStoppedCallback movementStoppedCallback;
    private final int timeoutMilliseconds;

    private int addedCount = 0;
    private boolean movementStopped = false;

    private long lastUpdate;
    private Thread timeoutThread = null;

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            while(!movementStopped) {
                if(System.currentTimeMillis() > (lastUpdate + timeoutMilliseconds)) {
                    movementStopped = true;
                    movementStoppedCallback.onMovementStopped();
                }

                try {
                    Thread.sleep(100);
                }
                catch (Exception exception) {
                    //Ignore
                }
            }
        }
    };

    /**
     * Creates a new {@link MovementSlowdownDetector}.
     * @param minimalDifferencesCount The amount of coordinates to monitor.
     * @param minimalDifferenceValue The minimum difference between two coordinates.
     * @param movementStoppedCallback The callback to notify, if the movement slowed down.
     * @param timeoutMilliseconds A timeout that triggers the callback, after the last update, if the movement didn't stop.
     */
    public MovementSlowdownDetector(int minimalDifferencesCount, double minimalDifferenceValue, MovementStoppedCallback movementStoppedCallback,
                                    int timeoutMilliseconds) {
        this.minimalDifferencesCount = minimalDifferencesCount;
        this.minimalDifferenceValue = minimalDifferenceValue;
        this.movementStoppedCallback = movementStoppedCallback;
        this.timeoutMilliseconds = timeoutMilliseconds;
    }

    /**
     * Updates the {@link MovementSlowdownDetector} an checks if a slowdown occurred.
     * @param x The latest x coordinate.
     * @param y The latest y coordinate.
     */
    public void update(double x, double y) {
        if(movementStopped)
            return;

        lastUpdate = System.currentTimeMillis();

        if(timeoutThread == null) {
            timeoutThread = new Thread(timeoutRunnable);
            timeoutThread.start();
        }

        double xDifference;

        if(lastX > x) {
            xDifference = lastX - x;
        }
        else {
            xDifference = x - lastX;
        }

        double yDifference;

        if(lastY > y) {
            yDifference = lastY - y;
        }
        else {
            yDifference = y - lastY;
        }

        lastX = x;
        lastY = y;

        if(xDifference < minimalDifferenceValue && yDifference < minimalDifferenceValue) {
            invalidDifferencesCount++;
        }
        else {
            invalidDifferencesCount = 0;
        }

        if(addedCount == (minimalDifferencesCount - 1)) {
            if(invalidDifferencesCount >= minimalDifferencesCount) {
                movementStoppedCallback.onMovementStopped();
                movementStopped = true;
            }
        }
        else {
            addedCount++;
            lastUpdate = System.currentTimeMillis();
        }
    }

    /**
     * Resets and enables the slowdown detector.
     */
    public void reset() {
        lastX = 0;
        lastY = 0;
        addedCount = 0;
        movementStopped = false;
        timeoutThread = null;
    }
}

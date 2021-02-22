package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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
 * POJO class to irritate the hedgehog.
 */
public class HedgehogIrritation {
    private final boolean imbalanceEnabled;
    private final float slowdownFactor;

    /**
     * Creates a new {@link HedgehogIrritation}.
     * @param imbalanceEnabled Causes the hedgehog to move left or right, if true and a {@link CollectableItem} was collected.
     * @param slowdownFactor The factor to slow the hedgehog down, if a {@link CollectableItem} was collected. If set to 0 the hedgehog doesn't slow down.
     */
    public HedgehogIrritation(boolean imbalanceEnabled, float slowdownFactor) {
        this.imbalanceEnabled = imbalanceEnabled;
        this.slowdownFactor = slowdownFactor;
    }

    /**
     * True if the hedgehog moves left or right if a {@link CollectableItem} was collected.
     * @return True if enabled.
     */
    public boolean isImbalanceEnabled() {
        return imbalanceEnabled;
    }

    /**
     * The factor to slow the hedgehog down, if a {@link CollectableItem} was collected.
     * @return The slowdown factor.
     */
    public float getSlowdownFactor() {
        return slowdownFactor;
    }
}

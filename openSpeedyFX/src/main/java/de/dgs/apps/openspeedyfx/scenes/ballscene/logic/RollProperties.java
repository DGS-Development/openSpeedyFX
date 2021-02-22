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

import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;

import java.util.ResourceBundle;

/**
 * All information necessary to conduct a roll.
 */
public interface RollProperties {
    HedgehogIrritation getHedgehogIrritation();
    HedgehogPhysicsProperties getHedgehogPhysicsProperties();

    ResourceBundle getResourceBundle();
    Resourcepack getResourcepack();
    CollectablesCount getCollectablesCount();

    /**
     * Volume between 0 and 1.
     * @return The sound effects volume.
     */
    float getEffectsBaseVolume();

    /**
     * A seed to inititalize the {@link java.util.Random} used to place tiles, play sounds and irritate the hedgehog.
     * @return The random seed.
     */
    long getRandomSeed();
}

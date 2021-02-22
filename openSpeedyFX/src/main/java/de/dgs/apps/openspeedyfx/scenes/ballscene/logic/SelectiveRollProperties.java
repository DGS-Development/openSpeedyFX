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
 * All information necessary to conduct a roll. Provides default properties unless the subclass overrides the desired methods.
 */
public interface SelectiveRollProperties extends RollProperties {
    @Override
    default HedgehogIrritation getHedgehogIrritation() {
        return new HedgehogIrritation(true, 0.09f);
    }

    @Override
    default HedgehogPhysicsProperties getHedgehogPhysicsProperties() {
        return new HedgehogPhysicsProperties(0.65f, 0.5f, 0.99f, 0.6f);
    }

    ResourceBundle getResourceBundle();
    Resourcepack getResourcepack();

    /**
     * {@inheritDoc}
     */
    float getEffectsBaseVolume();

    /**
     * {@inheritDoc}
     */
    @Override
    default long getRandomSeed() {
        return System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default CollectablesCount getCollectablesCount() {
        return new CollectablesCount(6, 6, 6);
    }
}

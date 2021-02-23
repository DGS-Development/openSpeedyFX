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
public abstract class SelectiveRollProperties implements RollProperties {
    private HedgehogIrritation hedgehogIrritation = new HedgehogIrritation(true, 0.09f);
    private HedgehogPhysicsProperties hedgehogPhysicsProperties = new HedgehogPhysicsProperties(0.65f, 0.5f, 0.99f, 0.6f);

    private CollectablesCount collectablesCount = new CollectablesCount(6, 6, 6);

    @Override
    public HedgehogIrritation getHedgehogIrritation() {
        return hedgehogIrritation;
    }

    public void setHedgehogIrritation(HedgehogIrritation hedgehogIrritation) {
        this.hedgehogIrritation = hedgehogIrritation;
    }

    @Override
    public HedgehogPhysicsProperties getHedgehogPhysicsProperties() {
        return hedgehogPhysicsProperties;
    }

    public void setHedgehogPhysicsProperties(HedgehogPhysicsProperties hedgehogPhysicsProperties) {
        this.hedgehogPhysicsProperties = hedgehogPhysicsProperties;
    }

    public abstract ResourceBundle getResourceBundle();
    public abstract Resourcepack getResourcepack();

    /**
     * {@inheritDoc}
     */
    public abstract float getEffectsBaseVolume();

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRandomSeed() {
        return System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectablesCount getCollectablesCount() {
        return collectablesCount;
    }

    public void setCollectablesCount(CollectablesCount collectablesCount) {
        this.collectablesCount = collectablesCount;
    }
}

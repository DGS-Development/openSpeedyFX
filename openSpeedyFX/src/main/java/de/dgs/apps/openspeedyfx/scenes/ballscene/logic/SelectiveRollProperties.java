package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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

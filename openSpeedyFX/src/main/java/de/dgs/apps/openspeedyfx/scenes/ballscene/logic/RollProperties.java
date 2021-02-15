package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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

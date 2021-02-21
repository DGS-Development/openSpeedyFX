package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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

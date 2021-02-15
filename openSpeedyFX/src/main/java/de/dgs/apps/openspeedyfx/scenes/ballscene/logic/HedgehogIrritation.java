package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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

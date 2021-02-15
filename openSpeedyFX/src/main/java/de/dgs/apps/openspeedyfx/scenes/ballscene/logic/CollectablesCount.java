package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

/**
 * POJO class representing the amount of collected/collectable items.
 */
public class CollectablesCount {
    private int applesCount;
    private int leafsCount;
    private int mushroomsCount;

    public CollectablesCount(int applesCount, int leafsCount, int mushroomsCount) {
        this.applesCount = applesCount;
        this.leafsCount = leafsCount;
        this.mushroomsCount = mushroomsCount;
    }

    public int getApplesCount() {
        return applesCount;
    }

    public int getLeafsCount() {
        return leafsCount;
    }

    public int getMushroomsCount() {
        return mushroomsCount;
    }
}

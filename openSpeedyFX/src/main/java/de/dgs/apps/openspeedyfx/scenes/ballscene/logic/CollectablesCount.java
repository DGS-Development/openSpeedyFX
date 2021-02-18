package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

/**
 * POJO class representing the amount of collected/collectable items.
 */
public class CollectablesCount {
    private int applesCount;
    private int leavesCount;
    private int mushroomsCount;

    public CollectablesCount(int applesCount, int leavesCount, int mushroomsCount) {
        this.applesCount = applesCount;
        this.leavesCount = leavesCount;
        this.mushroomsCount = mushroomsCount;
    }

    public int getApplesCount() {
        return applesCount;
    }

    public int getLeavesCount() {
        return leavesCount;
    }

    public int getMushroomsCount() {
        return mushroomsCount;
    }
}

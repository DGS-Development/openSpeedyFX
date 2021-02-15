package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.LinkedList;
import java.util.List;

public class Roll {

    private int appleCount;
    private int mushroomCount;
    private int leafCount;

    public Roll(int appleCount, int mushroomCount, int leafCount){
        this.appleCount = appleCount;
        this.mushroomCount = mushroomCount;
        this.leafCount = leafCount;
    }

    public Roll(Roll roll){
        this(roll.getAppleCount(), roll.getMushroomCount(), roll.getLeafCount());
    }

    public List<CollectableForestPiece> getCollectedForestPieces() {
        List<CollectableForestPiece> collectedForestPieces = new LinkedList<>();

        for(int i = 0; i < appleCount; i++)
            collectedForestPieces.add(CollectableForestPiece.APPLE);

        for(int i = 0; i < mushroomCount; i++)
            collectedForestPieces.add(CollectableForestPiece.MUSHROOM);

        for(int i = 0; i < leafCount; i++)
            collectedForestPieces.add(CollectableForestPiece.LEAF);

        return collectedForestPieces;
    }

    public int getAppleCount() {
        return appleCount;
    }

    public void setAppleCount(int appleCount) {
        this.appleCount = appleCount;
    }

    public int getMushroomCount() {
        return mushroomCount;
    }

    public void setMushroomCount(int mushroomCount) {
        this.mushroomCount = mushroomCount;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(int leafCount) {
        this.leafCount = leafCount;
    }

    public int getCollectedItemsCount() {
        return appleCount + mushroomCount + leafCount;
    }

    @Override
    public String toString() {
        return "Roll{" +
                "appleCount=" + appleCount +
                ", mushroomCount=" + mushroomCount +
                ", leafCount=" + leafCount +
                '}';
    }
}

package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.LinkedList;
import java.util.List;

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

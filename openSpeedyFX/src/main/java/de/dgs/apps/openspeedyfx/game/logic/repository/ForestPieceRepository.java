package de.dgs.apps.openspeedyfx.game.logic.repository;

import de.dgs.apps.openspeedyfx.game.logic.model.Roll;

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

public class ForestPieceRepository {
    private static final int MAX_PIECES_COUNT = 6;

    private int appleCount;
    private int mushroomCount;
    private int leafCount;

    public ForestPieceRepository(){
        resetPieces();
    }

    public int getAvailableAppleCount(){
        return appleCount;
    }

    public int getAvailableMushroomCount(){
        return mushroomCount;
    }

    public int getAvailableLeafCount(){
        return leafCount;
    }


    public void processRoll(Roll roll){
        appleCount -= roll.getAppleCount();
        mushroomCount -= roll.getMushroomCount();
        leafCount -= roll.getLeafCount();
    }

    private void resetPieces(){
        appleCount = MAX_PIECES_COUNT;
        mushroomCount = MAX_PIECES_COUNT;
        leafCount = MAX_PIECES_COUNT;
    }

    private boolean shouldReset(){
        return appleCount <= 2 || mushroomCount <= 2 || leafCount <= 2;
    }

    public void update(){
        if(shouldReset())
            resetPieces();
    }
}

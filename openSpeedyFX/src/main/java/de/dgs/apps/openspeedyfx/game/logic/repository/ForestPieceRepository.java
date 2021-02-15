package de.dgs.apps.openspeedyfx.game.logic.repository;

import de.dgs.apps.openspeedyfx.game.logic.model.Roll;

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

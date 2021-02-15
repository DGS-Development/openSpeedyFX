package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.List;

public interface GameModeCallback {
    void onInitialized(List<? extends Actor> actors);
    void onRoll(int appleCount, int mushroomCount, int leafCount);
    void onActivePlayerSet(Player player);
    void onReadyToMove(List<Move> possibleMoves, int appleCount, int mushroomCount, int leafCount);
    void onUnableToMove();
    void onTooManyItemsCollected(int itemsCount);
    void onPlayerWon(Player player);
    void onPlayerLost(Player player);
    void onGameDone(List<Player> winners);
    void onPlayerInDanger(Player player);
    void onFoxMove(NPC fox, List<Tile> tilesToMove);
}

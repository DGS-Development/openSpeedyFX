package de.dgs.apps.openspeedyfx.game.logic.model;

public interface GameMode {
    void nextTurn();
    boolean isGameOver();
    void playerWon(Player player);
    void playerLost(Player player);
}

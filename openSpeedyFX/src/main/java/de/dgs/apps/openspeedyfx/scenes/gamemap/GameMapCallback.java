package de.dgs.apps.openspeedyfx.scenes.gamemap;

public interface GameMapCallback {
    void onMapIsReady();

    void onGameOver();

    void onException(Exception exception);
}

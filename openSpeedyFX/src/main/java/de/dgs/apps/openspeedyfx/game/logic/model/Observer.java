package de.dgs.apps.openspeedyfx.game.logic.model;

public interface Observer<T> {
    void update(T updatedObj);
}

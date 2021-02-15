package de.dgs.apps.openspeedyfx.game.logic.model;

public interface Observable {
    void register(Observer<Actor> observer);
    void unregister(Observer<Actor> observer);
    void notifyObservers(Actor obj);
}

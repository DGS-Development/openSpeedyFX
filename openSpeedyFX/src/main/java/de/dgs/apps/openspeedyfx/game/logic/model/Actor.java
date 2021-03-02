package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Actor implements Observable{
    private Tile currentTile;
    private final List<Observer<Actor>> observers = new ArrayList<>();
    private boolean initialized;

    public void movePiece(Tile newTile){
        initialized = false;
        currentTile = newTile;
        notifyObservers(this);
    }
    public Tile getCurrentTile(){
        return currentTile;
    }

    public void register(Observer<Actor> observer){
        observers.add(observer);
    }

    public void unregister(Observer<Actor> observer){
        observers.remove(observer);
    }

    public void notifyObservers(Actor obj){
        // ConcurrentModificationException if no new List is instantiated
        new ArrayList<>(observers).forEach(actorObserver -> actorObserver.update(obj));
    }

    public void setCurrentTile(Tile tile){
        if(initialized) return;
        initialized = true;
        this.currentTile = tile;
    }

    @Override
    public String toString() {
        return "currentTile: " + currentTile;
    }
}

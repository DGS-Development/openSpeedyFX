package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
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

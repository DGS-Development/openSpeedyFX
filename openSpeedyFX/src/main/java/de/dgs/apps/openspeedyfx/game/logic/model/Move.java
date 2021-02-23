package de.dgs.apps.openspeedyfx.game.logic.model;

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

public class Move {
    private final Tile startTile;
    private Tile endTile;

    public Move(Tile startTile){
        this.startTile = startTile;
    }

    public Move(Tile startTile, Tile endTile){
        this.startTile = startTile;
        this.endTile = endTile;
    }

    public void setEndTile(Tile endTile) {
        this.endTile = endTile;
    }

    public Tile getStartTile() {
        return startTile;
    }

    public Tile getEndTile() {
        return endTile;
    }

    @Override
    public String toString() {
        return "Bewegung von " + startTile.getTileType() + " nach " + endTile.getTileType();
    }
}

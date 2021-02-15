package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.LinkedList;
import java.util.List;

public class Tile {
    private List<Tile> adjacent;
    private final TileType tileType;

    public Tile(TileType tileType, List<Tile> adjacent){
        this.tileType = tileType;
        this.adjacent = adjacent;
    }

    public Tile(TileType tileType){
        this(tileType, new LinkedList<>());
    }

    public TileType getTileType() {
        return tileType;
    }

    public List<Tile> getAdjacent() {
        return adjacent;
    }

    public void setAdjacent(List<Tile> adjacent) {
        this.adjacent = adjacent;
    }

    @Override
    public String toString() {
        return "" + tileType;
    }
}

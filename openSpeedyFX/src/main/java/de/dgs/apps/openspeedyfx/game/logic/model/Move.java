package de.dgs.apps.openspeedyfx.game.logic.model;

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

package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private final Tile startTile;

    public Map(Tile tile){
        startTile = tile;
    }

    public List<Move> getPossibleMoves(Player player, Roll roll){
        List<Move> possibleMoves = new ArrayList<>();
        List<Tile> connectedTiles = player.getCurrentTile().getAdjacent();
        List<CollectableForestPiece> collectedForestPieces = roll.getCollectedForestPieces();

        connectedTiles.forEach(tile -> {
            collectedForestPieces.forEach(collectableForestPiece -> {
                if(tile.getTileType().name().equals(collectableForestPiece.name()) || tile.getTileType() == TileType.END){
                    possibleMoves.add(new Move(player.getCurrentTile(), tile));
                }
            });
        });

        return possibleMoves;
    }

    public boolean canMove(Player player, Roll roll){
        if(player != null) {
            List<Tile> connectedTiles = player.getCurrentTile().getAdjacent();
            List<CollectableForestPiece> collectedForestPieces = roll.getCollectedForestPieces();

            for(Tile t : connectedTiles){
                for(CollectableForestPiece piece : collectedForestPieces){
                    if(t.getTileType().name().equals(piece.name()) || t.getTileType() == TileType.END){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Tile getStartTile() {
        return startTile;
    }

    public Tile getHedgehogStart(){
        if(startTile.getTileType() == TileType.HEDGEHOG_START) return startTile;

        Tile hedgehogStart = startTile.getAdjacent().get(0);
        Tile lastTile = startTile;

        while(hedgehogStart.getTileType() != TileType.HEDGEHOG_START){
            for(Tile t : hedgehogStart.getAdjacent()){
                if(t.getTileType() == TileType.HEDGEHOG_START){
                    return t;
                }
            }
            for(Tile t : hedgehogStart.getAdjacent()){
                if(t != lastTile){
                    lastTile = hedgehogStart;
                    hedgehogStart = t;
                }
            }
        }
        return null;
    }

    public Tile getFoxStart(){
        if(startTile.getTileType() == TileType.FOX_OFFSET) return startTile;
        return null;
    }

    public Tile getEnd(){
        Tile endTile = startTile;
        while(!endTile.getAdjacent().isEmpty() || endTile.getAdjacent() != null){
            endTile = endTile.getAdjacent().get(0);
        }
        return endTile;
    }
}

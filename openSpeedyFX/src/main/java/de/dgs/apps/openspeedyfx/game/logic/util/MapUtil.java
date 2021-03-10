package de.dgs.apps.openspeedyfx.game.logic.util;

import de.dgs.apps.openspeedyfx.game.logic.model.Tile;

import java.util.*;

public class MapUtil {
    public static Stack<Tile> shortestPath(Tile startTile, Tile endTile){
        int count = 0;
        Queue<Tile> queue = new LinkedList<>();
        java.util.Map<Tile, Integer> visited = new LinkedHashMap<>();

        visited.put(startTile, count);
        queue.offer(startTile);

        while(!queue.isEmpty()){
            count++;
            Tile currentTile = queue.remove();
            if(currentTile == endTile){
                break;
            }

            int finalCount = count;
            currentTile.getAdjacent().forEach(tile -> {
                if(!visited.containsKey(tile)){
                    visited.put(tile, finalCount);
                    queue.offer(tile);
                }
            });
        }

        return reconstructPath(startTile, endTile, visited);
    }

    private static Stack<Tile> reconstructPath(Tile startTile, Tile finalTile, Map<Tile, Integer> visited) {
        Stack<Tile> tiles = new Stack<>();

        // Add the destination to the stack
        tiles.push(finalTile);

        while(finalTile != startTile){
            for(Tile tile : finalTile.getAdjacent()){
                if(visited.containsKey(tile) && visited.get(tile) < visited.get(finalTile)){
                    finalTile = tile;
                    tiles.push(tile);
                    break;
                }
            }
        }

        // Remove the tile the fox is already standing on from the Stack
        if(tiles.size() > 1){
            tiles.pop();
        }

        return tiles;
    }
}

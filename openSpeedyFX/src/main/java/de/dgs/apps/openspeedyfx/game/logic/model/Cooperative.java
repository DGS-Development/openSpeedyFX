package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.*;

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

public class Cooperative extends AbstractGameMode {
    private final NPC fox;
    private final int foxMoves;

    public Cooperative(Player player, GameModeCallback cooperativeCallback, de.dgs.apps.openspeedyfx.game.logic.model.Map map) {
        this(List.of(player), cooperativeCallback, map);
    }

    public Cooperative(List<Player> player, GameModeCallback cooperativeCallback, de.dgs.apps.openspeedyfx.game.logic.model.Map map) {
        this(player, cooperativeCallback, map, 2);
    }

    public Cooperative(List<Player> player, GameModeCallback cooperativeCallback, Map map, int foxMoves) {
        super(List.of(player.get(0)), cooperativeCallback, map);

        this.foxMoves = foxMoves;
        this.fox = new NPC();
        getPlayers().get(0).register(getEndConditionObserver());
        this.fox.register(getEndConditionObserver());
        onPiecesSetup();
    }

    protected void onPiecesSetup() {
        fox.setCurrentTile(getMap().getFoxStart());
        getPlayers().get(0).setCurrentTile(getMap().getHedgehogStart());

        List<Actor> actors = new ArrayList<>(2);
        actors.add(getPlayers().get(0));
        actors.add(this.fox);

        getGameModeCallback().onInitialized(actors);
    }

    @Override
    protected void onAdditionalMove(Turn.Builder turnBuilder) {
        moveFox(turnBuilder);
    }

    private void moveFox(Turn.Builder turnBuilder){
        if(getPlayers().isEmpty())return;

        List<Tile> foxMoves = new ArrayList<>();

        Tile foxTile = fox.getCurrentTile();
        Tile playerTile = getPlayers().get(0).getCurrentTile();

        Stack<Tile> moves = shortestPath(foxTile, playerTile);

        while(!moves.isEmpty() && foxMoves.size() < this.foxMoves){
            foxMoves.add(moves.pop());
        }

        for (Tile t : foxMoves) {
            fox.movePiece(t);
            Move foxMove = new Move(fox.getCurrentTile(), t);
            turnBuilder.addFoxMove(foxMove);
        }

        if(foxMoves.isEmpty()){
            return;
        }

        getGameModeCallback().onFoxMove(fox, foxMoves);
    }

    private Stack<Tile> shortestPath(Tile startTile, Tile endTile){
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

    private Stack<Tile> reconstructPath(Tile startTile, Tile finalTile, java.util.Map<Tile, Integer> visited){
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

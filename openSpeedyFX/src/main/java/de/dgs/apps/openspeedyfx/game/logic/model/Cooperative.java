package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.*;

public class Cooperative extends AbstractGameMode {
    private final NPC fox;
    private final List<Player> winners;
    private final List<Player> losers;
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
        this.winners = new ArrayList<>();
        this.losers = new ArrayList<>();
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
    public void playerWon(Player player) {
        winners.add(player);
        getGameModeCallback().onPlayerWon(player);
        player.setCurrentTile(getMap().getFoxStart());
        getGameModeCallback().onGameDone(winners);
    }

    @Override
    public void playerLost(Player player) {
        losers.add(player);
        getGameModeCallback().onPlayerLost(player);
        player.setCurrentTile(getMap().getFoxStart());
        getGameModeCallback().onGameDone(winners);
    }

    @Override
    protected void onAdditionalMove(Turn.Builder turnBuilder) {
        for(int i = 0; i < foxMoves; i++){
            moveFox(turnBuilder);
        }
    }

    private void moveFox(Turn.Builder turnBuilder){
        List<Tile> foxMoves = new ArrayList<>();

        Tile foxTile = fox.getCurrentTile();
        Tile playerTile = getPlayers().get(0).getCurrentTile();

        Stack<Tile> moves = shortestPath(foxTile, playerTile);

        if(!moves.isEmpty()){
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
        Queue<Tile> queue = new ArrayDeque<>();
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
        while(finalTile != startTile){
            if(startTile.getAdjacent().contains(finalTile)){
                tiles.push(finalTile);
                break;
            }
            for(Tile tile : finalTile.getAdjacent()){
                if(visited.containsKey(tile) && visited.get(tile) < visited.get(finalTile)){
                    finalTile = tile;
                    tiles.push(tile);
                    break;
                }
            }
        }
        if(tiles.size() > 1){
            tiles.pop();
        }
        return tiles;
    }

}

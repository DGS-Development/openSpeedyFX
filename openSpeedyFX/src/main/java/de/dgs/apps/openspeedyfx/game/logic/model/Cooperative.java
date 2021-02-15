package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.*;

public class Cooperative extends AbstractGameMode {
    private final NPC fox;
    private final List<Player> winners;
    private final List<Player> losers;
    private int foxMoves;

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
    protected Player getNextPlayer() {
        if(!winners.isEmpty() || !losers.isEmpty()){
            setGameOver(true);
            getGameModeCallback().onGameDone(winners);
        }
        return getPlayers().get(0);
    }

    @Override
    public void playerWon(Player player) {
        winners.add(player);
        getGameModeCallback().onPlayerWon(player);
        player.setCurrentTile(getMap().getFoxStart());
    }

    @Override
    public void playerLost(Player player) {
        losers.add(player);
        getGameModeCallback().onPlayerLost(player);
        player.setCurrentTile(getMap().getFoxStart());
    }

    @Override
    protected void onAdditionalMove(Turn.Builder turnBuilder) {
        for(int i = 0; i < foxMoves; i++){
            moveFox(turnBuilder);
        }
    }

    private void moveFox(Turn.Builder turnBuilder){
        List<Tile> foxMoves = new ArrayList<>(2);

        Tile foxTile = fox.getCurrentTile();

        if(!getPlayers().isEmpty() && foxTile.getAdjacent().contains(getPlayers().get(0).getCurrentTile())){
            foxMoves.add(getPlayers().get(0).getCurrentTile());
        } else if (foxTile.getAdjacent().contains(getMap().getHedgehogStart()) && foxTile.getTileType() == TileType.FOX_OFFSET) {
            foxMoves.add(foxTile.getAdjacent().get(foxTile.getAdjacent().indexOf(getMap().getHedgehogStart())));
        } else if (foxTile.getAdjacent().size() == 1) {
            foxMoves.add(foxTile.getAdjacent().get(0));
        } else if (foxTile.getAdjacent().size() < 3 && !foxTile.getAdjacent().contains(getMap().getHedgehogStart())) {
            for (Tile t : foxTile.getAdjacent()) {
                if (t != foxTile)
                    foxMoves.add(t);
            }
        }

        List<Move> playerMoves = new LinkedList<>();

        getTurnRepository().getAllForPlayer(getPlayers().get(0)).forEach(turn -> {
            playerMoves.addAll(turn.getPlayerMoves());
        });

        List<Tile> playerMoveTiles = new LinkedList<>();

        playerMoves.forEach(move -> {
            playerMoveTiles.add(move.getEndTile());
        });

        if (foxTile == getMap().getHedgehogStart()) {
            foxTile.getAdjacent().forEach(tile -> {
                if (playerMoveTiles.contains(tile)) {
                    foxMoves.add(tile);
                }
            });
        }

        foxTile.getAdjacent().forEach(tile -> {
            if (playerMoveTiles.contains(tile)) {
                foxMoves.add(tile);
            }
        });

        for (Tile t : foxMoves) {
            fox.movePiece(t);
            Move foxMove = new Move(fox.getCurrentTile(), t);
            turnBuilder.addFoxMove(foxMove);
        }

        getGameModeCallback().onFoxMove(fox, foxMoves);
    }

}

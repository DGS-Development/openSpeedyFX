package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
import java.util.List;

public class Competitive extends AbstractGameMode{
    private final List<Player> winners;
    private final List<Player> losers;

    public Competitive(List<Player> players, GameModeCallback competitiveCallback, Map map){
        super(players, competitiveCallback, map);
        this.winners = new ArrayList<>();
        this.losers = new ArrayList<>();
        onPiecesSetup();
    }

    protected void onPiecesSetup(){
        for(Player p : getPlayers()){
            p.setCurrentTile(getMap().getHedgehogStart());
        }
        getGameModeCallback().onInitialized(getPlayers());
    }

    @Override
    public void playerWon(Player player) {
        winners.add(player);
        getGameModeCallback().onPlayerWon(player);
    }

    @Override
    public void playerLost(Player player) {
        losers.add(player);
        getGameModeCallback().onPlayerLost(player);
    }

    @Override
    protected Player getNextPlayer(){
        if(getPlayers().isEmpty()) return null;

        if(getTurnRepository().getLast() == null) return getPlayers().get(0);

        Player lastPlayer = getTurnRepository().getLast().getPlayer();
        int playerIndex = getPlayers().indexOf(lastPlayer);
        if(playerIndex == getPlayers().size() - 1) return getPlayers().get(0);

        if(!winners.isEmpty()) {
            setGameOver(true);
            losers.addAll(getPlayers());
            getGameModeCallback().onGameDone(winners);
            nextTurn();
        }

        return getPlayers().get(playerIndex + 1);
    }
}

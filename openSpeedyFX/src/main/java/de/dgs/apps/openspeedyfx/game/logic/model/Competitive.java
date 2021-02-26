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
        getGameModeCallback().onGameDone(winners);
    }

    @Override
    public void playerLost(Player player) {
        losers.add(player);
        getGameModeCallback().onPlayerLost(player);
        getGameModeCallback().onGameDone(winners);
    }
}

package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
import java.util.List;

public class Competitive extends AbstractGameMode{

    public Competitive(List<Player> players, GameModeCallback competitiveCallback, Map map){
        super(players, competitiveCallback, map);
        onPiecesSetup();
    }

    protected void onPiecesSetup(){
        for(Player p : getPlayers()){
            p.setCurrentTile(getMap().getHedgehogStart());
        }
        getGameModeCallback().onInitialized(getPlayers());
    }
}

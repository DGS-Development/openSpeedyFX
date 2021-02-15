package de.dgs.apps.openspeedyfx.game.logic.repository;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;
import de.dgs.apps.openspeedyfx.game.logic.model.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TurnRepository {
    private final List<Turn> turns;

    public TurnRepository(){
        turns = new ArrayList<>();
    }

    public List<Turn> getAll(){
        return turns;
    }

    public List<Turn> getAllForPlayer(Player player){
        return turns.stream().filter(turn -> turn.getPlayer().equals(player)).collect(Collectors.toList());
    }

    public Turn getLast(){
        if(turns.size() < 1) return null;
        return turns.get(turns.size() - 1);
    }

    public Turn getLastForPlayer(Player player){
        List<Turn> playerTurns = getAllForPlayer(player);
        return playerTurns.get(playerTurns.size() - 1);
    }

    public void addTurn(Turn turn){
        turns.add(turn);
    }
}

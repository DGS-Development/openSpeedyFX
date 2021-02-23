package de.dgs.apps.openspeedyfx.game.logic.repository;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;
import de.dgs.apps.openspeedyfx.game.logic.model.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

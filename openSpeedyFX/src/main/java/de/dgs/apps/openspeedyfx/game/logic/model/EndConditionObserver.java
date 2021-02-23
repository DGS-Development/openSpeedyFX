package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.List;

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

public class EndConditionObserver implements Observer<Actor>{

    private final AbstractGameMode gameMode;

    public EndConditionObserver(AbstractGameMode gameMode){
        this.gameMode = gameMode;
    }

    @Override
    public void update(Actor updateActor) {

        if(updateActor instanceof Player){
            if(updateActor.getCurrentTile().getTileType() == TileType.END){
                gameMode.playerWon((Player) updateActor);
                gameMode.endTurn();
            }
        }

        if(updateActor instanceof NPC){
            List<Player> players = gameMode.getPlayers();
            for(Player p : players){
                if(p.getCurrentTile() == updateActor.getCurrentTile()){
                    gameMode.playerLost(p);
                }
                p.getCurrentTile().getAdjacent().forEach(tile -> {
                    if(tile == updateActor.getCurrentTile()){
                        gameMode.getGameModeCallback().onPlayerInDanger(p);
                    }
                });
            }
        }
    }
}

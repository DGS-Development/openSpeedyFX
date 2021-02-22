package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
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

public class Turn {
    private final List<Move> playerMoves;
    private final Player player;

    private Turn(Builder builder){
        Roll roll = builder.roll;
        playerMoves = builder.playerMoves;
        List<Move> foxMoves = builder.foxMoves;
        player = builder.player;
    }

    public static class Builder{
        private Roll roll;
        private final List<Move> playerMoves = new ArrayList<>();
        private final List<Move> foxMoves = new ArrayList<>();
        private final Player player;

        public Builder(Player player){
            this.player = player;
        }

        public Builder addRoll(Roll roll){
            this.roll = roll;
            return this;
        }

        public Builder addPlayerMove(Move move){
            playerMoves.add(move);
            return this;
        }

        public Builder addFoxMove(Move move){
            foxMoves.add(move);
            return this;
        }

        public Turn build(){
            return new Turn(this);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Move> getPlayerMoves() {
        return playerMoves;
    }
}

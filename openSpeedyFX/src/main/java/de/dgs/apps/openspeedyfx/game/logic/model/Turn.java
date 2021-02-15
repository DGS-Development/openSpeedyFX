package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.ArrayList;
import java.util.List;

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

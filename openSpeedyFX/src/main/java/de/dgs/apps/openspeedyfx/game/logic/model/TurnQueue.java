package de.dgs.apps.openspeedyfx.game.logic.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TurnQueue {
    private final Queue<Player> turnQueue;
    private final List<Player> playerList;

    TurnQueue(List<Player> playerList){
        this.playerList = playerList;
        this.turnQueue = new LinkedList<>();
        repopulateQueue();
    }

    public Player getNextPlayer(){
        if(turnQueue.isEmpty()){
            repopulateQueue();
        }

        return turnQueue.poll();
    }

    private void repopulateQueue(){
        turnQueue.addAll(playerList);
    }
}

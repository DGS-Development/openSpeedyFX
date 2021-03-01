package de.dgs.apps.openspeedyfx.game.logic.model;


import java.util.ArrayList;
import java.util.List;

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
                updateActor.unregister(this);
                gameMode.endTurn();
            }
        }

        if(updateActor instanceof NPC){
            List<Player> players = new ArrayList<>(gameMode.getPlayers());
            for(Player p : players){
                if(p.getCurrentTile() == updateActor.getCurrentTile()){
                    gameMode.playerLost(p);
                    p.unregister(this);
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

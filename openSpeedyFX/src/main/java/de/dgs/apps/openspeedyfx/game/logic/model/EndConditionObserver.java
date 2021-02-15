package de.dgs.apps.openspeedyfx.game.logic.model;


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

package de.dgs.apps.openspeedyfx.game.logic.model;

import de.dgs.apps.openspeedyfx.game.logic.repository.ForestPieceRepository;
import de.dgs.apps.openspeedyfx.game.logic.repository.TurnRepository;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGameMode implements GameMode{
    private boolean isGameOver = false;
    private boolean awaitNextMove = false;
    private boolean moved = false;
    private final Map map;
    private final TurnRepository turnRepository;
    private final ForestPieceRepository forestPieceRepository;
    private final GameModeCallback gameModeCallback;
    private final List<Player> players;
    private final EndConditionObserver endConditionObserver;
    private final TurnQueue turnQueue;
    private final List<Player> winners;
    private final List<Player> losers;

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    public AbstractGameMode(List<Player> players, GameModeCallback gameModeCallback, Map map){
        this.map = map;
        this.gameModeCallback = gameModeCallback;
        this.players = new ArrayList<>(players);
        this.turnRepository = new TurnRepository();
        this.forestPieceRepository = new ForestPieceRepository();
        this.endConditionObserver = new EndConditionObserver(this);
        this.turnQueue = new TurnQueue(players);
        this.winners = new ArrayList<>();
        this.losers = new ArrayList<>();
        for(Player p : players){
            p.register(endConditionObserver);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    protected abstract void onPiecesSetup();

    public Map getMap() {
        return map;
    }

    public GameModeCallback getGameModeCallback() {
        return gameModeCallback;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public ForestPieceRepository getForestPieceRepository() {
        return forestPieceRepository;
    }

    public TurnRepository getTurnRepository() {
        return turnRepository;
    }

    public EndConditionObserver getEndConditionObserver() {
        return endConditionObserver;
    }

    @Override
    public void playerWon(Player player) {
        winners.add(player);
        players.remove(player);
        gameModeCallback.onPlayerWon(player);
        gameModeCallback.onGameDone(winners);
    }

    @Override
    public void playerLost(Player player) {
        losers.add(player);
        players.remove(player);
        gameModeCallback.onPlayerLost(player);
        gameModeCallback.onGameDone(winners);
    }

    public void nextTurn() {
        moved = false;
        moveActivePlayer = getNextPlayer();
        if(moveActivePlayer == null){
            endTurn();
        }
        getForestPieceRepository().update();
        getGameModeCallback().onActivePlayerSet(moveActivePlayer);
        getGameModeCallback().onRoll(
                getForestPieceRepository().getAvailableAppleCount(),
                getForestPieceRepository().getAvailableMushroomCount(),
                getForestPieceRepository().getAvailableLeafCount());
    }

    private Player getNextPlayer(){
        return turnQueue.getNextPlayer();
    }

    /**
     * Gets executed after the player moved his character. This is useful when other actions are required, if the player was able to move forward.
     * @param turnBuilder
     */
    protected void onAdditionalMove(Turn.Builder turnBuilder) {
        //Ignore...
    }

    private Player moveActivePlayer;
    private Turn.Builder moveTurnBuilder;
    private Roll realRoll;
    private Roll moveRoll;

    public void onRollCompleted(Roll roll) {
        moveRoll = roll;
        realRoll = new Roll(roll);

        boolean tooManyItems = roll.getCollectedItemsCount() >= 5;

        moveTurnBuilder = new Turn.Builder(moveActivePlayer);
        moveTurnBuilder.addRoll(new Roll(roll));

        if(!tooManyItems && getMap().canMove(moveActivePlayer, roll)) {
            awaitNextMove = true;
            List<Move> possibleMoves = getMap().getPossibleMoves(moveActivePlayer, roll);
            getGameModeCallback().onReadyToMove(possibleMoves, roll.getAppleCount(), roll.getMushroomCount(), roll.getLeafCount());
            turnRepository.addTurn(moveTurnBuilder.build());
        } else {
            awaitNextMove = false;

            if(tooManyItems) {
                getGameModeCallback().onTooManyItemsCollected(roll.getCollectedItemsCount());
            }
            else {
                getGameModeCallback().onUnableToMove();
            }

            turnRepository.addTurn(moveTurnBuilder.build());

            endTurn();
        }
    }

    public boolean onMoveSelected(Move selectedMove) {
        if(!awaitNextMove)
            return false;

        moved = true;

        TileType tileType = selectedMove.getEndTile().getTileType();

        if(tileType == TileType.APPLE){
            moveRoll.setAppleCount(moveRoll.getAppleCount() - 1);
        }else if(tileType == TileType.MUSHROOM){
            moveRoll.setMushroomCount(moveRoll.getMushroomCount() - 1);
        }else{
            moveRoll.setLeafCount(moveRoll.getLeafCount() - 1);
        }

        moveActivePlayer.movePiece(selectedMove.getEndTile());
        moveTurnBuilder.addPlayerMove(selectedMove);

        boolean canPerformAdditionalMove = getMap().canMove(moveActivePlayer, moveRoll);

        if(canPerformAdditionalMove){
            List<Move> possibleMoves = getMap().getPossibleMoves(moveActivePlayer, moveRoll);
            getGameModeCallback().onReadyToMove(possibleMoves, moveRoll.getAppleCount(), moveRoll.getMushroomCount(), moveRoll.getLeafCount());
        }
        else {
            getTurnRepository().addTurn(moveTurnBuilder.build());

            awaitNextMove = false;

            onAdditionalMove(moveTurnBuilder);

            getForestPieceRepository().processRoll(realRoll);

            endTurn();
        }

        getTurnRepository().addTurn(moveTurnBuilder.build());

        return true;
    }

    public void onEarlyTurnEnd(){
        if(moved){
            getForestPieceRepository().processRoll(realRoll);
            onAdditionalMove(moveTurnBuilder);
        }
    }

    public void endTurn(){
        if(!isGameOver){
            nextTurn();
        }
    }
}

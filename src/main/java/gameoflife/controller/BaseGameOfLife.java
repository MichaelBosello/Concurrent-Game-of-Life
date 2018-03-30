package gameoflife.controller;

import gameoflife.boardmanager.BaseBoardManager;
import gameoflife.board.Board;
import gameoflife.boardmanager.BoardManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseGameOfLife implements GameOfLife{

    private static final Logger LOGGER = Logger.getLogger( BaseGameOfLife.class.getName() );

    private final int ROW = 50;
    private final int COLUMN = 50;


    private final Set<GameObserver> gameWatcher = new HashSet<>();
    private AtomicBoolean run = new AtomicBoolean();
    private AtomicBoolean nextAlreadyPrepared = new AtomicBoolean();
    private Semaphore consumedEvent = null;
    BoardManager game;

    public BaseGameOfLife(Semaphore consumedEvent) {
        this.consumedEvent = consumedEvent;
        game = new BaseBoardManager(ROW,COLUMN);

    }

    @Override
    public void start(){
        if(nextAlreadyPrepared.getAndSet(false)){
            notifyNewState(game.getBoard(),game.getLivingCell());
        }
        if(!run.getAndSet(true)){
            LOGGER.log(Level.FINE, "Start event received by backend");
            new Thread(() -> {
                while (run.get()){
                    try {
                        LOGGER.log(Level.FINEST, "Try to acquire event lock");
                        consumedEvent.acquire();
                        LOGGER.log(Level.FINEST, "Lock acquired, updating board");
                        game.updateBoard();
                        LOGGER.log(Level.FINEST, "New board ready");
                        if(run.get()){
                            notifyNewState(game.getBoard(),game.getLivingCell());
                        }else{
                            nextAlreadyPrepared.set(true);
                        }

                    } catch (InterruptedException e) {
                        LOGGER.log(Level.SEVERE, "Can't acquire event lock " + e.toString(), e);
                    }
                }
            }).start();
        }
    }

    @Override
    public void stop(){
        this.run.set(false);
        LOGGER.log(Level.FINE, "Stop event received by backend");
    }

    @Override
    public void addObserver(GameObserver observer){
        this.gameWatcher.add(observer);
    }

    private void notifyNewState(Board newBoard, int livingCell){
        for (final GameObserver observer : this.gameWatcher){
            observer.nextBoardComplete(newBoard);
            observer.livingCellUpdated(livingCell);
        }
    }


    @Override
    public Board getBoard() {
        return game.getBoard();
    }

    @Override
    public int getLivingCell() {
        return game.getLivingCell();
    }

}

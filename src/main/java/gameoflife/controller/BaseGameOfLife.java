package gameoflife.controller;

import gameoflife.board.BaseBoard;
import gameoflife.boardmanager.BaseBoardManager;
import gameoflife.board.Board;
import gameoflife.boardmanager.BoardManager;
import gameoflife.boardmanager.ConcurrentBoardManager;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseGameOfLife implements GameOfLife{

    private final Logger LOGGER = Logger.getLogger( BaseGameOfLife.class.getName() );
    private final StopWatch boardComputation = new MillisecondStopWatch();
    private final String TIME_UNIT = "ms";

    private final int ROW = 5000;
    private final int COLUMN = 5000;


    private final Set<GameObserver> gameWatcher = new HashSet<>();
    private AtomicBoolean run = new AtomicBoolean();
    private Semaphore consumedEvent = null;
    private Semaphore startEvent = new Semaphore(0);
    BoardManager game;

    public BaseGameOfLife(Semaphore consumedEvent) {
        this.consumedEvent = consumedEvent;
        game = new ConcurrentBoardManager(ROW,COLUMN);

        new Thread(new Updater(),"Thread-update").start();
    }

    private class Updater implements Runnable {
        private final Logger LOGGER = Logger.getLogger( Updater.class.getName() );

        public void run() {
            while (true){
                try {
                    LOGGER.log(Level.FINEST, "Try to acquire event lock");
                    consumedEvent.acquire();
                    LOGGER.log(Level.FINEST, "Lock acquired, updating board");
                    LOGGER.log(Level.FINE, "CPU intensive compute next board, Thread: " + Thread.currentThread().getName());

                    boardComputation.start();
                    game.updateBoard();
                    boardComputation.stop();
                    LOGGER.log(Level.INFO, "Board computation time: ("+TIME_UNIT+") " + boardComputation.getTime());

                    LOGGER.log(Level.FINEST, "New board ready");
                    while (!run.get()){
                        startEvent.acquire();
                    }
                    notifyNewState(game.getBoard(),game.getLivingCell());
                    LOGGER.log(Level.FINER, "Notified new board");
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Lock request interrupted" + e.toString(), e);
                }
            }
        }
    }

    @Override
    public void start(){
        this.run.set(true);
        if(startEvent.availablePermits() == 0){
            startEvent.release();
        }
        LOGGER.log(Level.FINE, "Start event received by backend");
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
            observer.nextBoardComplete(new BaseBoard(newBoard));
            observer.livingCellUpdated(livingCell);
        }
    }


    @Override
    public Board getBoard() {
        return new BaseBoard(game.getBoard());
    }

    @Override
    public int getLivingCell() {
        return game.getLivingCell();
    }

}

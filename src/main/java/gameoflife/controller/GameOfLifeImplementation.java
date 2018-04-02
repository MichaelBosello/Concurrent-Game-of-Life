package gameoflife.controller;

import gameoflife.board.Board;
import gameoflife.board.BoardFactory;
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

public class GameOfLifeImplementation implements GameOfLife{

    private static final Logger LOGGER = Logger.getLogger( GameOfLifeImplementation.class.getName() );
    private final Set<GameObserver> gameWatcher = new HashSet<>();
    private AtomicBoolean run = new AtomicBoolean();
    private Semaphore startEvent = new Semaphore(0);
    private Semaphore consumedEvent;
    private BoardManager game;

    public GameOfLifeImplementation(int row, int column, Semaphore consumedEvent, BoardManager.BoardType startBoard) {
        this.consumedEvent = consumedEvent;
        game = new ConcurrentBoardManager(row, column, startBoard);
        new Thread(new Updater(),"Thread-update").start();
    }

    private class Updater implements Runnable {
        private static final String TIME_UNIT = "ms";
        private final Logger logger = Logger.getLogger( Updater.class.getName() );
        private final StopWatch boardComputationTimer = new MillisecondStopWatch();

        public void run() {
            while (true){
                try {
                    //logger.log(Level.FINE, "Board update manager, Thread: " + Thread.currentThread().getName());

                    //logger.log(Level.FINEST, "Try to acquire event lock");
                    consumedEvent.acquire();
                    //logger.log(Level.FINEST, "Lock acquired, updating board");

                    boardComputationTimer.start();
                    game.updateBoard();
                    boardComputationTimer.stop();
                    logger.log(Level.INFO, "Board computation time: ("+TIME_UNIT+") " + boardComputationTimer.getTime());

                    //logger.log(Level.FINEST, "New board ready");

                    while (!run.get()){
                        startEvent.acquire();
                    }
                    notifyNewState(game.getBoard(), game.getLivingCell());
                    //logger.log(Level.FINER, "Notified new board");

                } catch (InterruptedException e) {
                    //logger.log(Level.SEVERE, "Lock request interrupted" + e.toString(), e);
                }
            }
        }
    }

    private void notifyNewState(Board newBoard, int livingCell){
        for (final GameObserver observer : this.gameWatcher){
            observer.nextBoardComplete(BoardFactory.createCopyBoard(newBoard));
            observer.livingCellUpdate(livingCell);
        }
    }

    @Override
    public void start(){
        this.run.set(true);
        if(startEvent.availablePermits() == 0){
            startEvent.release();
        }
        //LOGGER.log(Level.FINE, "Start event received by backend");
    }

    @Override
    public void stop(){
        this.run.set(false);
        //LOGGER.log(Level.FINE, "Stop event received by backend");
    }

    @Override
    public void addObserver(GameObserver observer){
        this.gameWatcher.add(observer);
    }

    @Override
    public Board getBoard() {
        return BoardFactory.createCopyBoard(game.getBoard());
    }

    @Override
    public int getLivingCell() {
        return game.getLivingCell();
    }

}

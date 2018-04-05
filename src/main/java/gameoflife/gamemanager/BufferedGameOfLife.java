package gameoflife.gamemanager;

import gameoflife.board.Board;
import gameoflife.board.BoardFactory;
import gameoflife.boardmanager.BoardManager;
import gameoflife.boardmanager.ConcurrentBoardManager;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BufferedGameOfLife implements GameOfLife{

    private static final Logger LOGGER = Logger.getLogger( BufferedGameOfLife.class.getName() );
    private static final int BUFFER_SIZE = 2;
    private final Set<GameObserver> gameWatcher = new HashSet<>();
    private final BlockingQueue<Board> bufferBoard = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final BlockingQueue<Integer> bufferLivingCell = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private AtomicBoolean run = new AtomicBoolean();
    private Semaphore startEvent = new Semaphore(0);
    private Semaphore consumedEvent;
    private BoardManager game;

    public BufferedGameOfLife(int row, int column, Semaphore consumedEvent, BoardManager.BoardType startBoard) {
        this.consumedEvent = consumedEvent;
        game = new ConcurrentBoardManager(row, column, startBoard);
        new Thread(new Notifier(), "Thread-notifier").start();
        new Thread(new Updater(), "Thread-updater").start();
    }

    private class Updater implements Runnable {
        private static final String TIME_UNIT = "ms";
        private final Logger logger = Logger.getLogger( Updater.class.getName() );
        private final StopWatch boardComputationTimer = new MillisecondStopWatch();

        public void run() {
            while (true){
                //logger.log(Level.FINE, "Board updater, request computation, Thread: " + Thread.currentThread().getName());
                boardComputationTimer.start();
                game.updateBoard();
                boardComputationTimer.stop();
                logger.log(Level.INFO, "Board computation time: (" + TIME_UNIT + ") " + boardComputationTimer.getTime());
                try {
                    bufferBoard.put(BoardFactory.createCopyBoard(game.getBoard()));
                    bufferLivingCell.put(game.getLivingCell());
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Error, blocking queue waiting interrupted" + e.toString(), e);
                }
                //logger.log(Level.FINER, "New board put in queue");
            }
        }
    }

    private class Notifier implements Runnable {
        private final Logger logger = Logger.getLogger( Notifier.class.getName() );

        public void run() {
            while (true){
                try {
                    //logger.log(Level.FINE, "Board notifier, Thread: " + Thread.currentThread().getName());
                    //logger.log(Level.FINEST, "Try to acquire event lock");
                    consumedEvent.acquire();
                    //logger.log(Level.FINEST, "Lock acquired, updating board");

                    while (!run.get()){
                        startEvent.acquire();
                    }
                    notifyNewState(bufferBoard.take(), bufferLivingCell.take());
                    //logger.log(Level.FINER, "Notified new board");
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Lock request interrupted" + e.toString(), e);
                }
            }
        }
    }

    private void notifyNewState(Board newBoard, int livingCell){
        for (final GameObserver observer : this.gameWatcher){
            observer.nextBoardComplete(newBoard);
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

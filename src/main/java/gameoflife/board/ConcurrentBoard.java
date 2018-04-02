package gameoflife.board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrentBoard extends BaseBoard{

    private static final Logger LOGGER = Logger.getLogger( ConcurrentBoard.class.getName() );
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors() + 1 ;
    private final Semaphore subComputationDone = new Semaphore(0);
    private final List<subBoardWorker> workers = new ArrayList<>();
    private static final Executor executorPool = Executors.newFixedThreadPool(PROCESSORS);

    public ConcurrentBoard(int row, int column) {
        super(row, column);
    }

    public ConcurrentBoard(Board board) {
        super(board.getRow(), board.getColumn());
        concurrentIterateCell((row, column) -> this.board[row][column] = board.isCellAlive(row, column));
    }


    public void initializeWithRandomState(){
        concurrentIterateCell((row, column) -> board[row][column] = ThreadLocalRandom.current().nextBoolean());
    }


    private void concurrentIterateCell(cellIterator toPerform){

        int cellPerThread = (row * column) / PROCESSORS;
        int remainCell = (row * column) - (cellPerThread * PROCESSORS);
        for(int counter = 0; counter < PROCESSORS; counter++){
            if(counter < remainCell){
                workers.add(new subBoardWorker(
                        counter * cellPerThread + counter, (counter+1) * (cellPerThread + 1), toPerform ));
            }else{
                workers.add(new subBoardWorker(
                        counter * cellPerThread + remainCell, (counter+1) * cellPerThread + remainCell, toPerform ));
            }
        }

        for (subBoardWorker worker : workers){
            executorPool.execute(worker);
        }
        try {
            subComputationDone.acquire(PROCESSORS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error on synchronization " + e.toString(), e);
        }

    }

    private class subBoardWorker implements Runnable{
        private int startRow, startColumn, endRow, endColumn;
        cellIterator toPerform;

        public subBoardWorker(int startRow, int startColumn, int endRow, int endColumn, cellIterator toPerform) {
            this.startRow = startRow;
            this.startColumn = startColumn;
            this.endRow = endRow;
            this.endColumn = endColumn;
            this.toPerform = toPerform;
        }

        public subBoardWorker(int startCell, int endCell, cellIterator toPerform){
            this(startCell/column,
                    startCell%column,
                    endCell/column,
                    endCell%column,
                    toPerform);
        }

        @Override
        public void run() {
            iterateSubCell(startRow,startColumn,endRow,endColumn,
                    (row, column) -> toPerform.doForEachCell(row,column));
            subComputationDone.release();
        }
    }
}

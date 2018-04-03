package gameoflife.board.boardworker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardConcurrentWorker {

    private static final Logger LOGGER = Logger.getLogger( BoardConcurrentWorker.class.getName() );
    protected static final int PROCESSORS = Runtime.getRuntime().availableProcessors() + 1 ;

    protected final List<SubBoardWorker> workers = new ArrayList<>();
    protected final Executor executorPool = Executors.newFixedThreadPool(PROCESSORS);
    protected final Semaphore subComputationDone = new Semaphore(0);
    protected boolean logging;
    protected int column;

    public BoardConcurrentWorker(int row, int column, boolean logging) {
        this.logging = logging;
        this.column = column;

        if(logging)
            LOGGER.log(Level.INFO, "Worker number:" + PROCESSORS);
        int cellPerThread = (row * column) / PROCESSORS;
        int remainCell = (row * column) - (cellPerThread * PROCESSORS);
        for(int workerIndex = 0; workerIndex < PROCESSORS; workerIndex++){
            if(workerIndex < remainCell){
                workers.add(createSubBoardWorker(
                        workerIndex * cellPerThread + workerIndex, (workerIndex+1) * (cellPerThread + 1) ));
            }else{
                workers.add(createSubBoardWorker(
                        workerIndex * cellPerThread + remainCell, (workerIndex+1) * cellPerThread + remainCell ));
            }
        }
    }

    protected SubBoardWorker createSubBoardWorker(int startCell, int endCell){
        return new SubBoardWorker(startCell, endCell);
    }

    public void executeAndWait(){
        for (SubBoardWorker worker : workers){
            executorPool.execute(worker);
        }
        try {
            subComputationDone.acquire(PROCESSORS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error on synchronization " + e.toString(), e);
        }
    }

    protected class SubBoardWorker implements Runnable{
        protected final Logger logger = Logger.getLogger( SubBoardWorker.class.getName() );
        protected int startRow, startColumn, endRow, endColumn;

        protected SubBoardWorker(int startRow, int startColumn, int endRow, int endColumn) {
            this.startRow = startRow;
            this.startColumn = startColumn;
            this.endRow = endRow;
            this.endColumn = endColumn;
            if(logging)
                logger.log(Level.INFO, "Created new worker for cells from: [" + startRow + "," + startColumn +
                    "] to: [" + endRow + "," + endColumn + "]");
        }

        protected SubBoardWorker(int startCell, int endCell){
            this(startCell/column, startCell%column, endCell/column, endCell%column);
            if(logging)
                logger.log(Level.INFO, "Call creation of new worker: from " + startCell + " to " + endCell);
        }

        @Override
        public void run() {
            computation();
            subComputationDone.release();
        }

        protected void computation(){}
    }
}

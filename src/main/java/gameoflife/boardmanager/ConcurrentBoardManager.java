package gameoflife.boardmanager;

import gameoflife.board.BoardFactory;
import gameoflife.board.ManagedBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrentBoardManager extends BaseBoardManager {

    private final Logger LOGGER = Logger.getLogger( ConcurrentBoardManager.class.getName() );
    private final boolean DEBUG = false;
    private ManagedBoard debugBoard;

    private final List<subBoardWorker> WORKERS = new ArrayList<>();
    private final int PROCESSORS = Runtime.getRuntime().availableProcessors() + 1 ;
    //best performance with core/2 worker cause of hyper-threading core:
    //private final int PROCESSORS = Runtime.getRuntime().availableProcessors() > 1 ? Runtime.getRuntime().availableProcessors()/2 : 1 ;
    private final Executor EXECUTOR_POOL = Executors.newFixedThreadPool(PROCESSORS);
    private final Semaphore SUB_COMPUTATION_DONE = new Semaphore(0);

    public ConcurrentBoardManager(int row, int column) {
        super(row, column);
        LOGGER.log(Level.INFO, "Worker number:" + PROCESSORS);
        if(DEBUG)
            debugBoard = BoardFactory.createEmptyBoard(row,column);
        int cellPerThread = (row * column)/PROCESSORS;
        int remainCell = (row * column) - (cellPerThread * PROCESSORS);
        for(int counter = 0; counter < PROCESSORS; counter++){
            if(counter < remainCell){
                WORKERS.add(new subBoardWorker(
                        counter * cellPerThread + counter,(counter+1) * (cellPerThread + 1) ));
            }else{
                WORKERS.add(new subBoardWorker(
                        counter * cellPerThread + remainCell,(counter+1) * cellPerThread + remainCell ));
            }

        }

    }

    @Override
    public void updateBoard(){
        livingCell = 0;

        for (subBoardWorker worker :WORKERS){
            //new Thread(worker).start();
            EXECUTOR_POOL.execute(worker);
        }

        try {
            SUB_COMPUTATION_DONE.acquire(PROCESSORS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (subBoardWorker worker :WORKERS){
            livingCell += worker.getSubLivingCell();
        }

        if(DEBUG){
            debugBoard.iterateCell((row,column)->{
                if(!debugBoard.isCellAlive(row,column)){
                    LOGGER.log(Level.SEVERE, "ERROR CELL NOT UPDATED [" + row + "," + column + "]");
                }
                debugBoard.setDead(row,column);
            });
        }

        swapBoard();

    }

    private class subBoardWorker implements Runnable{
        private int startRow, startColumn, endRow, endColumn;
        private int subLivingCell;

        public subBoardWorker(int startRow, int startColumn, int endRow, int endColumn) {
            this.startRow = startRow;
            this.startColumn = startColumn;
            this.endRow = endRow;
            this.endColumn = endColumn;

            LOGGER.log(Level.INFO, "Created new worker for cells from: [" + startRow + "," + startColumn +
                    "] to: [" + endRow + "," + endColumn + "]");
        }

        public subBoardWorker(int startCell, int endCell){
            this(startCell/currentBoard.getColumn(),
                    startCell%currentBoard.getColumn(),
                    endCell/currentBoard.getColumn(),
                    endCell%currentBoard.getColumn());
            LOGGER.log(Level.INFO, "Call creation of new worker: from " + startCell + " to " + endCell);
        }


        @Override
        public void run() {
            subLivingCell = 0;
            currentBoard.iterateSubCell(startRow,startColumn,endRow,endColumn,
                    (row, column) -> {
                        if(DEBUG){
                            if(debugBoard.isCellAlive(row,column))
                                LOGGER.log(Level.SEVERE, "ERROR CELL UPDATE MORE THAN ONCE [" + row + "," + column + "]");
                            debugBoard.setAlive(row,column);
                        }
                        if(cellSurvive(row,column)){
                            nextBoard.setAlive(row,column);
                            subLivingCell++;
                        }else{
                            nextBoard.setDead(row,column);
                        }

                    });
            SUB_COMPUTATION_DONE.release();
        }

        public int getSubLivingCell() {
            return subLivingCell;
        }
    }
}

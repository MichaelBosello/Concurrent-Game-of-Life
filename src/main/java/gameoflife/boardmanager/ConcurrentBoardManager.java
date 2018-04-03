package gameoflife.boardmanager;

import gameoflife.board.BoardFactory;
import gameoflife.board.ManagedBoard;
import gameoflife.board.boardworker.BoardConcurrentWorker;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrentBoardManager extends BaseBoardManager {

    private static final Logger LOGGER = Logger.getLogger( ConcurrentBoardManager.class.getName() );
    private static final boolean DEBUG = false;
    private ManagedBoard debugBoard;
    private UpdateWorker updater;

    public ConcurrentBoardManager(int row, int column, BoardType startBoard) {
        super(row, column, startBoard);
        if(DEBUG)
            debugBoard = BoardFactory.createEmptyBoard(row, column);
        updater = new UpdateWorker(row, column, true);
    }

    @Override
    public void updateBoard(){
        updater.executeAndWait();

        if(DEBUG){
            debugBoard.iterateCell((row, column)-> {
                if(!debugBoard.isCellAlive(row, column)){
                    LOGGER.log(Level.SEVERE, "ERROR CELL NOT UPDATED [" + row + "," + column + "]");
                }
                debugBoard.setDead(row, column);
            });
        }

        swapBoard();
    }


    private class UpdateWorker extends BoardConcurrentWorker {

        protected final Executor executorPool = Executors.newFixedThreadPool(PROCESSORS);

        protected UpdateWorker(int row, int column, boolean logging) {
            super(row, column, logging);
        }

        @Override
        protected SubBoardWorker createSubBoardWorker(int startCell, int endCell){
            return new SubBoardWorker(startCell, endCell);
        }

        @Override
        public void executeAndWait(){
            super.executeAndWait();
            livingCell = 0;
            for (BoardConcurrentWorker.SubBoardWorker worker : workers){
                livingCell += ((SubBoardWorker)worker).getSubLivingCell();
            }
        }

        @Override
        protected void execute(){
            for (BoardConcurrentWorker.SubBoardWorker worker : workers){
                executorPool.execute(worker);
            }
        }

        protected class SubBoardWorker extends BoardConcurrentWorker.SubBoardWorker{
            protected int subLivingCell;

            protected SubBoardWorker(int startCell, int endCell) {
                super(startCell, endCell);
            }

            @Override
            protected void computation() {
                subLivingCell = 0;
                currentBoard.iterateSubCell(startRow,startColumn,endRow,endColumn,
                        (row, column) -> {

                            if(DEBUG){
                                if(debugBoard.isCellAlive(row, column))
                                    logger.log(Level.SEVERE, "ERROR CELL UPDATE MORE THAN ONCE [" + row + "," + column + "]");
                                debugBoard.setAlive(row, column);
                            }

                            if(cellSurvive(row, column)){
                                nextBoard.setAlive(row, column);
                                subLivingCell++;
                            }else{
                                nextBoard.setDead(row, column);
                            }
                        });
            }

            private int getSubLivingCell() {
                return subLivingCell;
            }
        }

    }
}

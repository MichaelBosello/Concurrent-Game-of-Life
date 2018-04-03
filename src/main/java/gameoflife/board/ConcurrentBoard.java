package gameoflife.board;

import gameoflife.board.boardworker.BoardConcurrentWorker;

import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentBoard extends BaseBoard{

    private BoardConcurrentTaskWorker executor;

    public ConcurrentBoard(int row, int column) {
        super(row, column);
        executor = new BoardConcurrentTaskWorker(row, column, false);
    }

    public ConcurrentBoard(Board board) {
        this(board.getRow(), board.getColumn());
        concurrentIterateCell((row, column) -> this.board[row][column] = board.isCellAlive(row, column));
    }

    @Override
    public void initializeWithRandomState(){
        concurrentIterateCell((row, column) -> board[row][column] = ThreadLocalRandom.current().nextBoolean());
    }

    private void concurrentIterateCell(cellIterator toPerform){
        executor.setToPerform(toPerform);
        executor.executeAndWait();
    }


    private class BoardConcurrentTaskWorker extends BoardConcurrentWorker {

        protected BoardConcurrentTaskWorker(int row, int column, boolean logging) {
            super(row, column, logging);
        }

        @Override
        protected SubBoardWorker createSubBoardWorker(int startCell, int endCell){
            return new SubBoardWorker(startCell, endCell);
        }

        protected void setToPerform(cellIterator toPerform){
            for (BoardConcurrentWorker.SubBoardWorker worker : workers){
                ((SubBoardWorker)worker).setToPerform(toPerform);
            }
        }

        protected class SubBoardWorker extends BoardConcurrentWorker.SubBoardWorker {
            protected cellIterator toPerform;

            protected SubBoardWorker(int startCell, int endCell) {
                super(startCell, endCell);
            }

            @Override
            protected void computation() {
                iterateSubCell(startRow, startColumn, endRow, endColumn,
                        (row, column) -> toPerform.doForEachCell(row, column));
            }

            protected void setToPerform(cellIterator toPerform) {
                this.toPerform = toPerform;
            }
        }
    }
}

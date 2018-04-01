package gameoflife.board;

import java.util.Random;
public class BaseBoard implements ManagedBoard {
    private int row, column;
    private boolean[][] board;

    public BaseBoard(Board board){
        this.row = board.getRow();
        this.column = board.getColumn();
        this.board = new boolean[row][column];
        board.iterateCell((row, column) -> this.board[row][column] = board.isCellAlive(row,column));
    }

    public BaseBoard(final int row, final int column) {
        if(row <= 0 || column <= 0){
            throw new IllegalArgumentException();
        }
        this.row = row;
        this.column = column;
        board = new boolean[row][column];
    }

    @Override
    public void initializeWithRandomState(){
        initializeWithRandomState(System.nanoTime());
    }

    @Override
    public void initializeWithRandomState(long seed){
        final Random randomForBoard = new Random();
        randomForBoard.setSeed(seed);
        iterateCell((row, column) -> board[row][column] = randomForBoard.nextBoolean());
    }

    @Override
    public void iterateCell(cellIterator toPerform){
        iterateSubCell(0,0,getRow(),0,toPerform);
    }

    @Override
    public void iterateSubCell(int startRow, int startColumn, int endRow, int endColumn, cellIterator toPerform) {
        for (int row = startRow; row <= endRow; row++){
            int startSubColumn = 0;
            int endSubColumn = getColumn();
            if(row == startRow){
                startSubColumn = startColumn;
            }else if(row == endRow){
                endSubColumn = endColumn;
            }
            for (int column = startSubColumn; column < endSubColumn; column++){
                toPerform.doForEachCell(row,column);
            }
        }
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public boolean isCellAlive(final int row, final int column){
        return board[row][column];
    }

    @Override
    public void setAlive(final int row, final int column){
        board[row][column] = true;
    }

    @Override
    public void setDead(final int row, final int column){
        board[row][column] = false;
    }
}

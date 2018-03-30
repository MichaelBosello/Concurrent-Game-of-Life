package gameoflife.board;

import utility.MillisecondStopWatch;
import utility.StopWatch;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseBoard implements ManagedBoard {

    private int row, column;
    private boolean[][] board;
    private final StopWatch INITIALIZE_STOPWATCH = new MillisecondStopWatch();

    private static final Logger LOGGER = Logger.getLogger( ManagedBoard.class.getName() );

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
        INITIALIZE_STOPWATCH.start();
        final Random randomForBoard = new Random();
        randomForBoard.setSeed(seed);
        iterateCell((row, column) -> board[row][column] = randomForBoard.nextBoolean());
        INITIALIZE_STOPWATCH.stop();
        LOGGER.log(Level.INFO, "Random board created in (ms) " + INITIALIZE_STOPWATCH.getTime());
    }

    @Override
    public void iterateCell(cellIterator toPerform){
        for (int row = 0; row < getRow(); row++){
            for (int column = 0; column < getColumn(); column++){
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

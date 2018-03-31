package gameoflife.boardmanager;

import gameoflife.board.Board;
import gameoflife.board.BoardFactory;
import gameoflife.board.ManagedBoard;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseBoardManager implements BoardManager {

    private final Logger LOGGER = Logger.getLogger( BaseBoardManager.class.getName() );

    private ManagedBoard currentBoard;
    private ManagedBoard nextBoard;
    private int livingCell = 0;


    public BaseBoardManager(int row, int column) {
        currentBoard = BoardFactory.createLotOfGlider(row,column);
        nextBoard = BoardFactory.createCopyBoard(currentBoard);

        currentBoard.iterateCell((cellRow, cellColumn) -> {
            if(currentBoard.isCellAlive(cellRow,cellColumn)){
                livingCell++;
            }
        });

    }

    @Override
    public Board getBoard(){
        return currentBoard;
    }

    private boolean cellSurvive(int row, int column){
        int neighborhood = 0;

        int rowStart = (row > 0) ? (row - 1) : 0;
        int columnStart = (column > 0) ? (column - 1) : 0;
        int rowEnd = (row < currentBoard.getRow() -1) ? (row + 2):
                (row < currentBoard.getRow()) ? (row + 1) : currentBoard.getRow();
        int columnEnd = (column < currentBoard.getColumn() -1) ? (column + 2):
                (column < currentBoard.getColumn()) ? (column + 1) : currentBoard.getColumn();

        for(int nearRow = rowStart; nearRow < rowEnd; nearRow++){
            for(int nearColumn = columnStart; nearColumn < columnEnd; nearColumn++){
                neighborhood += currentBoard.isCellAlive(nearRow,nearColumn)? 1 : 0;
                LOGGER.log(Level.FINEST, "Cell [" + row + "," + column +
                        "] neighbour: [" + nearRow + "," + nearColumn + "]");
            }
        }

        if(currentBoard.isCellAlive(row,column)){
            neighborhood--;
            if(neighborhood == 2){
                LOGGER.log(Level.FINEST, "Cell [" + row + "," + column + "] survive");
                return true;
            }
        }

        if(neighborhood == 3){
            LOGGER.log(Level.FINEST, "Cell [" + row + "," + column + "] survive");
            return true;
        }

        LOGGER.log(Level.FINEST, "Cell [" + row + "," + column + "] dies");
        return false;
    }

    @Override
    public void updateBoard(){
        livingCell = 0;
        currentBoard.iterateCell((row, column) -> {
                if(cellSurvive(row,column)){
                    nextBoard.setAlive(row,column);
                    livingCell++;
                }else{
                    nextBoard.setDead(row,column);
                }

        });

        swapBoard();

    }

    private void swapBoard(){
        ManagedBoard temporaryBoard = currentBoard;
        currentBoard = nextBoard;
        nextBoard = temporaryBoard;
    }

    @Override
    public int getLivingCell() {
        return livingCell;
    }
}

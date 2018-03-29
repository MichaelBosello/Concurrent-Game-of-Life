package gameoflife.boardmanager;

import gameoflife.board.Board;
import gameoflife.board.BoardFactory;
import gameoflife.board.ManagedBoard;

public class BaseBoardManager implements BoardManager {

    private ManagedBoard currentBoard;
    private ManagedBoard nextBoard;
    private int livingCell = 0;


    public BaseBoardManager(int row, int column) {
        currentBoard = BoardFactory.createSimpleBoard(row, column);
        nextBoard = BoardFactory.createSimpleBoard(row, column);
    }

    @Override
    public Board getBoard(){
        return currentBoard;
    }

    private boolean cellSurvive(int row, int column){
        int neighborhood = 0;

        int rowStart = (row > 0) ? (row - 1) : 0;
        int columnStart = (column > 0) ? (column - 1) : 0;
        int rowEnd = (row < currentBoard.getRow()) ? (row + 1) : currentBoard.getRow();
        int columnEnd = (column < currentBoard.getColumn()) ? (column + 1) : currentBoard.getColumn();

        for(int nearRow = rowStart; nearRow < rowEnd; nearRow++){
            for(int nearColumn = columnStart; nearColumn < columnEnd; nearColumn++){
                neighborhood += currentBoard.isCellAlive(nearRow,nearColumn)? 1 : 0;
            }
        }

        if(currentBoard.isCellAlive(row,column)){
            neighborhood--;
            if(neighborhood == 2){
                return true;
            }
        }

        if(neighborhood == 3){
            return true;
        }


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

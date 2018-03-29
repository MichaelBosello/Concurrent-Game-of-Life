package gameoflife.boardmanager;

import gameoflife.board.Board;

public interface BoardManager {

    Board getBoard();

    void updateBoard();

    int getLivingCell();
}

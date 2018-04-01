package gameoflife.boardmanager;

import gameoflife.board.Board;

public interface BoardManager {

    enum BoardType { RANDOM, LWSS, GLIDER }

    Board getBoard();

    int getLivingCell();

    void updateBoard();
}

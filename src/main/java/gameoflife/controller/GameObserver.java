package gameoflife.controller;

import gameoflife.board.Board;

public interface GameObserver {

    void nextBoardComplete(Board board);
    void livingCellUpdate(int livingCell);
}

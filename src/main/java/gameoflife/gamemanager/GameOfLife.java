package gameoflife.gamemanager;

import gameoflife.board.Board;

public interface GameOfLife {

    void start();

    void stop();

    void addObserver(GameObserver observer);

    Board getBoard();

    int getLivingCell();
}

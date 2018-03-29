package gameoflife.board;

import gameoflife.board.Board;

public interface ManagedBoard extends Board {

    void setAlive(int row, int column);

    void setDead(int row, int column);

}

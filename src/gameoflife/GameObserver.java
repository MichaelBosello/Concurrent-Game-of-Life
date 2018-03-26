package gameoflife;

public interface GameObserver {

    void nextBoardComplete(Board board);
    void livingCellUpdate(int livingCell);
}

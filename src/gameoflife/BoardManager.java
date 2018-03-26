package gameoflife;

public interface BoardManager {

    Board getBoard();

    void updateBoard();

    int getLivingCell();
}

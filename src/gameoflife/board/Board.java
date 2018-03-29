package gameoflife.board;

public interface Board {

    @FunctionalInterface
    interface cellIterator {
        void doForEachCell(int row, int column);
    }

    void initializeWithRandomState(long seed);

    void initializeWithRandomState();

    void iterateCell(cellIterator toPerform);

    boolean isCellAlive(int row, int column);

    int getColumn();

    int getRow();

}

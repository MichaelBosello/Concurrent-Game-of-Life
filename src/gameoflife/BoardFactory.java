package gameoflife;

public class BoardFactory {

    private static final long SEED = System.nanoTime();

    private BoardFactory() {}

    public static ManagedBoard createSimpleBoard(int row, int column){
        ManagedBoard board = new BaseBoard(row, column);
        board.initializeWithRandomState(SEED);
        return board;
    }

}

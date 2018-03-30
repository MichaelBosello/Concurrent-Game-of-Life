package gameoflife.board;

public class BoardFactory {

    private static final long SEED = System.nanoTime();

    private BoardFactory() {}

    public static ManagedBoard createSimpleBoard(int row, int column){
        ManagedBoard board = new BaseBoard(row, column);
        board.initializeWithRandomState(SEED);
        return board;
    }

    public static ManagedBoard createRandomBoard(int row, int column, int seed){
        ManagedBoard board = new BaseBoard(row, column);
        board.initializeWithRandomState(seed);
        return board;
    }

}

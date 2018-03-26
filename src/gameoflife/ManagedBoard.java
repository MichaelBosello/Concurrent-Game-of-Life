package gameoflife;

public interface ManagedBoard extends Board {

    void setAlive(int row, int column);

    void setDead(int row, int column);

}

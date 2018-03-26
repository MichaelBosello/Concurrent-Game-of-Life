package gameoflife.controller;

import gameoflife.BaseBoardManager;
import gameoflife.Board;
import gameoflife.BoardManager;
import gameoflife.GameObserver;

import java.util.HashSet;
import java.util.Set;

public class GameOfLife {

    private final int ROW = 100;
    private final int COLUMN = 100;


    private final Set<GameObserver> gameWatcher = new HashSet<>();
    private boolean run = false;
    BoardManager game = new BaseBoardManager(ROW,COLUMN);

    public void start(){
        run = true;
        new Thread(() -> {
            while (run){
                game.updateBoard();
                notifyNewState(game.getBoard(),game.getLivingCell());
            }
        }).start();

    }

    public void stop(){
        this.run = false;
    }

    public void addObserver(GameObserver observer){
        this.gameWatcher.add(observer);
    }

    private void notifyNewState(Board newBoard, int livingCell){
        for (final GameObserver observer : this.gameWatcher){
            observer.nextBoardComplete(newBoard);
            observer.livingCellUpdate(livingCell);
        }
    }

}

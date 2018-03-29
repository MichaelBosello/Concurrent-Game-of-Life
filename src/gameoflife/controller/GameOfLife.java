package gameoflife.controller;

import gameoflife.boardmanager.BaseBoardManager;
import gameoflife.board.Board;
import gameoflife.boardmanager.BoardManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class GameOfLife {

    private final int ROW = 100;
    private final int COLUMN = 100;


    private final Set<GameObserver> gameWatcher = new HashSet<>();
    private boolean run = false;
    private Semaphore consumedEvent = null;
    BoardManager game = new BaseBoardManager(ROW,COLUMN);

    public void start(){
        run = true;
        new Thread(() -> {
            while (run){
                try {
                    consumedEvent.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    public void addComputeNextSemaphoreEvent(Semaphore consumedEvent){
        this.consumedEvent = consumedEvent;
    }

}

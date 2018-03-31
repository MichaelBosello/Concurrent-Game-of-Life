package application;

import gameoflife.board.Board;
import gameoflifegui.mainpanel.GameOfLifeGUI;

import javax.swing.*;

public class BaseGameObserver extends GameObserverImageComputation {

    public BaseGameObserver(GameOfLifeGUI window) {
        super(window);
    }

    @Override
    public void nextBoardComplete(final Board board) {
        SwingUtilities.invokeLater(() -> {
            window.updateBoard(board);
        });
    }
}

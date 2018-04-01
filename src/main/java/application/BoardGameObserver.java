package application;

import gameoflife.board.Board;
import gameoflifegui.mainpanel.GameOfLifeGUI;

import javax.swing.*;

public class BoardGameObserver extends GameObserverWithImageComputation {

    public BoardGameObserver(GameOfLifeGUI window) {
        super(window);
    }

    @Override
    public void nextBoardComplete(final Board board) {
        SwingUtilities.invokeLater(() -> {
            window.updateBoard(board);
        });
    }
}

package application;

import gameoflife.board.Board;
import gameoflife.controller.GameObserver;
import gameoflifegui.mainpanel.GameOfLifeGUI;

import javax.swing.*;

public class BoardGameObserver implements GameObserver {

    protected GameOfLifeGUI window;

    public BoardGameObserver(GameOfLifeGUI window) {
        this.window = window;
    }

    @Override
    public void nextBoardComplete(final Board board) {
        SwingUtilities.invokeLater(() -> window.updateBoard(board));
    }

    @Override
    public void livingCellUpdate(final int livingCell) {
        SwingUtilities.invokeLater(() -> window.updateLivingCellLabel(livingCell));
    }
}

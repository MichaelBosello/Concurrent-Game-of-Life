package application;

import gameoflife.board.Board;
import gameoflife.controller.GameObserver;
import gameoflifegui.mainpanel.GameOfLifeGUI;
import gameoflifegui.matrixtoimage.ConvertToImage;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameObserverImageComputation implements GameObserver{

    protected final Logger LOGGER = Logger.getLogger( GUIObserver.class.getName());
    protected final StopWatch CREATE_IMAGE_TIME = new MillisecondStopWatch();
    protected final String TIME_UNIT = "ms";
    protected GameOfLifeGUI window;

    public GameObserverImageComputation(GameOfLifeGUI window) {
        this.window = window;
    }

    @Override
    public void nextBoardComplete(final Board board) {

        LOGGER.log(Level.FINE, "CPU Intensive image creation, Thread: " + Thread.currentThread().getName());
        CREATE_IMAGE_TIME.start();
        final BufferedImage boardImage = ConvertToImage.boardToImageWithBigCells(board);
        CREATE_IMAGE_TIME.stop();
        LOGGER.log(Level.INFO, "BufferedImage creation time: ("+TIME_UNIT+") " + CREATE_IMAGE_TIME.getTime());

        SwingUtilities.invokeLater(() -> {
            window.updateBoard(boardImage);
        });
    }

    @Override
    public void livingCellUpdated(final int livingCell) {
        SwingUtilities.invokeLater(() -> {
            window.updateLivingCellLabel(livingCell);
        });
    }
}

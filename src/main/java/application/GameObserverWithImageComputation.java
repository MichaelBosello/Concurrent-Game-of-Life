package application;

import gameoflife.board.Board;
import gameoflifegui.mainpanel.GameOfLifeGUI;
import gameoflifegui.matrixtoimage.ConvertToImage;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameObserverWithImageComputation extends BoardGameObserver{

    private static final Logger LOGGER = Logger.getLogger( GameObserverWithImageComputation.class.getName());
    private static final StopWatch CREATE_IMAGE_TIME = new MillisecondStopWatch();
    private static final String TIME_UNIT = "ms";

    public GameObserverWithImageComputation(GameOfLifeGUI window) {
        super(window);
    }

    @Override
    public void nextBoardComplete(final Board board) {

        LOGGER.log(Level.FINE, "CPU Intensive image creation, Thread: " + Thread.currentThread().getName());
        CREATE_IMAGE_TIME.start();
        final BufferedImage boardImage = ConvertToImage.boardToImage(board);
        CREATE_IMAGE_TIME.stop();
        LOGGER.log(Level.INFO, "BufferedImage creation time: ("+TIME_UNIT+") " + CREATE_IMAGE_TIME.getTime());

        SwingUtilities.invokeLater(() -> {
            window.updateBoard(boardImage);
        });
    }
}

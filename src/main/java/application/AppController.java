package application;

import gameoflife.boardmanager.BoardManager;
import gameoflife.controller.GameOfLifeImplementation;
import gameoflife.controller.GameOfLife;
import gameoflifegui.mainpanel.GameOfLifeGUI;
import gameoflifegui.mainpanel.MainPanel;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import javax.swing.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController {

    /**
     * App parameter
     * */
    private final static int ROW = 10000;
    private final static int COLUMN = 10000;
    private final static BoardManager.BoardType startBoard = BoardManager.BoardType.RANDOM;
    /**
     *
     * */

    private static final Logger LOGGER = Logger.getLogger( AppController.class.getName() );
    private static final StopWatch BOOT_TIME = new MillisecondStopWatch();
    private static final StopWatch CREATE_BOARD_TIME = new MillisecondStopWatch();
    private static final String TIME_UNIT = "ms";
    private final static Semaphore CONSUME_EVENT = new Semaphore(0);

    public static void main(String args[]) {

        BOOT_TIME.start();
        LOGGER.log(Level.FINER, "Model initialization, Thread: " + Thread.currentThread().getName());

        CREATE_BOARD_TIME.start();
        GameOfLife gameOfLife = new GameOfLifeImplementation(ROW, COLUMN, CONSUME_EVENT, startBoard);
        CREATE_BOARD_TIME.stop();
        LOGGER.log(Level.INFO, "Board created in: (" + TIME_UNIT + ") " + CREATE_BOARD_TIME.getTime());

        try {
            SwingUtilities.invokeAndWait(() -> {
                GameOfLifeGUI window = new MainPanel(MainPanel.UpdateType.BOARD);
                window.addObserver(new GUIObserver(gameOfLife, CONSUME_EVENT));
                gameOfLife.addObserver(new BoardGameObserver(window));
                window.updateBoard(gameOfLife.getBoard());
                window.updateLivingCellLabel(gameOfLife.getLivingCell());
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error on boot " + e.toString(), e);
        }

        BOOT_TIME.stop();
        LOGGER.log(Level.INFO, "Boot overall time: " + BOOT_TIME.getTime());
    }

}

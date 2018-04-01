package application;

import gameoflife.controller.BaseGameOfLife;
import gameoflife.controller.GameOfLife;
import gameoflifegui.mainpanel.GameOfLifeGUI;
import gameoflifegui.mainpanel.MainPanel;
import gameoflifegui.matrixtoimage.ConvertToImage;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController {

    private final static Semaphore CONSUME_EVENT = new Semaphore(0);
    private static final Logger LOGGER = Logger.getLogger( AppController.class.getName() );
    private static final StopWatch BOOT_TIME = new MillisecondStopWatch();
    private static final StopWatch CREATE_BOARD_TIME = new MillisecondStopWatch();
    private static final String TIME_UNIT = "ms";

    public static void main(String args[]) {

        BOOT_TIME.start();
        LOGGER.log(Level.FINER, "Model initialization, Thread: " + Thread.currentThread().getName());

        CREATE_BOARD_TIME.start();
        GameOfLife gameOfLife = new BaseGameOfLife(CONSUME_EVENT);
        CREATE_BOARD_TIME.stop();
        LOGGER.log(Level.INFO, "Board created in: ("+TIME_UNIT+") " + CREATE_BOARD_TIME.getTime());

        try {
            SwingUtilities.invokeAndWait(() -> {
                final GameOfLifeGUI window = new MainPanel();
                window.addObserver(new GUIObserver(gameOfLife,CONSUME_EVENT));
                gameOfLife.addObserver(new BaseGameObserver(window));
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

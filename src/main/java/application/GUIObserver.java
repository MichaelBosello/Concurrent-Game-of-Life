package application;

import gameoflife.controller.GameOfLife;
import gameoflifegui.mainpanel.MainPanelObserver;
import utility.MillisecondStopWatch;
import utility.StopWatch;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUIObserver implements MainPanelObserver {

    private final Logger GUI_LOGGER = Logger.getLogger( GUIObserver.class.getName());
    private final StopWatch CREATE_BOARD_TIME = new MillisecondStopWatch();
    private final String TIME_UNIT = "ms";
    private GameOfLife gameOfLife;
    private Semaphore consumeEvent;

    public GUIObserver(GameOfLife gameOfLife, Semaphore consumeEvent) {
        this.gameOfLife = gameOfLife;
        this.consumeEvent = consumeEvent;
    }

    @Override
    public void startEvent() {
        GUI_LOGGER.log(Level.FINER, "GUI shot non-blocking start-event, Thread: " + Thread.currentThread().getName());
        CREATE_BOARD_TIME.start();
        gameOfLife.start();
    }

    @Override
    public void stopEvent() {
        GUI_LOGGER.log(Level.FINER, "GUI shot non-blocking stop-event, Thread: " + Thread.currentThread().getName());
        gameOfLife.stop();
    }

    @Override
    public void boardUpdated() {
        CREATE_BOARD_TIME.stop();
        GUI_LOGGER.log(Level.INFO, "Time between two frame: ("+TIME_UNIT+") " + CREATE_BOARD_TIME.getTime());
        GUI_LOGGER.log(Level.FINER, "GUI shot image updated-event, Thread: " + Thread.currentThread().getName());
        CREATE_BOARD_TIME.start();
        consumeEvent.release();
    }
}

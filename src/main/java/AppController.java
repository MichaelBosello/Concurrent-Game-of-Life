import gameoflife.board.Board;
import gameoflife.controller.BaseGameOfLife;
import gameoflife.controller.GameObserver;
import gameoflife.controller.GameOfLife;
import gameoflifegui.mainpanel.GameOfLifeGUI;
import gameoflifegui.mainpanel.MainPanel;
import gameoflifegui.mainpanel.MainPanelObserver;
import gameoflifegui.matrixtoimage.ConvertToImage;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController {

    private final static Semaphore CONSUME_EVENT = new Semaphore(0);
    private static final Logger LOGGER = Logger.getLogger( AppController.class.getName() );

    public static void main(String args[]) {

        GameOfLife gameOfLife = new BaseGameOfLife(CONSUME_EVENT);
        final BufferedImage firstImage = ConvertToImage.boardToImageWithBigCells(gameOfLife.getBoard());


        try {
            SwingUtilities.invokeAndWait(() -> {
                final GameOfLifeGUI window = new MainPanel();

                window.addObserver(new MainPanelObserver() {
                    @Override
                    public void startEvent() {
                        gameOfLife.start();
                    }

                    @Override
                    public void stopEvent() {
                        gameOfLife.stop();
                    }

                    @Override
                    public void boardUpdated() {
                        CONSUME_EVENT.release();
                    }
                });

                gameOfLife.addObserver(new GameObserver() {
                    @Override
                    public void nextBoardComplete(final Board board) {

                        final BufferedImage boardImage = ConvertToImage.boardToImageWithBigCells(board);

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
                });

                window.updateBoard(firstImage);
                window.updateLivingCellLabel(gameOfLife.getLivingCell());

            });
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Bootstrap interrupted " + e.toString(), e);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "InvocationTargetException on boot" + e.toString(), e);
        }

    }

}

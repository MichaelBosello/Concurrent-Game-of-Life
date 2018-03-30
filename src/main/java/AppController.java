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
import java.util.concurrent.Semaphore;

public class AppController {

    private final static Semaphore CONSUME_EVENT = new Semaphore(1);

    public static void main(String args[]) {

        GameOfLife gameOfLife = new BaseGameOfLife();
        gameOfLife.addComputeNextSemaphoreEvent(CONSUME_EVENT);

        SwingUtilities.invokeLater(() -> {
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

        });

    }

}

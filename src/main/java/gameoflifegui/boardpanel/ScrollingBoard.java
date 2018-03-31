package gameoflifegui.boardpanel;

import gameoflife.board.Board;
import gameoflifegui.matrixtoimage.ConvertToImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScrollingBoard extends BoardPanel{

    private static final Logger LOGGER = Logger.getLogger( BigImageScrollPane.class.getName() );
    private static final int BOARD_ROW = 100;
    private static final int BOARD_COLUMN = 100;

    private JLabel boardDisplay = new JLabel();
    private JScrollBar horizontalScroller;
    private JScrollBar verticalScroller;
    private int canvasWidth, canvasHeight;

    private boolean initialized = false;
    Board board = null;

    public ScrollingBoard() {
        this.setLayout(new BorderLayout());
        horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL);
        verticalScroller = new JScrollBar(JScrollBar.VERTICAL);
        horizontalScroller.setMinimum (0);
        verticalScroller.setMinimum (0);
        this.add(boardDisplay,BorderLayout.CENTER);
        this.add(horizontalScroller,BorderLayout.PAGE_END);
        this.add(verticalScroller,BorderLayout.LINE_END);

        horizontalScroller.addAdjustmentListener((e)-> updateImage());
        verticalScroller.addAdjustmentListener((e)-> updateImage());

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LOGGER.log(Level.FINER, "resizing");
                super.componentResized(e);
                canvasWidth = e.getComponent().getWidth() - verticalScroller.getWidth();
                canvasHeight = e.getComponent().getHeight() - horizontalScroller.getHeight();
                if(board != null){
                    verticalScroller.setMaximum(board.getRow() - BOARD_ROW);
                    horizontalScroller.setMaximum(board.getColumn() - BOARD_COLUMN);
                    updateImage();
                }
            }
        });
    }

    private void updateImage(){
        LOGGER.log(Level.FINE, "Image Update from [" + horizontalScroller.getValue() +
                "," + verticalScroller.getValue() + "] to [" +
                canvasWidth + "," + canvasHeight + "]");
        if(board != null && canvasWidth > 0 && canvasHeight > 0)
            boardDisplay.setIcon(new ImageIcon(
                    ConvertToImage.resize(
                    ConvertToImage.boardToImage(board,
                    verticalScroller.getValue(),horizontalScroller.getValue(),BOARD_ROW,BOARD_COLUMN),
                    canvasWidth,canvasHeight)));
    }

    public void updateDisplayedBoard(BufferedImage boardImage){
        //TODO
    }

    @Override
    public void updateDisplayedBoard(Board board) {
        if(!initialized){
            initialized = true;
            canvasWidth = this.getWidth() - verticalScroller.getWidth();
            canvasHeight = this.getHeight() - horizontalScroller.getHeight();
            verticalScroller.setMaximum(board.getRow() - BOARD_ROW);
            horizontalScroller.setMaximum(board.getColumn() - BOARD_COLUMN);
        }

        this.board = board;
        updateImage();
    }
}

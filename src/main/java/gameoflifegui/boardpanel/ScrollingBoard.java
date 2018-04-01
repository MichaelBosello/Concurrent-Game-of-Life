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
    private static int boardRow = 100;
    private static int boardColumn = 100;

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
                    if(board.getRow() < boardRow) {
                        boardRow = board.getRow();
                    }
                    if(board.getColumn() < boardColumn) {
                        boardColumn = board.getColumn();
                    }
                    verticalScroller.setMaximum(board.getRow() - boardRow);
                    horizontalScroller.setMaximum(board.getColumn() - boardColumn);
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
                    verticalScroller.getValue(),horizontalScroller.getValue(), boardRow, boardColumn),
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
            if(board.getRow() < boardRow) {
                boardRow = board.getRow();
            }
            if(board.getColumn() < boardColumn) {
                boardColumn = board.getColumn();
            }
            verticalScroller.setMaximum(board.getRow() - boardRow);
            horizontalScroller.setMaximum(board.getColumn() - boardColumn);
        }

        this.board = board;
        updateImage();
    }
}

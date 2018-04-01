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

    private static final Logger LOGGER = Logger.getLogger( ScrollingBoard.class.getName() );

    private int canvasRow = 100;
    private int canvasColumn = 100;
    private boolean initialized = false;
    Board board = null;

    private JLabel boardDisplay = new JLabel();
    private JScrollBar horizontalScroller;
    private JScrollBar verticalScroller;
    private int canvasWidth, canvasHeight;

    public ScrollingBoard() {
        horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScroller.addAdjustmentListener((e)-> updateImage());
        horizontalScroller.setMinimum (0);
        verticalScroller = new JScrollBar(JScrollBar.VERTICAL);
        verticalScroller.addAdjustmentListener((e)-> updateImage());
        verticalScroller.setMinimum (0);
        this.setLayout(new BorderLayout());
        this.add(boardDisplay,BorderLayout.CENTER);
        this.add(horizontalScroller,BorderLayout.PAGE_END);
        this.add(verticalScroller,BorderLayout.LINE_END);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LOGGER.log(Level.FINER, "resizing");
                super.componentResized(e);
                canvasWidth = e.getComponent().getWidth() - verticalScroller.getWidth();
                canvasHeight = e.getComponent().getHeight() - horizontalScroller.getHeight();
                if(board != null){
                    updateScroller();
                    updateImage();
                }
            }
        });
    }

    @Override
    public void updateDisplayedBoard(BufferedImage boardImage){
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDisplayedBoard(Board board) {
        this.board = board;
        if(!initialized){
            initialized = true;
            canvasWidth = this.getWidth() - verticalScroller.getWidth();
            canvasHeight = this.getHeight() - horizontalScroller.getHeight();
            updateScroller();
        }
        updateImage();
    }

    private void updateImage(){
        LOGGER.log(Level.FINE, "Image Update from [" + horizontalScroller.getValue() +
                "," + verticalScroller.getValue() + "] to [" +
                canvasWidth + "," + canvasHeight + "]");
        if(board != null && canvasWidth > 0 && canvasHeight > 0){
            BufferedImage subBoardImage = ConvertToImage.boardToImage(board,
                    verticalScroller.getValue(),horizontalScroller.getValue(), canvasRow, canvasColumn);
            boardDisplay.setIcon(new ImageIcon(ConvertToImage.resize(subBoardImage, canvasWidth,canvasHeight)));
        }

    }

    private void updateScroller(){
        if(board.getRow() < canvasRow) {
            canvasRow = board.getRow();
        }
        if(board.getColumn() < canvasColumn) {
            canvasColumn = board.getColumn();
        }
        verticalScroller.setMaximum(board.getRow() - canvasRow);
        horizontalScroller.setMaximum(board.getColumn() - canvasColumn);
    }
}

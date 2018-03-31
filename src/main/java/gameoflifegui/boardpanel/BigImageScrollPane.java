package gameoflifegui.boardpanel;

import gameoflife.board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BigImageScrollPane extends BoardPanel{

    private static final Logger LOGGER = Logger.getLogger( BigImageScrollPane.class.getName() );

    private JLabel boardDisplay = new JLabel();
    private JScrollBar horizontalScroller;
    private JScrollBar verticalScroller;
    private int canvasWidth, canvasHeight;

    private boolean initialized = false;
    BufferedImage boardImage = null;

    public BigImageScrollPane() {
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
                super.componentResized(e);
                canvasWidth = e.getComponent().getWidth() - verticalScroller.getWidth();
                canvasHeight = e.getComponent().getHeight() - horizontalScroller.getHeight();
                if(boardImage != null){
                    horizontalScroller.setMaximum(boardImage.getWidth() - canvasWidth);
                    verticalScroller.setMaximum(boardImage.getHeight() - canvasHeight);
                    updateImage();
                }
            }
        });
    }

    private void updateImage(){
        LOGGER.log(Level.FINE, "Image Update from [" + horizontalScroller.getValue() +
                            "," + verticalScroller.getValue() + "] to [" +
                            canvasWidth + "," + canvasHeight + "]");
        if(boardImage != null)
        boardDisplay.setIcon(new ImageIcon(
                boardImage.getSubimage(horizontalScroller.getValue(),verticalScroller.getValue(),
                        canvasWidth,canvasHeight)));
    }

    public void updateDisplayedBoard(BufferedImage boardImage){

        if(!initialized){
            initialized = true;
            canvasWidth = this.getWidth() - verticalScroller.getWidth();
            canvasHeight = this.getHeight() - horizontalScroller.getHeight();
            horizontalScroller.setMaximum(boardImage.getWidth() - canvasWidth);
            verticalScroller.setMaximum(boardImage.getHeight() - canvasHeight);
        }

        this.boardImage = boardImage;
        updateImage();
    }

    @Override
    public void updateDisplayedBoard(Board board) {
        //TODO
    }
}

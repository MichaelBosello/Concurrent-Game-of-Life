package gameoflifegui.boardpanel;

import gameoflife.board.Board;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class SimpleBoardPanel extends BoardPanel {

    JLabel boardDisplay = new JLabel();

    public SimpleBoardPanel() {
        this.add(boardDisplay);
    }


    public void updateDisplayedBoard(BufferedImage boardImage){
        boardDisplay.setIcon(new ImageIcon(boardImage));
    }

    @Override
    public void updateDisplayedBoard(Board board) {
        //TODO
    }

}
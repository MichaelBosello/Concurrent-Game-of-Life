package gameoflifegui.boardpanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class SimpleBoardPanel extends BoardPanel {

    JLabel boardDisplay = new JLabel();

    @Override
    public void initialize() {
        this.add(boardDisplay);
    }

    public void updateDisplayedBoard(BufferedImage boardImage){
        boardDisplay.setIcon(new ImageIcon(boardImage));
    }


}

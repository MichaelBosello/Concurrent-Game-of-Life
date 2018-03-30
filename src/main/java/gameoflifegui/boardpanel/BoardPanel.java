package gameoflifegui.boardpanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class BoardPanel extends JPanel{

    public abstract void initialize();

    public abstract void updateDisplayedBoard(BufferedImage boardImage);
}

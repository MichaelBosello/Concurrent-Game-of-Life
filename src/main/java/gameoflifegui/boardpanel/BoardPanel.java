package gameoflifegui.boardpanel;

import gameoflife.board.Board;

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class BoardPanel extends JPanel{

    public abstract void updateDisplayedBoard(BufferedImage boardImage);

    public abstract void updateDisplayedBoard(Board board);
}

package gameoflifegui.mainpanel;

import gameoflife.board.Board;

import java.awt.image.BufferedImage;

public interface GameOfLifeGUI {

    void updateBoard(BufferedImage boardImage);

    void updateBoard(Board boardImage);

    void updateLivingCellLabel(int livingCell);

    void addObserver(MainPanelObserver observer);

}

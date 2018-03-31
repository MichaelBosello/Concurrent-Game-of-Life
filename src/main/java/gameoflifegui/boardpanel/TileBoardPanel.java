package gameoflifegui.boardpanel;

import gameoflife.board.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gameoflifegui.matrixtoimage.ConvertToImage.RATIO;
import static gameoflifegui.matrixtoimage.ConvertToImage.getTile;

public class TileBoardPanel extends BoardPanel {

    private boolean initialized = false;
    List<JLabel> boardDisplayChunk = new ArrayList<>();


    @Override
    public void updateDisplayedBoard(BufferedImage boardImage){
        if(!initialized){
            initialized = true;
            GridLayout tilesLayout = new GridLayout(RATIO, RATIO,0,0);
            this.setLayout(tilesLayout);

            for(int tiles = 0; tiles < RATIO*RATIO; tiles++){
                JLabel tile = new JLabel();
                boardDisplayChunk.add(tile);
                this.add(tile);
            }
        }
        BufferedImage[] chunk = getTile(boardImage,RATIO,RATIO);
        for(int index = 0; index< RATIO*RATIO; index++){
            boardDisplayChunk.get(index).setIcon(new ImageIcon(chunk[index]));
        }

    }


    @Override
    public void updateDisplayedBoard(Board board) {
        //TODO
    }

}

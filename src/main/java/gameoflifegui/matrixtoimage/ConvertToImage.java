package gameoflifegui.matrixtoimage;

import gameoflife.board.Board;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

public class ConvertToImage {

    public static BufferedImage boardToImage(Board board){

        int width = board.getRow(), height = board.getColumn();

        byte BLACK = (byte)0, WHITE = (byte)255;
        byte[] map = {BLACK, WHITE};
        IndexColorModel icm = new IndexColorModel(1, map.length, map, map, map);

        int[] data = new int[width*height];
        for(int row=0; row<width; row++)
            for(int column=0; column<height; column++)
                data[row*height + column] = board.isCellAlive(row,column) ? BLACK:WHITE;

        WritableRaster raster = icm.createCompatibleWritableRaster(width, height);
        raster.setPixels(0, 0, width, height, data);
        return new BufferedImage(icm, raster, false, null);

    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage boardToImageWithBigCells(Board board){

        if(board.getColumn() > 8000 && board.getRow() > 8000){
            return boardToImage(board);
        }

        int resizeRatio = 1;
        if(board.getColumn() <= 2000 && board.getRow() <= 2000){
            resizeRatio = 8;
        } else
        if(board.getColumn() <= 4000 && board.getRow() <= 4000){
            resizeRatio = 5;
        } else
        if(board.getColumn() <= 6000 && board.getRow() <= 6000){
            resizeRatio = 3;
        } else
        if(board.getColumn() <=8000 && board.getRow() <= 8000){
            resizeRatio = 2;
        }

        return resize(boardToImage(board),board.getRow()*resizeRatio,board.getColumn()*resizeRatio);
    }
}

import gameoflifegui.GameOfLifeGUI;
import gameoflifegui.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

public class AppController {

    private final static int ROW = 1000;
    private final static int COLUMN = 1000;

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            GameOfLifeGUI window = new MainPanel();


            int w = 5000, h = 5000;

            // create the binary mapping
            byte BLACK = (byte)0, WHITE = (byte)255;
            byte[] map = {BLACK, WHITE};
            IndexColorModel icm = new IndexColorModel(1, map.length, map, map, map);

            // create checkered data
            int[] data = new int[w*h];
            for(int i=0; i<w; i++)
                for(int j=0; j<h; j++)
                    data[i*h + j] = i%4<2 && j%4<2 || i%4>=2 && j%4>=2 ? BLACK:WHITE;

            // create image from color model and data
            WritableRaster raster = icm.createCompatibleWritableRaster(w, h);
            raster.setPixels(0, 0, w, h, data);
            BufferedImage bi = new BufferedImage(icm, raster, false, null);

            window.updateBoard(bi);


        });
    }

}

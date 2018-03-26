import gameoflifegui.MainPanel;

import javax.swing.*;

public class AppController {

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            MainPanel window = new MainPanel();
        });
    }

}

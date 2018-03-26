package gameoflifegui;

import javax.swing.*;

public class MainPanel extends JFrame {

    BoardPanel board = new SimpleBoardPanel();
    JButton start = new JButton("Start");
    JButton stop = new JButton("Stop");
    JLabel aliveCellLabel = new JLabel();

    public MainPanel() {
        this.setTitle("Game of Life");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        board.initialize();
        this.add(board);

        JPanel commandPanel = new JPanel();
        commandPanel.add(start);
        commandPanel.add(stop);
        commandPanel.add(aliveCellLabel);

        this.add(commandPanel);

        this.setVisible(true);
    }
}

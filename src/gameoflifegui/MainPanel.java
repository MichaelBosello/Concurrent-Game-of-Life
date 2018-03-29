package gameoflifegui;

import gameoflifegui.boardpanel.BoardPanel;
import gameoflifegui.boardpanel.SimpleBoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainPanel extends JFrame implements GameOfLifeGUI{

    private final String LIVING_TEXT = "Living cell: ";

    BoardPanel board = new SimpleBoardPanel();
    JButton start = new JButton("Start");
    JButton stop = new JButton("Stop");
    JLabel aliveCellLabel = new JLabel(LIVING_TEXT + "0");

    public MainPanel() {
        this.setTitle("Game of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        board.initialize();

        JScrollPane boardPanel = new JScrollPane(board);
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        JPanel commandPanel = new JPanel();
        commandPanel.add(start);
        commandPanel.add(stop);
        commandPanel.add(aliveCellLabel);

        this.getContentPane().add(commandPanel,BorderLayout.PAGE_END);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void updateBoard(BufferedImage boardImage) {
        board.updateDisplayedBoard(boardImage);
    }
}

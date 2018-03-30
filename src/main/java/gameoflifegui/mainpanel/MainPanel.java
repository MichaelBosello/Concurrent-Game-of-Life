package gameoflifegui.mainpanel;

import gameoflife.controller.GameObserver;
import gameoflifegui.boardpanel.BoardPanel;
import gameoflifegui.boardpanel.SimpleBoardPanel;
import gameoflifegui.mainpanel.GameOfLifeGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class MainPanel extends JFrame implements GameOfLifeGUI {

    private final String LIVING_TEXT = "Living cell: ";
    private final Set<MainPanelObserver> guiObserver = new HashSet<>();

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
        start.addActionListener(e -> notifyStart());
        commandPanel.add(start);
        stop.addActionListener(e -> notifyStop());
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
        notifyUpdated();
    }

    @Override
    public void updateLivingCellLabel(int livingCell) {
        aliveCellLabel.setText(LIVING_TEXT + livingCell);
    }

    private void notifyStart(){
        for (final MainPanelObserver observer : this.guiObserver){
            observer.startEvent();
        }
    }
    private void notifyStop(){
        for (final MainPanelObserver observer : this.guiObserver){
            observer.stopEvent();
        }
    }

    private void notifyUpdated(){
        for (final MainPanelObserver observer : this.guiObserver){
            observer.boardUpdated();
        }
    }

    @Override
    public void addObserver(MainPanelObserver observer){
        this.guiObserver.add(observer);
    }


}

package gameoflifegui.mainpanel;

import gameoflife.board.Board;
import gameoflifegui.boardpanel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class MainPanel extends JFrame implements GameOfLifeGUI {

    public enum UpdateType { BOARD, BUFFERED_IMAGE }
    private static final String LIVING_TEXT = "Living cell: ";
    private final Set<MainPanelObserver> guiObserver = new HashSet<>();

    private BoardPanel boardPanel;
    private JLabel aliveCellLabel = new JLabel(LIVING_TEXT + "0");

    public MainPanel(UpdateType updateType) {
        this.setTitle("The Game of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        if(updateType == UpdateType.BOARD){
            boardPanel = new ScrollingBoard();
            this.getContentPane().add(boardPanel, BorderLayout.CENTER);
        }
        if(updateType == UpdateType.BUFFERED_IMAGE){
            boardPanel = new SimpleBoardPanel();
            JScrollPane boardPanel = new JScrollPane(this.boardPanel);
            this.getContentPane().add(boardPanel, BorderLayout.CENTER);
        }

        JPanel commandPanel = new JPanel();
        JButton start = new JButton("Start");
        start.addActionListener(e -> notifyStart());
        commandPanel.add(start);
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> notifyStop());
        commandPanel.add(stop);
        commandPanel.add(aliveCellLabel);
        this.getContentPane().add(commandPanel,BorderLayout.PAGE_END);
        this.setSize(1000, 1000);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void addObserver(MainPanelObserver observer){
        this.guiObserver.add(observer);
    }

    @Override
    public void updateBoard(BufferedImage boardImage) {
        boardPanel.updateDisplayedBoard(boardImage);
        notifyUpdated();
    }

    @Override
    public void updateBoard(Board board) {
        boardPanel.updateDisplayedBoard(board);
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
}

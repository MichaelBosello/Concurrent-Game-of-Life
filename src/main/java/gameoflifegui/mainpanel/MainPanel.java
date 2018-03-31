package gameoflifegui.mainpanel;

import gameoflife.board.Board;
import gameoflifegui.boardpanel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class MainPanel extends JFrame implements GameOfLifeGUI {

    private final String LIVING_TEXT = "Living cell: ";
    private final Set<MainPanelObserver> guiObserver = new HashSet<>();

    private BoardPanel board = new ScrollingBoard();
    private JLabel aliveCellLabel = new JLabel(LIVING_TEXT + "0");

    public MainPanel() {
        this.setTitle("Game of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        //JScrollPane boardPanel = new JScrollPane(board);
        this.getContentPane().add(board, BorderLayout.CENTER);

        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");

        JPanel commandPanel = new JPanel();
        start.addActionListener(e -> notifyStart());
        commandPanel.add(start);
        stop.addActionListener(e -> notifyStop());
        commandPanel.add(stop);
        commandPanel.add(aliveCellLabel);

        this.getContentPane().add(commandPanel,BorderLayout.PAGE_END);
        this.setSize(1000, 1000);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void updateBoard(BufferedImage boardImage) {
        board.updateDisplayedBoard(boardImage);
        notifyUpdated();
    }

    @Override
    public void updateBoard(Board boardImage) {
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

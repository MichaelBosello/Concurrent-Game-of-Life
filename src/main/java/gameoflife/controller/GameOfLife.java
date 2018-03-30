package gameoflife.controller;

import gameoflife.board.Board;

import java.util.concurrent.Semaphore;

public interface GameOfLife {

    void start();

    void stop();

    void addObserver(GameObserver observer);

    void addComputeNextSemaphoreEvent(Semaphore consumedEvent);
}

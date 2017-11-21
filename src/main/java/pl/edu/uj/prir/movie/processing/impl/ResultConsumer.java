package pl.edu.uj.prir.movie.processing.impl;

import pl.edu.uj.prir.movie.processing.ResultConsumerInterface;

import java.awt.geom.Point2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultConsumer implements ResultConsumerInterface{
    private static final Logger logger = Logger.getLogger(ResultConsumer.class.getName());
    private final Deque<Integer> frames;
    private final ReentrantReadWriteLock lock;

    public ResultConsumer() {
        frames = new LinkedList<>();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public void accept(int frameNumber, Point2D.Double position) {
        lock.writeLock().lock();
        try {
            logger.log(Level.INFO,
                    "accepting frame number: {0}, with point[x: {1}, y: {2}]",
                    new Object[]{frameNumber, position.getX(), position.getY()});
            if (frames.peekLast() != null && frames.peekLast() != frameNumber - 1) {
                throw new IllegalArgumentException("Illegal Number! " + frameNumber + " got " + frames.peekLast());
            }
            frames.add(frameNumber);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Deque<Integer> getResult() {
        return frames;
    }
}

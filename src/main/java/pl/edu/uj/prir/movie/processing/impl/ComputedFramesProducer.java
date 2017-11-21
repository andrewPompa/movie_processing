package pl.edu.uj.prir.movie.processing.impl;

import pl.edu.uj.prir.movie.processing.ResultConsumerInterface;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright: Format C
 *
 * @author michal jazowski on 19.11.17.
 */
public class ComputedFramesProducer {
    private static final Logger logger = Logger.getLogger(ComputedFramesProducer.class.getName());
    private final Map<Integer, Point2D.Double> computedFramesMap;
    private final ReentrantReadWriteLock producerLock;
    private int computedFramesCounter;
    private ResultConsumerInterface resultConsumer;

    public ComputedFramesProducer() {
        computedFramesCounter = 0;
        computedFramesMap = new HashMap<>();
        producerLock = new ReentrantReadWriteLock();
    }
    public void setResultListener(ResultConsumerInterface resultConsumer) {
        this.resultConsumer = resultConsumer;
    }

    public void addProducedFrame(final int frameNumber, final Point2D.Double result) {
        producerLock.writeLock().lock();
        try {
            computedFramesMap.put(frameNumber, result);
        } finally {
            producerLock.readLock().unlock();
        }
        notifyConsumer();
    }

    private void notifyConsumer() {
        producerLock.readLock().unlock();
        try {
            final Point2D.Double result = computedFramesMap.get(computedFramesCounter);
            if (result == null) {
                logger.log(Level.INFO,"cannot get {0} frame waiting", computedFramesCounter);
                return;
            }
            resultConsumer.accept(computedFramesCounter, result);
            computedFramesMap.remove(computedFramesCounter);
            computedFramesCounter++;
        } finally {
            producerLock.readLock().unlock();
        }
    }
}
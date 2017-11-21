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
            logger.log(Level.INFO,"got frame {0}, already have {1} elements", new Object[]{frameNumber, computedFramesMap.size()});
            computedFramesMap.put(frameNumber, result);
        } finally {
            producerLock.writeLock().unlock();
        }
        notifyConsumer();
    }

    private void notifyConsumer() {
        producerLock.writeLock().lock();
        try {
            while(!computedFramesMap.isEmpty()) {
                final Point2D.Double result = computedFramesMap.get(computedFramesCounter);
                if (result == null) {
                    logger.log(Level.INFO, "cannot get {0} frame currently waiting {1} elements", new Object[]{computedFramesCounter, computedFramesMap.size()});
                    break;
                }
                logger.log(Level.INFO, "Sending to accept {0} frame", new Object[]{computedFramesCounter});
                resultConsumer.accept(computedFramesCounter, result);
                computedFramesMap.remove(computedFramesCounter);
                computedFramesCounter++;
            }
        } finally {
            producerLock.writeLock().unlock();
        }
    }
}

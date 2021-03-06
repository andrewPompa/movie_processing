package pl.edu.uj.prir.movie.processing.impl;

import pl.edu.uj.prir.movie.processing.model.MovieFrameQueueElement;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by @author michal jazowski on 19.11.17. on 2017-11-19.
 */
public class MovieFrameConsumer {
    private static final Logger logger = Logger.getLogger(MovieFrameConsumer.class.getName());
    private final MotionDetectionSystem motionDetectionSystem;
    private BlockingQueue<MovieFrameQueueElement> movieFrameQueue;
    private final ReentrantReadWriteLock movieFrameLock;

    public MovieFrameConsumer(MotionDetectionSystem motionDetectionSystem) {
        this.motionDetectionSystem = motionDetectionSystem;
        movieFrameLock = new ReentrantReadWriteLock();
        this.movieFrameQueue = new PriorityBlockingQueue<>();
    }
    public void computeMovieFrames(MovieFrameQueueElement newElement) {
        BlockingQueue<MovieFrameQueueElement> newMovieFrameQueue = new PriorityBlockingQueue<>();
        movieFrameLock.writeLock().lock();
        try {
            movieFrameQueue.add(newElement);
            if (movieFrameQueue.size() < 2) {
                logger.info("not enough elements in queue, waiting for more than one");
                return;
            }
            while (!movieFrameQueue.isEmpty()) {
                MovieFrameQueueElement movieFrameQueueElement = movieFrameQueue.poll();
                if (movieFrameQueue.peek() == null) {
                    logger.log(Level.INFO, "{0} is last element finished computation", movieFrameQueueElement);
                    newMovieFrameQueue.add(movieFrameQueueElement);
                    break;
                }
                if (movieFrameQueueElement.shouldCompute(movieFrameQueue.peek())) {
                    orderFrameComputation(movieFrameQueueElement, movieFrameQueue.peek());
                }
                if (!movieFrameQueueElement.isComputed()) {
                    newMovieFrameQueue.add(movieFrameQueueElement);
                }
            }
            movieFrameQueue = newMovieFrameQueue;
        } finally {
            movieFrameLock.writeLock().unlock();
        }
    }

    private void orderFrameComputation(MovieFrameQueueElement movieFrameQueueElement, MovieFrameQueueElement nextFrameQueueElement) {
        movieFrameQueueElement.setAsComputed();
        nextFrameQueueElement.setComputedAsNextFrame();
        motionDetectionSystem.computeFrames(movieFrameQueueElement, nextFrameQueueElement);

    }
}

package pl.edu.uj.prir.movie.processing.impl;

import pl.edu.uj.prir.movie.processing.ImageConverterInterface;
import pl.edu.uj.prir.movie.processing.MotionDetectionSystemInterface;
import pl.edu.uj.prir.movie.processing.ResultConsumerInterface;
import pl.edu.uj.prir.movie.processing.model.MovieFrame;
import pl.edu.uj.prir.movie.processing.model.MovieFrameQueueElement;

import java.awt.geom.Point2D;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Katarzyna on 2017-11-18.
 */
public class MotionDetectionSystem implements MotionDetectionSystemInterface, Runnable {
    private static final int DEFAULT_THREAD_POOL_SIZE = 2;
    private static final Logger logger = Logger.getLogger(MotionDetectionSystem.class.getName());
    private final BlockingQueue<MovieFrame> movieFrameBuffer;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final MovieFrameConsumer movieFrameConsumer;
    private final ComputedFramesProducer computedFramesProducer;
    private ImageConverterInterface imageConverter;

    public MotionDetectionSystem() {
        threadPoolExecutor = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE,
                DEFAULT_THREAD_POOL_SIZE,
                1000L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        movieFrameBuffer = new LinkedTransferQueue<>();
        movieFrameConsumer = new MovieFrameConsumer(this);
        computedFramesProducer = new ComputedFramesProducer();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(this);
    }

    @Override
    public synchronized void run() {
        try {
            while (!Thread.interrupted()) {
                MovieFrame newMovieFrameFrame = movieFrameBuffer.take();
                logger.log(Level.INFO, "adding new frame: {0}", newMovieFrameFrame.toString());
                MovieFrameQueueElement movieFrameQueueElement = new MovieFrameQueueElement(newMovieFrameFrame);
                movieFrameConsumer.computeMovieFrames(movieFrameQueueElement);
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "thread interrupted!", e);
            Thread.currentThread().interrupt();
        }

    }

    public void computeFrames(MovieFrameQueueElement movieFrameQueueElement, MovieFrameQueueElement nextMovieFrameQueueElement) {
        threadPoolExecutor.execute(() -> {
            final Point2D.Double result = imageConverter.convert(
                    movieFrameQueueElement.getFrameNumber(),
                    movieFrameQueueElement.getFrames(),
                    nextMovieFrameQueueElement.getFrames());
            computedFramesProducer.addProducedFrame(movieFrameQueueElement.getFrameNumber(), result);
        });

    }

    @Override
    public void setThreads(int threads) {
        threadPoolExecutor.setCorePoolSize(threads);
        threadPoolExecutor.setMaximumPoolSize(threads);
    }

    @Override
    public void setImageConverter(ImageConverterInterface ici) {
        imageConverter = ici;
    }

    @Override
    public void setResultListener(ResultConsumerInterface rci) {
        computedFramesProducer.setResultListener(rci);
    }

    @Override
    public void addImage(int frameNumber, int[][] image) {
        MovieFrame movieFrame = new MovieFrame(frameNumber, image);
        movieFrameBuffer.add(movieFrame);
    }

}

package pl.edu.uj.prir.movie.processing.model;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Katarzyna on 2017-11-19.
 */
public class MovieFrameQueueElement implements Comparable<MovieFrameQueueElement> {
    private final MovieFrame movieFrame;
    private final AtomicBoolean frameComputed;
    private final AtomicBoolean frameComputedAsNext;

    public MovieFrameQueueElement(MovieFrame movieFrame) {
        this.movieFrame = movieFrame;
        frameComputed = new AtomicBoolean();
        frameComputedAsNext = new AtomicBoolean(movieFrame.getFrameNumber() == 0);
    }

    public boolean shouldCompute(MovieFrameQueueElement nextFrame) {
        return nextFrame.getFrameNumber() - movieFrame.getFrameNumber() == 1 && !frameComputed.get();
    }

    public int getFrameNumber() {
        return movieFrame.getFrameNumber();
    }

    public void setAsComputed() {
        frameComputed.set(true);
    }

    public void setComputedAsNextFrame() {
        frameComputedAsNext.set(true);
    }

    public int[][] getFrames() {
        return movieFrame.getImage();
    }

    public synchronized boolean isComputed() {
        return frameComputed.get() && frameComputedAsNext.get();
    }

    @Override
    public int compareTo(MovieFrameQueueElement movieFrameQueueElement) {
        return movieFrame.compareTo(movieFrameQueueElement.movieFrame);
    }

    @Override
    public String toString() {
        return String.format("MovieFrameQueueElement: [%s computed: %s, computedAsNext: %s]", movieFrame.toString(), frameComputed.get(), frameComputedAsNext.get());
    }
}

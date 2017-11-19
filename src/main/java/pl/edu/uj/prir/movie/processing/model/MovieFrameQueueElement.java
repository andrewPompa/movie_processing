package pl.edu.uj.prir.movie.processing.model;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Katarzyna on 2017-11-19.
 */
public class MovieFrameQueueElement {
    private final MovieFrame movieFrame;
    private final AtomicBoolean frameComputed;
    private final AtomicBoolean frameComputedAsNext;

    public MovieFrameQueueElement(MovieFrame movieFrame) {
        this.movieFrame = movieFrame;
        frameComputed = new AtomicBoolean();
        frameComputedAsNext = new AtomicBoolean(movieFrame.getFrameNumber() != 0);
    }

    public boolean shouldCompute(MovieFrameQueueElement nextFrame) {
        return nextFrame.getFrameNumber() - movieFrame.getFrameNumber() == 1;
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
}

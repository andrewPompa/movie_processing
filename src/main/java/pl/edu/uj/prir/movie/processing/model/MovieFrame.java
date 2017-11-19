package pl.edu.uj.prir.movie.processing.model;

import com.sun.javafx.binding.StringFormatter;

/**
 * Created by Katarzyna on 2017-11-19.
 */
public class MovieFrame implements Comparable<MovieFrame> {
    private final int frameNumber;
    private final int[][] image;

    public MovieFrame(int frameNumber, int[][] image) {
        this.frameNumber = frameNumber;
        this.image = image;
    }

    @Override
    public int compareTo(MovieFrame o) {
        return this.getFrameNumber() - o.getFrameNumber();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MovieFrame)) {
            throw new IllegalArgumentException("Cannot compare object other than MovieFrame");
        }
        return this.getFrameNumber() == ((MovieFrame) obj).getFrameNumber();
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int[][] getImage() {
        return image;
    }

    @Override
    public String toString() {
        return String.format("MoveFrame[id: %d]", frameNumber);
    }
}

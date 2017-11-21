package pl.edu.uj.prir.movie.processing.impl;

import pl.edu.uj.prir.movie.processing.ImageConverterInterface;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageConverter implements ImageConverterInterface {
    private static final Logger logger = Logger.getLogger(ImageConverter.class.getName());
    @Override
    public Point2D.Double convert(int frameNumber, int[][] firstImage, int[][] secondImage) {
        logger.log(Level.ALL, "converting {0} frame", frameNumber);
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Thread is interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return new Point2D.Double(0.0, 1.1);
    }
}

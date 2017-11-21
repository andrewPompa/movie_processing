package pl.edu.uj.prir.movie.processing.impl;

import pl.edu.uj.prir.movie.processing.ResultConsumerInterface;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultConsumer implements ResultConsumerInterface{
    private static final Logger logger = Logger.getLogger(ResultConsumer.class.getName());

    @Override
    public void accept(int frameNumber, Point2D.Double position) {
        logger.log(Level.ALL,
                "accepting frame number: {0}, with point[x: {1}, y: {2}]",
                new Object[]{frameNumber, position.getX(), position.getY()});
    }
}

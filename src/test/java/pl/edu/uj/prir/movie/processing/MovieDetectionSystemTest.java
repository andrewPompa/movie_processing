package pl.edu.uj.prir.movie.processing;

import org.testng.annotations.Test;
import pl.edu.uj.prir.movie.processing.impl.ImageConverter;
import pl.edu.uj.prir.movie.processing.impl.MotionDetectionSystem;
import pl.edu.uj.prir.movie.processing.impl.ResultConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.testng.Assert.*;

/**
 * Created by Katarzyna on 2017-11-18.
 */
public class MovieDetectionSystemTest {
    @Test
    public void threadsTest() throws InterruptedException {
        MotionDetectionSystemInterface motionDetectionSystem = new MotionDetectionSystem();
        ImageConverter imageConverter = new ImageConverter();
        ResultConsumer resultConsumer = new ResultConsumer();
        motionDetectionSystem.setImageConverter(imageConverter);
        motionDetectionSystem.setResultListener(resultConsumer);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AtomicInteger imageFrameCounter = new AtomicInteger();
        final int[][] image = new int[100][100];
        executorService.execute(() -> {
            motionDetectionSystem.addImage(imageFrameCounter.incrementAndGet(), image);
        });
        sleep(1100L);
    }

}
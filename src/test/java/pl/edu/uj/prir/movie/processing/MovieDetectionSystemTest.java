package pl.edu.uj.prir.movie.processing;

import com.jayway.awaitility.Awaitility;
import org.assertj.core.api.Assertions;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import pl.edu.uj.prir.movie.processing.impl.ImageConverter;
import pl.edu.uj.prir.movie.processing.impl.MotionDetectionSystem;
import pl.edu.uj.prir.movie.processing.impl.ResultConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jayway.awaitility.Awaitility.*;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

/**
 * Created by @author michal jazowski on 19.11.17. on 2017-11-18.
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
        executorService.execute(() -> motionDetectionSystem.addImage(imageFrameCounter.getAndIncrement(), image));
        executorService.execute(() -> motionDetectionSystem.addImage(imageFrameCounter.getAndIncrement(), image));
        await().atMost(1L, TimeUnit.SECONDS).until(() -> {
            assertThat(resultConsumer.getResult())
                    .hasSize(1)
                    .containsExactlyInAnyOrder(0);
        });

    }

    @Test
    public void framesPlacementTest() throws InterruptedException {
        MotionDetectionSystemInterface motionDetectionSystem = new MotionDetectionSystem();
        ImageConverter imageConverter = new ImageConverter();
        ResultConsumer resultConsumer = new ResultConsumer();
        motionDetectionSystem.setImageConverter(imageConverter);
        motionDetectionSystem.setResultListener(resultConsumer);

        final int[][] image = new int[100][100];
        motionDetectionSystem.addImage(1, image);
        motionDetectionSystem.addImage(2, image);
        motionDetectionSystem.addImage(3, image);
        motionDetectionSystem.addImage(0, image);
        await().atMost(5L, TimeUnit.SECONDS).until(() -> {
            assertThat(resultConsumer.getResult())
                    .hasSize(3)
                    .containsExactlyInAnyOrder(0, 1, 2);
        });

    }

    @Test(invocationCount = 10)
    public void fullTest() throws InterruptedException {
        MotionDetectionSystemInterface motionDetectionSystem = new MotionDetectionSystem();
        ImageConverter imageConverter = new ImageConverter();
        ResultConsumer resultConsumer = new ResultConsumer();
        motionDetectionSystem.setImageConverter(imageConverter);
        motionDetectionSystem.setResultListener(resultConsumer);

        final int[][] image = new int[100][100];
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        IntStream.range(0, 5).forEach(i -> {
            executorService.execute(() -> motionDetectionSystem.addImage(i, image));
        });
        IntStream.range(5, 10).forEach(i -> {
            executorService.execute(() -> motionDetectionSystem.addImage(i, image));
        });
        await().atMost(25L, TimeUnit.SECONDS).until(() -> {
            assertThat(resultConsumer.getResult())
                    .hasSize(9)
                    .containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5, 6, 7, 8);
        });

    }

    @Test
    public void fullTest2() throws InterruptedException {
        MotionDetectionSystemInterface motionDetectionSystem = new MotionDetectionSystem();
        ImageConverter imageConverter = new ImageConverter();
        ResultConsumer resultConsumer = new ResultConsumer();
        motionDetectionSystem.setImageConverter(imageConverter);
        motionDetectionSystem.setResultListener(resultConsumer);

        final List<Integer> results = new ArrayList<>();
        IntStream.rangeClosed(0, 98).forEach(results::add);

        final int[][] image = new int[100][100];
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        IntStream.range(0, 100).parallel().forEach(i ->
                executorService.execute(() -> motionDetectionSystem.addImage(i, image))
        );
        await().atMost(60L, TimeUnit.SECONDS).until(() -> {
            assertThat(resultConsumer.getResult())
                    .hasSize(99).containsExactlyElementsOf(results);
        });

    }
}

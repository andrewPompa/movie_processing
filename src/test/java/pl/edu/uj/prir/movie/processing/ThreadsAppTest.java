package pl.edu.uj.prir.movie.processing;

import org.testng.annotations.Test;
import pl.edu.uj.prir.movie.processing.impl.MotionDetectionSystem;

import static java.lang.Thread.sleep;
import static org.testng.Assert.*;

/**
 * Created by Katarzyna on 2017-11-18.
 */
public class ThreadsAppTest {
    @Test
    public void threadsTest() throws InterruptedException {
        MotionDetectionSystemInterface motionDetectionSystem = new MotionDetectionSystem();
        sleep(1100L);
    }

}
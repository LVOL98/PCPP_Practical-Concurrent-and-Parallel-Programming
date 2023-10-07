/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package exercises01;

import org.junit.Test;

import exercise63.Histogram;
import exercise63.Histogram2;

import static org.junit.Assert.*;

import java.util.concurrent.ForkJoinPool;

public class Histogram2Test {
    private Histogram histogram;
    private final int ITERATIONS = 10;
    private final int THREAD_COUNT = 20;

    @Test
    public void test() {
        histogram = new Histogram2(30);

        var pool = new ForkJoinPool();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            pool.submit(() -> {
                histogram.increment(0);
            });
        }
    }
}

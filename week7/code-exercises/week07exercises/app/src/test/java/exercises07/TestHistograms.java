package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports
    private final int BIN_COUNT = 25;
    private final int PRIME_COUNT_RANGE = 4_999_999;
    private final int THREAD_COUNT_BASE = 2;
    private ExecutorService pool;

    // TODO: 10.1.2
    @Test
    public void Test_10_1_2() {
        int threadCount;
        var histogram1 = new Histogram1(BIN_COUNT);

        for (int i = 0; i < PRIME_COUNT_RANGE; i++) {
            histogram1.increment(TestHitsogramHelper.countFactors(i));
        }
        
        for (int n = 0; n < 5; n++) {
            var ranges = TestHitsogramHelper.getRanges(PRIME_COUNT_RANGE, n == 0 ? 1 : n);
            var casHistogram = new CasHistogram(BIN_COUNT);
            threadCount = (int) Math.pow(THREAD_COUNT_BASE, n);
            pool = new ForkJoinPool(threadCount);
            var tasks = new ArrayList<CountPrimeFactor>();

            for (int i = 0; i < ranges.length; i++) {
                var range = ranges[i];
                tasks.add(new CountPrimeFactor(range.from, range.to, casHistogram));
            }

            try {
                pool.invokeAll(tasks);
            } catch (InterruptedException e) {
                fail("Parallel execution was interropted", e);
            }

            pool.shutdown();

            for (int i = 0; i < BIN_COUNT; i++) {
                assertEquals(histogram1.getCount(i), casHistogram.getCount(i), "CasHistogram is not equal to sequential histogram");
            }
        }
    }
}

final class TestHitsogramHelper {
    public static int countFactors(int p) {
        if (p < 2)
            return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p = p / k;
            } else
                k = k + 1;
        }
        return factorCount;
    }

    public static Range[] getRanges(int range, int splitCount) {
        var ranges = new Range[splitCount];
        var splitRange = range / splitCount;

        for (int i = 0; i < splitCount; i++) {
            ranges[i] = new Range(
                splitRange * i,
                (i + 1 == splitCount)
                ? range
                : splitRange * (i + 1)
            );
        }

        return ranges;
    }
}

class Range {
    public int from;
    public int to;

    public Range(int from, int to) {
        this.from = from;
        this.to = to;
    }
}

class CountPrimeFactor implements Callable<Integer> {
    private Histogram histogram;
    private int start;
    private int end;

    public CountPrimeFactor(int start, int end, Histogram histogram) {
        this.start = start;
        this.end = end;
        this.histogram = histogram;
    }

    @Override
    public Integer call() throws Exception {
        for (int i = start; i < end; i++) {
            histogram.increment(TestHitsogramHelper.countFactors(i));
        }

        return 1;
    }

}

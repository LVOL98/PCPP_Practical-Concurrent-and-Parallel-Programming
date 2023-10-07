package exercise63;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import benchmarking.Benchmark;

// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022
// jst@itu.dk 22-09-2023

public class HistogramPrimesThreads {
  private static final int PRIME_TO_COUNT = 4_999_999;

  public static void main(String[] args) {
    new HistogramPrimesThreads();
  }

  public HistogramPrimesThreads() {
    // TODO: Replace below with an instance of Histogram2 exercise 6.3.1 (recall
    // that Histogram1 is not thread-safe)
    // final Histogram histogram = new Histogram2(25); // 25 bins sufficient for a
    // range of 0..4_999_999

    // countPrimeThreadsHistogram2(5);
    // countPrimesHistogram3(1, 1, true);

    var threadCount = 12;
    for (int k = 1; k < 17; k++) {
      var lockCount = k;
      Benchmark.Mark7(String.format("Mark7 count primes using Histogram3 with %d threads and %d locks", threadCount, lockCount), i -> {
        countPrimesHistogram3(threadCount, lockCount, false);
  
        return 0.0;
      });
    }
  }

  private void countPrimeThreadsHistogram2(int threadCount) {
    var histogram = new Histogram2(25);
    var fromTo = splitIntegerEqually(PRIME_TO_COUNT, threadCount);
    var pool = new ForkJoinPool();

    for (int i = 0; i < threadCount; i++) {
      var start = fromTo.get(i).get(0);
      var end = fromTo.get(i).get(1);
      var future = pool.submit(() -> {
        for (int k = start; k < end; k++) {
          histogram.increment(countFactors(k));
        }
      });

      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    pool.shutdown();

    dump(histogram);
  }

  private static void countPrimesHistogram3(int threadCount, int binLockCount, boolean shouldDumpHistogram) {
    final Histogram histogram = new Histogram3(25, binLockCount); // 25 bins sufficient for a range of 0..4_999_999

    var fromTo = splitIntegerEqually(PRIME_TO_COUNT, threadCount);
    var pool = new ForkJoinPool();
    var tasks = new ArrayList<FindPrimeTask>();

    for (int i = 0; i < threadCount; i++) {
      var start = fromTo.get(i).get(0);
      var end = fromTo.get(i).get(1);
      tasks.add(new FindPrimeTask(histogram, start, end));
    }
    var futures = pool.invokeAll(tasks);

    try {
      for (var future : futures) {
        future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    pool.shutdown();

    if (shouldDumpHistogram) {
      dump(histogram);
    }
  }

  private static LinkedList<LinkedList<Integer>> splitIntegerEqually(int count, int split) {
    var splitCount = count / split;
    var remainder = count % split;
    var fromTo = new LinkedList<LinkedList<Integer>>();
    var tempFrom = 0;

    for (int i = 0; i < split; i++) {
      var innerList = new LinkedList<Integer>();
      innerList.add(tempFrom);

      var to = tempFrom + splitCount;
      if (i == split - 1) {
        to += remainder;
      }
      tempFrom = to;
      innerList.add(to);

      fromTo.add(innerList);
    }

    return fromTo;
  }

  // Returns the number of prime factors of `p`
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

  public static void dump(Histogram histogram) {
    for (int bin = 0; bin < histogram.getSpan(); bin = bin + 1) {
      System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
    }
  }
}

class FindPrimeTask implements Callable<Integer> {
  private Histogram histogram;
  private int start;
  private int end;

  public FindPrimeTask(Histogram histogram, int start, int end) {
    // TODO: not thread safe leaked
    this.histogram = histogram;
    this.start = start;
    this.end = end;
  }

  @Override
  public Integer call() throws Exception {
    for (int k = start; k < end; k++) {
      histogram.increment(countFactors(k));
    }

    return -1;
  }

  private int countFactors(int p) {
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
}

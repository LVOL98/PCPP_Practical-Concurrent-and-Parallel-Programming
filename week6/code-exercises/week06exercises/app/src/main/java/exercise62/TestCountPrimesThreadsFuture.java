package exercise62;

// Counting primes, using multiple threads for better performance.
// (Much simplified from CountprimesMany.java)
// sestoft@itu.dk * 2014-08-31, 2015-09-15
// modified rikj@itu.dk 2017-09-20
// modified jst@itu.dk 2021-09-24
// raup@itu.dk * 05/10/2022
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicLong;
import benchmarking.Benchmark;

public class TestCountPrimesThreadsFuture {
	public static void main(String[] args) {
		new TestCountPrimesThreadsFuture();
	}

	public TestCountPrimesThreadsFuture() {
		final int range = 100_000;
		Benchmark.Mark7("countSequential", i -> countSequential(range));
		for (int c = 1; c <= 16; c++) {
			final int threadCount = c;
			Benchmark.Mark7(String.format("countParallelN %2d", threadCount),
					i -> countParallelN(range, threadCount));
			Benchmark.Mark7(String.format("countParallelNLocal %2d", threadCount),
					i -> countParallelNLocal(range, threadCount));
		}
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
			k++;
		return n >= 2 && k * k > n;
	}

	// Sequential solution
	private static long countSequential(int range) {
		long count = 0;
		final int from = 0, to = range;
		for (int i = from; i < to; i++)
			if (isPrime(i))
				count++;
		return count;
	}

	// General parallel solution, using multiple threads
	private static long countParallelN(int range, int threadCount) {
		final var pool = new ForkJoinPool();
		final int perThread = range / threadCount;
		final AtomicLong lc = new AtomicLong(0);

		var futures = new ForkJoinTask[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			futures[t] = pool.submit(() -> {
				for (int i = from; i < to; i++)
					if (isPrime(i))
						lc.incrementAndGet();
			});
		}

		for (ForkJoinTask<?> forkJoinTask : futures) {
			try {
				forkJoinTask.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		pool.shutdown();

		return lc.get();
	}

	// General parallel solution, using multiple threads
	private static long countParallelNLocal(int range, int threadCount) {
		final var pool = new ForkJoinPool();
		final int perThread = range / threadCount;
		final long[] results = new long[threadCount];

		var futures = new ForkJoinTask[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			final int threadNo = t;

			futures[t] = pool.submit(() -> {
				long count = 0;
				for (int i = from; i < to; i++)
					if (isPrime(i))
						count++;
				results[threadNo] = count;
			});
		}

		for (var forkJoinTask : futures) {
			try {
				forkJoinTask.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		pool.shutdown();

		long result = 0;
		for (int t = 0; t < threadCount; t++)
			result += results[t];
		return result;
	}
}

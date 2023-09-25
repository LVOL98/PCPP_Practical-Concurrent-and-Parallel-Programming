package lecture05;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.IntToDoubleFunction;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class countPrimesExecutorBenchmark {
  public static void main(String[] args) { 
    new countPrimesExecutorBenchmark();
  }

  public countPrimesExecutorBenchmark() {
    Benchmark.SystemInfo();
    final ExecutorService pool= new ForkJoinPool(4);


    final int range= 1_000_000;
    final int threshold= 10000;

    Benchmark.Mark7(String.format("countExecutor ", 0), 
          i -> countPrimes(range, pool, threshold));

    pool.shutdown();
  }

  private int countPrimes(int range, 
                           ExecutorService pool, int threshold) {
    final PrimeCounter lc= new PrimeCounter();
    Future<?> done= pool.submit(new countPrimesTask(lc, 2, range, pool, threshold));
    try {  done.get();  }
    catch (InterruptedException | ExecutionException e) { e.printStackTrace(); } 
    return lc.get();
  }
}

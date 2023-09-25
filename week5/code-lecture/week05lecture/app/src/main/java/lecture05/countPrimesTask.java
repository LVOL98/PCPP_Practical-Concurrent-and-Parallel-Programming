package lecture05;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class countPrimesTask implements Runnable {
  private final int low;
  private final int high;
  private final int threshold;
  private final ExecutorService pool;
  private final PrimeCounter lc;

  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }

  public countPrimesTask(PrimeCounter lc, int low, int high, ExecutorService pool, int threshold) {
    this.lc        = lc;
    this.low       = low;
    this.high      = high;
    this.pool      = pool;
    this.threshold = threshold;
  }
    
  @Override
  public void run() {

    //System.out.println("countprimes "+low+", "+high);
    if ((high-low) < threshold) { 
      for (int i=low; i<=high; i++)
        if (isPrime(i)) lc.increment();
        //System.out.println("countprimes "+low+", "+high+ " result: "+lc.get());
    } else {
      int mid= low+(high-low)/2;
      // Submit two new counting tasks
      Future<?> f1= pool.submit( new countPrimesTask(lc, low, mid, pool, threshold) );
      Future<?> f2= pool.submit( new countPrimesTask(lc, mid+1, high, pool, threshold) );
   
      try {
          f1.get();f2.get();
      }
      catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
      }
    }
  }
}

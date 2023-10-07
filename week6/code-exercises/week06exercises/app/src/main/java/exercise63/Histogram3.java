package exercise63;
// raup@itu.dk * 05/10/2022
// jst@itu.dk * 23/9/2023

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Histogram3 implements Histogram {
  // bin % count of locks
  // private final static Lock lock = new ReentrantLock();
  private Lock[] locks;
  private int lockCount;
  private int[] counts;

  public Histogram3(int span, int nrLocks) {
    this.counts = new int[span];
    this.locks = new Lock[nrLocks];
    this.lockCount = nrLocks;

    for (int i = 0; i < nrLocks; i++) {
      this.locks[i] = new ReentrantLock();
    }
  }

  public void increment(int bin) {
    var lock = locks[bin % lockCount];

    try {
      lock.lock();

      counts[bin] = counts[bin] + 1;
    } finally {
      lock.unlock();
    }
  }

  public synchronized int getCount(int bin) {
    var lock = locks[bin % lockCount];
    var binCount = 0;

    try {
      lock.lock();

      binCount = counts[bin];
    } finally {
      lock.unlock();
    }

    return binCount;
  }
  
  public int getSpan() {
    return counts.length;
  }
}

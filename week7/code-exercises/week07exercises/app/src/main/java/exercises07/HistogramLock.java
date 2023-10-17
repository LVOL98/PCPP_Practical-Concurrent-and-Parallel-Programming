package exercises07;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HistogramLock implements Histogram {
    private Lock[] locks;
    private int lockCount;
    private int[] counts;

  public HistogramLock(int span, int nrLocks) {
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

    /**
     * Returns the count of the given bin and clears it.
     * Uses a lock to ensure thread-safety.
     *
     * @param bin the bin to get and clear
     * @return the count of the given bin before clearing it, -1 if an exception occurred
     */
    @Override
    public int getAndClear(int bin) {
        int count = -1;
        var lock = locks[bin % lockCount];
        
        try {
            lock.lock();

            count = counts[bin];

            counts[bin] = 0;
        } finally {
            lock.unlock();
        }

        return count;
    }
}

package exercises05;

import java.util.concurrent.atomic.AtomicLong;

class LongCounter {
  private final AtomicLong count = new AtomicLong();
  // private volatile long count = 0;

  public synchronized void increment() {
    // count++;
    count.incrementAndGet();
  }

  public synchronized long get() {
    // return count;
    return count.get();
  }

  public synchronized void add(long c) {
    // count += c;
    count.addAndGet(c);
  }

  public synchronized void reset() {
    // count = 0;
    count.set(0);
  }
}
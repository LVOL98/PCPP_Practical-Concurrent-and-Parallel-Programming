package exercises05;

class LongCounter {
  private volatile long count = 0;

  public synchronized void increment() {
    count++;
  }

  public synchronized long get() {
    return count;
  }

  public synchronized void add(long c) {
    count += c;
  }

  public synchronized void reset() {
    count = 0;
  }
}
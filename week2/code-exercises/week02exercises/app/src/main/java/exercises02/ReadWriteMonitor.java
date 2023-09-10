package exercises02;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteMonitor {
  private final Lock lock = new ReentrantLock(true);
  private final Condition condition = this.lock.newCondition();
  private boolean isWriting = false;
  private int readers = 0;
  private int writerQueue = 0;

  public void readLock() {
    // Method copied from lecture example
    this.lock.lock();
    try {
      while (this.isWriting || this.writerQueue > 0) {
        if (this.writerQueue > 0) {
          System.out.println(Thread.currentThread().getId() + " - waiting for writers queue: " + this.writerQueue);
        }
        this.condition.await();
      }
      this.readers++;
    } catch (InterruptedException e) {
    } finally {
      lock.unlock();
    }
  }

  public void readUnlock() {
    // Method copied from lecture example
    this.lock.lock();
    try {
      this.readers--;
      if (this.readers == 0)
        this.condition.signalAll();
    } finally {
      this.lock.unlock();
    }
  }

  public void writeLock() {
    // Method copied from lecture example
    this.lock.lock();
    try {
      // Add writer to the queue
      this.writerQueue++;
      while (this.readers > 0 || this.isWriting) {
        this.condition.await();
      }
      // As the writer successfully obtained lock the writer is removed from the queue
      this.isWriting = true;
      this.writerQueue--;
    } catch (InterruptedException e) {
    } finally {
      this.lock.unlock();
    }
  }

  public void writeUnlock() {
    // Method copied from lecture example
    this.lock.lock();
    try {
      this.isWriting = false;
      this.condition.signalAll();
    } finally {
      this.lock.unlock();
    }
  }

  public static void main(String[] args) {
    // Method copied from lecture example
    ReadWriteMonitor m = new ReadWriteMonitor();
    for (int i = 0; i < 10; i++) {
      // start a reader
      new Thread(() -> {
        m.readLock();
        System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
        // read
        System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");
        m.readUnlock();
      }).start();
      // start a writer
      new Thread(() -> {
        m.writeLock();
        System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
        // write
        System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
        m.writeUnlock();
      }).start();
    }
  }
}
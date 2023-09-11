package exercises02;

public class ReadWriteMonitor {
  private boolean isWriting = false;
  private int readers = 0;
  private int writerQueue = 0;

  public void readLock() {
    synchronized (this) {
      try {
        while (this.isWriting || this.writerQueue > 0) {
          if (this.writerQueue > 0) {
            System.out.println(
                " - Reader " + Thread.currentThread().getId() + " waiting for writers queue: " + this.writerQueue);
          }
          this.wait();
        }
        this.readers++;
      } catch (InterruptedException e) {
      } finally {
      }
    }
  }

  public void readUnlock() {
    synchronized (this) {
      try {
        this.readers--;
        if (this.readers == 0)
          this.notifyAll();
      } finally {
      }
    }
  }

  public void writeLock() {
    // Method copied from lecture example
    synchronized (this) {
      try {
        // Add writer to the queue
        this.writerQueue++;
        System.out.println(" - Writer " + Thread.currentThread().getId() + " queued as number " + this.writerQueue);
        while (this.readers > 0 || this.isWriting) {
          this.wait();
        }
        // As the writer successfully obtained lock the writer is removed from the queue
        this.isWriting = true;
        this.writerQueue--;
      } catch (InterruptedException e) {
      } finally {
      }
    }
  }

  public void writeUnlock() {
    // Method copied from lecture example
    synchronized (this) {
      try {
        this.isWriting = false;
        this.notifyAll();
      } finally {
      }
    }
  }

  public static void main(String[] args) {
    // Method copied from lecture example
    ReadWriteMonitor m = new ReadWriteMonitor();
    for (int i = 0; i < 100; i++) {
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
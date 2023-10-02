package exercises02;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FifoReadWriteMonitor {
    private int readersAcquireCount = 0;
    private int readersReleaseCount = 0;
    private boolean writer = false;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void readLock() {
        lock.lock();

        try {
            while (writer) {
                condition.await();
            }

            readersAcquireCount++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void readUnlock() {
        lock.lock();

        try {
            readersReleaseCount++;

            if (readersAcquireCount == readersReleaseCount) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void writeLock() {
        lock.lock();

        try {
            while (writer) {
                condition.await();
            }
            
            writer = true;

            while (readersAcquireCount != readersReleaseCount) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void writeUnlock() {
        lock.lock();

        try {
            writer = false;

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

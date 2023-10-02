package exercises03;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBufferTest {
    public static void main(String[] args) {
        final int bufferSize = 20;
        final int insertCount = 1_000_0;

        var boundedBuffer = new BoundedBuffer<Integer>(bufferSize);

        Thread t1 = new Thread(() -> {
            for (int i = 1; i < insertCount + 1; i++) {
                try {
                    boundedBuffer.insert(i);
                    System.out.println(String.format("Element %d inserted", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            int result = 0;

            for (int i = 1; i < insertCount + 1; i++) {
                try {
                    boundedBuffer.take();
                    result += 1;
                    System.out.println(String.format("Element %d taken", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("Retreived %d amount of items from the buffer, expected was %d\n", result, insertCount);
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
        }

        System.out.printf("Remaining elements in bounded buffer: %d\n", boundedBuffer.getRemainingElements());
    }
}

class BoundedBuffer<T> implements BoundedBufferInteface {
    private LinkedList<T> buffer = new LinkedList<T>();
    private final Semaphore insertSemaphore, takeSemaphore;
    private final Lock takeLock, insertLock;


    public BoundedBuffer(int bufferSize) {
        insertSemaphore = new Semaphore(1);
        takeSemaphore = new Semaphore(0);
        insertLock = new ReentrantLock();
        takeLock = new ReentrantLock();
    }

    @Override
    public Object take() throws Exception {
        takeSemaphore.acquire();
        Object result = null;

        takeLock.lock();

        try {
            result = buffer.pop();
        } finally {
            takeLock.unlock();
        }

        insertSemaphore.release();

        return result;
    }

    @Override
    public void insert(Object elem) throws Exception {
        insertSemaphore.acquire();
        
        insertLock.lock();

        try {
            buffer.push((T) elem);
        } finally {
            insertLock.unlock();
        }

        takeSemaphore.release();
    }

    public int getRemainingElements() {
        return buffer.size();
    }
}

// class BoundedBuffer<T> implements BoundedBufferInteface {
// private LinkedList<T> buffer = new LinkedList<T>();
// private int bufferMaxSize;

// public BoundedBuffer(int bufferSize) {
// bufferMaxSize = bufferSize;
// }

// @Override
// public Object take() throws Exception {
// synchronized (this) {
// Object result = null;

// try {
// if (buffer.size() == 0) {
// this.wait();
// }

// result = buffer.pop();

// this.notifyAll();
// } finally {

// }

// return result;
// }
// }

// @Override
// public void insert(Object elem) throws Exception {
// synchronized (this) {
// try {
// if (buffer.size() == bufferMaxSize) {
// this.wait();
// }

// buffer.push((T) elem);

// this.notifyAll();
// } finally {

// }
// }
// }
// }

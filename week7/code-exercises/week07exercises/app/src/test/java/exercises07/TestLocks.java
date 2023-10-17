package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports
    private ReadWriteCASLock lock;
    private ExecutorService pool;
    private TestCounter counter;
    private final int THREAD_COUNT = 12;
    private final int TIMES_TO_INCREMENT = 500000;

    
    @BeforeEach
    public void initialize() {
        lock = new ReadWriteCASLock();
        pool = new ForkJoinPool(THREAD_COUNT);
        counter = new TestCounter();
    }

    @Test
    public void writerTryLock_SingleLock_Sequential() {
        assertEquals(true, lock.writerTryLock(), "Acquiring no held locks should return true");
        lock.writerUnlock();
    }
    
    @Test
    public void writerUnlock_Only_Unlock_Exception_Sequential() {
        assertThrows(IllegalStateException.class, () -> lock.writerUnlock());
    }
    
    @Test
    public void writerTryLock_2_Locks_Exception_Sequential() {
        assertEquals(true, lock.writerTryLock(), "Acquiring no hold locks should return true");
        assertEquals(false, lock.writerTryLock(), "Acquiring an already held lock should return false");
    }
    
    @Test
    public void readerTryLock_Sequential() {
        assertTrue(lock.readerTryLock()); 
        lock.readerUnlock(); 
    }
    
    @Test
    public void readerUnlock_Exception_Sequential() {
        assertThrowsExactly(IllegalStateException.class, () -> lock.readerUnlock());
    }

    @Test
    public void readerTryLock_Several_Sequential() {
        assertTrue(lock.readerTryLock()); 
        assertTrue(lock.readerTryLock()); 
        lock.readerUnlock(); 
        assertThrows(Exception.class, () -> lock.readerUnlock()); 
    }
    
    // TODO: 10.2.5

    @Test 
    public void writerTryLock_readerTryLock_False_Sequential() {
        assertTrue(lock.writerTryLock());
        assertFalse(lock.readerTryLock());
    }
    
    @Test 
    public void readerTryLock_writerTryLock_False_Sequential() {
        assertTrue(lock.readerTryLock());
        assertFalse(lock.writerTryLock());
    }

    @Test
    public void writerTryLock_readerUnlock_Exception_Sequential() {
        assertEquals(true, lock.writerTryLock(), "Acquiring no held locks should return true");
        assertThrows(IllegalStateException.class, () -> lock.readerUnlock());
    }
    
    @Test
    public void readerTryLock_writerUnlock_Exception_Sequential() {
        assertEquals(true, lock.readerTryLock());
        assertThrows(IllegalStateException.class, () -> lock.writerUnlock());
    }

    public void writerUnlock_Another_Threads_Lock() {
        var thread = new Thread(() -> {
            lock.writerTryLock();
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            fail(e);
        }

        assertThrows(IllegalStateException.class, () -> lock.writerUnlock());
    }

    public void readerUnlock_Another_Threads_Lock() {
        var thread = new Thread(() -> {
            lock.readerTryLock();
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            fail(e);
        }

        assertThrows(IllegalStateException.class, () -> lock.readerUnlock());
    }
    
    // TODO: 10.2.6   
    @Test
    @RepeatedTest(10)
    public void writerLock_Parallel() {
        var tasks = new ArrayList<IncrementTask>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            tasks.add(new IncrementTask(counter, TIMES_TO_INCREMENT));
        }
        
        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(THREAD_COUNT * TIMES_TO_INCREMENT, counter.get());
    }
}

class IncrementTask implements Callable<Integer> {
    private TestCounter counter;
    private int timesToIncrement;

    public IncrementTask(TestCounter counter, int timesToIncrement) {
        this.counter = counter;
        this.timesToIncrement = timesToIncrement;
    }

    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < timesToIncrement; i++) {
            counter.increment();
        }

        return 0;
    }
}

class TestCounter {
    private int count;
    private ReadWriteCASLock lock;

    public TestCounter() {
        count = 0;
        lock = new ReadWriteCASLock();
    }

    public void increment() {
        
        try {
            // TODO: don't do spinning locks
            while (!lock.writerTryLock());

            count++;
        } finally {
            lock.writerUnlock();
        }
    }

    public int get() {
        int result;

        try {
            // TODO: don't do spinning locks
            while (!lock.readerTryLock());

            result = count;
        } finally {
            lock.readerUnlock();
        }

        return result;
    }
}


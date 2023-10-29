// raup@itu.dk * 2023-10-20 
package exercises08;

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


class TestLockFreeStack {
    private LockFreeStack stack;
    private ExecutorService pool;
    private final int THREAD_COUNT = 10;
    private final int COUNT = 100000;
    
    @BeforeEach
    public void Initialize() {
        stack = new LockFreeStack<Integer>();
        pool = new ForkJoinPool();
    }

    // TODO: 8.2.2 - Test push
    @Test
    @RepeatedTest(10)
    public void Test_8_2_2() {
        var tasks = new ArrayList<PushTask>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            tasks.add(new PushTask(stack, COUNT));
        }

        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            fail(e);
        }

        for (int i = 0; i < COUNT * THREAD_COUNT; i++) {
            stack.pop();
        }

        assertEquals(null, stack.pop());
    }

    // TODO: 8.2.3 - Test pop 
    @Test
    @RepeatedTest(10)
    public void Test_8_2_3() {
        var tasks = new ArrayList<PopTask>();

        for (int i = 0; i < COUNT * THREAD_COUNT; i++) {
            stack.push(i);
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            if (i == THREAD_COUNT - 1) {
                tasks.add(new PopTask(stack, COUNT - 1));
            } else {
                tasks.add(new PopTask(stack, COUNT));
            }
        }

        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            fail(e);
        }

        assertNotEquals(null, stack.pop());
        assertEquals(null, stack.pop());
    }
}

class PushTask implements Callable<Integer> {
    private LockFreeStack stack;
    private int count;

    public PushTask(LockFreeStack stack, int count) {
        this.stack = stack;
        this.count = count;
    }

    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < count; i++) {
            stack.push(1);
        }

        return 1;
    }

}

class PopTask implements Callable<Integer> {
    private LockFreeStack stack;
    private int count;

    public PopTask(LockFreeStack stack, int count) {
        this.stack = stack;
        this.count = count;
    }

    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < count; i++) {
            stack.pop();
        }

        return 1;
    }

}

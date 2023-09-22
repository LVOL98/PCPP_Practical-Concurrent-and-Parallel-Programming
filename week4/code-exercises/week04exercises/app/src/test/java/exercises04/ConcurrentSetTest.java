package exercises04;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class ConcurrentSetTest {
  private ConcurrentIntegerSet set;
  private CyclicBarrier barrier;

  // Uncomment the appropriate line below to choose the class to
  // test
  // Remember that @BeforeEach is executed before each test
  @BeforeEach
  public void initialize() {
    // init set
    // set = new ConcurrentIntegerSetBuggy();
    set = new ConcurrentIntegerSetSync();
    // set = new ConcurrentIntegerSetLibrary();
  }

  // @RepeatedTest(5000)
  @Test
  public void exercise_4_1_1() {
    int amountOfThreads = 20;
    int iterations = 1000;

    barrier = new CyclicBarrier(amountOfThreads + 1, null);

    for (int i = 0; i < amountOfThreads; i++) {
      new Thread(() -> {
        try {
          barrier.await();
          for (int k = 0; k < iterations; k++) {
            set.add(k);
          }
          barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
          e.printStackTrace();
        }
      }).start();
    }
    
    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }

    assertEquals(1000, set.size(), "Expected set of size 1, set had a size different from 1");
  }

  // @RepeatedTest(5000)
  @Test
  public void exercise_4_1_2() {
    int amountOfThreads = 20;
    int iterations = 1000;
    
    for (var i = 0; i < iterations * amountOfThreads; i++) {
      set.add(i);
    }

    barrier = new CyclicBarrier(amountOfThreads + 1, null);

    for (int i = 0; i < amountOfThreads; i++) {
      new Thread(() -> {
        try {
          barrier.await();
          for (int k = 0; k < iterations * amountOfThreads; k++) {
            set.remove(k);
          }
          barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
          e.printStackTrace();
        }
      }).start();
    }

    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }

    assertEquals(0, set.size(), "Expected set of size 1, set had a size different from 1");
  }
}

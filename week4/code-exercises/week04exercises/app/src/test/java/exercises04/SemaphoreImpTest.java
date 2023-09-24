package exercises04;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class SemaphoreImpTest {
  private SemaphoreImp semaphoreImp;
  private CyclicBarrier barrier;

  @BeforeEach
  public void initialize() {
    semaphoreImp = new SemaphoreImp(5);
  }

  @Test
  public void exercise_4_2_2() {
    var amountOfThread = 21;
    var iterations = 5;

    barrier = new CyclicBarrier(amountOfThread + 1);

    for (int i = 0; i < amountOfThread; i++) {
      new Thread(() -> {
        try {
          barrier.await();
          for (int k = 0; k < iterations; k++) {
            semaphoreImp.release();
            semaphoreImp.acquire();
            semaphoreImp.release();
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

    assertEquals(0, semaphoreImp.getState(), "Expected state to be 0, but was " +
        semaphoreImp.getState());
  }
}

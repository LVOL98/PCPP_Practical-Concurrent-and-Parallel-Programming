# Solutions Exercise 7

## 7.1
*Implement a CasHistogram class in the style of week 6 with this interface:*

```java
interface Histogram {
    void increment(int bin);
    int getCount(int bin);
    int getSpan();
    int getAndClear(int bin);
}
```
*The implementation must use AtomicInteger (instead of locks), and only use the methods compareAndSet and get; no other methods provided in the class AtomicInteger are allowed. The method getAndClear returns the current value in the bin and sets it to 0.*

### Mandatory

#### 7.1.1
***
*Write a class CasHistogram implementing the above interface. In your implementation, ensure that: i) class state does not escape, and ii) safe-publication. Explain why i) and ii) are guaranteed in your implementation, and report any immutable variables.*

See [./app/src/main/java/exercises07/CasHistogram.java](./app/src/main/java/exercises07/CasHistogram.java)

**i**: The class state is stored in the `counts` field by using `AtomicInteger`, hence we need to ensure that neither the `counts` array nor the `AtomicInteger` object escapes. In the constructor the state doesn't escape as the reference to the `counts` array is kept internal to the class. Further, there's no method in the implementation that leaks a reference to an `AtomicInteger`, only a copy of the value `AtomicInteger` was holding is returned

**ii**: From the answer to question i as follows safe-publication or rather, there's no publication as only copy of values are returned

The array `counts` storing the values of the bins are immutable

***

#### 7.1.2
***
*Write a parallel functional correctness test for CasHistogram to check that it correctly stores the number of prime factors in the range (0, 4999999); as you did in exercise 6.3.3 in week 6. You must use JUnit 5 and the techniques we covered in week 4. The test must be executed with 2^n threads for n ∈ {0, . . . , 4}. To assert correctness, perform the same computation sequentially using the class Histogram1 from week 6. Your test must check that each bin of the resulting CasHistogram (executed in parallel) equals the result of the same bin in Histogram1 (executed sequentially).*

*Note: The method getAndClear was not part of the Histogram interface in week 6. Consequently, you will need to implement a getAndClear method for Histogram1 so that you can implement the new interface. This is just a technicality, since the method is not used in the test.*

See [./app/src/test/java/exercises07/TestHistogram.java](./app/src/test/java/exercises07/TestHistogram.java)

***

#### 7.1.3
***
*Measure the overall time to run the program above for CasHistogram and the lock-based Histogram week 6, concretely, Histogram2. For this task you should not use JUnit 5, as it is does offer good support to measure performance. Instead you can use the code in the file TestCASLockHistogram.java. It contains boilerplate code to evaluate the performance of counting prime factors using two Histogram classes. To execute it, simply create two objects named histogramCAS and histogramLock containing your implementation of Histogram using CAS (CasHistogram) and your implementation of Histogram using a single lock from week 6 (Histogram2).*

*What implementation performs better? The (coarse) lock-based implementation or the CAS-based one?*

*Is this result you got expected? Explain why.*

*Note: Most likely, your implementation for Histogram2 from week 6 does not have a getAndClear method for the same reason as we mentioned above. Simply implement a lock-based method for this exercise so that Histogram2 can implement the new version of the Histogram interface.*

Below is the result of running count primes from 0 ... 4.999.999 using two different Histograms, one using lock stripping and one using CAS (by utilizing the `AtomicInteger`). Note 12 threads was chosen as it is equal to the number of cores on the computer, and the amount of locks was set equal to the amount of threads used, as it wouldn't make sense to have 12 locks but only 2 threads that could use them

```
Mark7 HistogramLock with 1 threads and locks    4588927608.5 ns 260856039.27          2
Mark7 HistogramCAS with 1 threads               5230167686.4 ns 28476177.33          2
Mark7 HistogramLock with 2 threads and locks    3544698695.9 ns 26420528.51          2
Mark7 HistogramCAS with 2 threads               3275045714.9 ns 68240178.02          2
Mark7 HistogramLock with 3 threads and locks    2394839787.0 ns 28555954.69          2
Mark7 HistogramCAS with 3 threads               2337301798.1 ns 14145059.06          2
Mark7 HistogramLock with 4 threads and locks    1994578807.8 ns 18257561.08          2
Mark7 HistogramCAS with 4 threads               1971380355.6 ns 119007538.36          2
Mark7 HistogramLock with 5 threads and locks    1707834978.8 ns 8990695.33          2
Mark7 HistogramCAS with 5 threads               1768409388.3 ns 105311280.13          2
Mark7 HistogramLock with 6 threads and locks    1557716635.4 ns 36545257.07          2
Mark7 HistogramCAS with 6 threads               1750344714.2 ns 137110207.19          2
Mark7 HistogramLock with 7 threads and locks    1447591790.3 ns 14903830.53          2
Mark7 HistogramCAS with 7 threads               1547084762.4 ns 63887263.44          2
Mark7 HistogramLock with 8 threads and locks    1374810291.7 ns 14198172.40          2
Mark7 HistogramCAS with 8 threads               1450098730.6 ns 77405795.26          2
Mark7 HistogramLock with 9 threads and locks    1320347879.9 ns 22540635.22          2
Mark7 HistogramCAS with 9 threads               1367918369.8 ns 52474311.54          2
Mark7 HistogramLock with 10 threads and locks   1308509552.6 ns 51449374.50          2
Mark7 HistogramCAS with 10 threads              1311421254.9 ns 30583915.77          2
Mark7 HistogramLock with 11 threads and locks   1266214667.2 ns 15625407.50          2
Mark7 HistogramCAS with 11 threads              1241064288.8 ns 44827912.66          2
Mark7 HistogramLock with 12 threads and locks   1247667498.2 ns 23894907.31          2
Mark7 HistogramCAS with 12 threads              1205534439.5 ns 36178186.63          2
```

As the CAS implementation is essentially a lock stripped histogram gram with the amount of "OS" locks being equal to the amount of bins, we'd expect the CAS histogram to outperform the lock histogram at higher thread count, which is what we observe in the above execution

***

## 7.2
*Recall read-write locks, in the style of Java’s java.util.concurrent.locks.ReentrantReadWriteLock. As we discussed, this type of lock can be held either by any number of readers, or by a single writer. In this exercise you must implement a simple read-write lock class SimpleRWTryLock that is not reentrant and that does not block. It should implement the following interface:*

```java
interface SimpleRWTryLockInterface {
    public boolean readerTryLock();
    public void readerUnlock();
    public boolean writerTryLock();
    public void writerUnlock();
}
```

*For convenience, we provide the skeleton of the class in ReadWriteCASLock.java.*

*Method writerTryLock is called by a thread that tries to obtain a write lock. It must succeed and return true if the lock is not already held by any thread, and return false if the lock is held by at least one reader or by a writer.*

*Method writerUnlock is called to release the write lock, and must throw an exception if the calling thread does not hold a write lock.*

*Method readerTryLock is called by a thread that tries to obtain a read lock. It must succeed and return true if the lock is held only by readers (or nobody), and return false if the lock is held by a writer.*

*Method readerUnlock is called to release a read lock, and must throw an exception if the calling thread does not hold a read lock.*

*The class can be implemented using AtomicReference and compareAndSet(...), by maintaining a single field holders which is an atomic reference of type Holders, an abstract class that has two concrete subclasses:*

```java
private static abstract class Holders { }

private static class ReaderList extends Holders {
    private final Thread thread;
    private final ReaderList next;
    ...
}

private static class Writer extends Holders {
    public final Thread thread;
    ...
}
```

*The ReaderList class is used to represent an immutable linked list of the threads that hold read locks. The Writer class is used to represent a thread that holds the write lock. When holders is null the lock is unheld.*

*(Representing the holders of read locks by a linked list is very inefficient, but simple and adequate for illustration. The real Java ReentrantReadWriteLock essential has a shared atomic integer count of the number of locks held, supplemented with a ThreadLocal integer for reentrancy of each thread and for checking that only lock holders unlock anything. But this would complicate the exercise. Incidentally, the design used here allows the read locks to be reentrant, since a thread can be in the reader list multiple times, but this is inefficient too).*

### Mandatory

#### 7.2.1
***
*Implement the writerTryLock method. It must check that the lock is currently unheld and then atomically set holders to an appropriate Writer object.*

See [./app/src/main/java/exercises07/ReadWriteCASLock.java](./app/src/main/java/exercises07/ReadWriteCASLock.java)

***

#### 7.2.2
***
*Implement the writerUnlock method. It must check that the lock is currently held and that the holder is the calling thread, and then release the lock by setting holders to null; or else throw an exception.*

See [./app/src/main/java/exercises07/ReadWriteCASLock.java](./app/src/main/java/exercises07/ReadWriteCASLock.java)

***

#### 7.2.3
***
*Implement the readerTryLock method. This is marginally more complicated because multiple other threads may be (successfully) trying to lock at the same time, or may be unlocking read locks at the same time. Hence you need to repeatedly read the holders field, and, as long as it is either null or a ReaderList, attempt to update the field with an extended reader list, containing also the current thread.*

*(Although the SimpleRWTryLock is not intended to be reentrant, for the purposes of this exercise you need not prevent a thread from taking the same lock more than once).*

See [./app/src/main/java/exercises07/ReadWriteCASLock.java](./app/src/main/java/exercises07/ReadWriteCASLock.java)

***

#### 7.2.4
***
*Implement the readerUnlock method. You should repeatedly read the holders field and, as long as i) it is non-null and ii) refers to a ReaderList and iii) the calling thread is on the reader list, create a new reader list where the thread has been removed, and try to atomically store that in the holders field; if this succeeds, it should return. If holders is null or does not refer to a ReaderList or the current thread is not on the reader list, then it must throw an exception.*

*For the readerUnlock method it is useful to implement a couple of auxiliary methods on the immutable ReaderList:*

```java
public boolean contains(Thread t) { ... }
public ReaderList remove(Thread t) { ... }
```

See [./app/src/main/java/exercises07/ReadWriteCASLock.java](./app/src/main/java/exercises07/ReadWriteCASLock.java)

***

#### 7.2.5
***
*Write simple sequential JUnit 5 correctness tests that demonstrate that your read-write lock works with a single thread. Your test should check, at least, that:*

- *It is not possible to take a read lock while holding a write lock.*
- *It is not possible to take a write lock while holding a read lock.*
- *It is not possible to unlock a lock that you do not hold (both for read and write unlock).*

*You may write other tests to increase your confidence that your lock implementation is correct.*

See [./app/src/test/java/exercises07/TestLocks.java](./app/src/test/java/exercises07/TestLocks.java)

***

#### 7.2.6
***
*Finally, write a parallel functional correctness test that checks that two writers cannot acquire the lock at the same time. You must use JUnit 5 and the techniques we covered in week 4. Note that for this exercise readers are irrelevant. Intuitively, the test should create two or more writer threads that acquire and release the lock. You should instrument the test to check whether there were 2 or more threads holding the lock at the same time. This check must be performed when all threads finished their execution. This test should be performed with enough threads so that race conditions may occur (if the lock has bugs).*

See [./app/src/test/java/exercises07/TestLocks.java](./app/src/test/java/exercises07/TestLocks.java)

***

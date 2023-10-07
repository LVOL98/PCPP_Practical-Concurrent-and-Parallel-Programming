# Answers to Exercise 6

## Exercise 6.1
*This exercise is based on the program AccountExperiments.java (in the exercises directory for week 6). It generates a number of transactions to move money between accounts. Each transaction simulate transaction time by sleeping 50 milliseconds. The transactions are randomly generated, but ensures that the source and target accounts are not the same*

### Mandatory

#### 6.1.1
***
*Use Mark7 (from Benchmark.java in the benchmarking package) to measure the execution time andverify that the time it takes to run the program is proportional to the transaction time*

Running the `AccountExperiment` with 1, 2, 4, 8 and 16 transaction:

```
Mark7 AccountExperiments with 1 transactions and 10 accounts      50447470.0 ns   104992.54          8
Mark7 AccountExperiments with 2 transactions and 10 accounts      100939507.5 ns  203155.79          4
Mark7 AccountExperiments with 4 transactions and 10 accounts      201855261.9 ns  250677.29          2
Mark7 AccountExperiments with 8 transactions and 10 accounts      403728329.9 ns  320129.87          2
Mark7 AccountExperiments with 16 transactions and 10 accounts     806877051.4 ns  749124.96          2
```

Where we see an approxiamte doubleing of the running time

***

#### 6.1.2
***
*Now consider the version in ThreadsAccountExperimentsMany.java (in the directory exercise61).*

*Consider these four lines of the transfer:*

```java
    Account min = accounts[Math.min(source.id, target.id)];
    Account max = accounts[Math.max(source.id, target.id)];
    synchronized(min){
        synchronized(max){
```

*Explain why the calculation of min and max are necessary? Eg. what could happen if the code was written like this:*

```java
    Account s = accounts[source.id];
    Account t = accounts[target.id];
    synchronized(s){
        synchronized(t){
```

*Run the program with both versions of the code shown above and explain the results of doing this*

Let's say we have the following setup, and see what the values are

```java
source.id = 2
target.id = 1

min/max
min = 1
max = 2

source
s = 2
t = 1

```

We notice that `min != s && max != t` so each implementation have different ordering of synchronization locks. Now take the following interleaving of two threads, t1 and t2 with

```java
    Account s = accounts[source.id];    // 1
    Account t = accounts[target.id];    // 2
    synchronized(s){                    // 3
        synchronized(t){                // 4

t1(1) s = 2 --> t1(2) t = 1 --> t1(3) lock on s = 2 --> t2(1) s = 1 --> t2(2) t = 2 --> t2(3) lock on s = 1
```

Now we have reached a deadlock as t1 has acquired the lock on 2 and t2 have acquired the lock on 1, hence both threads are waiting for a lock the other holds, and a deadlock occurs

***

#### 6.1.3
***
*Change the program in ThreadsAccountExperimentsMany.java to use a the executor framework instead of raw threads. Make it use a ForkJoin thread pool. For now do not worry about terminating the main thread, but insert a print statement in the doTransaction method, so you can see that all executors are active*

TODO: Vertify that this is correct and remember that the next task is related to this task

See See [./src/main/java/exercises61/ThreadsAccountExperimentsMany.java](./src/main/java/exercises61/ThreadsAccountExperimentsMany.java) which has the following implementation

```java
  public ThreadsAccountExperimentsMany() {
    threadPool = new ForkJoinPool(NO_THREADS);

    for (int i = 0; i < N; i++) {
      accounts[i] = new Account(i);
    }

    for (int i = 0; i < NO_THREADS; i++) {
      var t = threadPool.submit(() -> {
        try {
          doNTransactions(NO_TRANSACTION);
        } catch (Error ex) {
          ex.printStackTrace();
          System.exit(0);
        }
      });

      try {
        t.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
  }
```

***

#### 6.1.4
***
*Ensure that the executor shuts down after all tasks has been executed.*

*Hint: See slides for suggestions on how to wait until all tasks are finished*

See the below modification to shut down a thread pool

```java
  public ThreadsAccountExperimentsMany() {
    ...

    threadPool.shutdown();
  }
```

***

## 6.2
*Use the code in file TestCountPrimesThreads.java (in the exercises directory for week 6) to count prime numbers using threads*

### Mandatory

#### 6.2.1
***
*Report and comment on the results you get from running TestCountPrimesThreads.java*

```
countSequential                 8410329.0 ns   70167.34         32
countParallelN  1               8958130.0 ns  114261.02         32
countParallelNLocal  1          8697017.3 ns   74379.95         32
countParallelN  2               5933838.1 ns  135379.07         64
countParallelNLocal  2          5589356.8 ns   96870.16         64
countParallelN  3               4408616.7 ns  104391.10         64
countParallelNLocal  3          4231171.0 ns   36950.05         64
countParallelN  4               3655200.4 ns   55395.86        128
countParallelNLocal  4          3573172.0 ns   65769.41        128
countParallelN  5               3585528.3 ns  118924.39        128
countParallelNLocal  5          3494811.5 ns  129418.78        128
countParallelN  6               3931125.7 ns   94608.58         64
countParallelNLocal  6          3834818.1 ns  114839.71        128
countParallelN  7               3779926.5 ns  217393.16        128
countParallelNLocal  7          3669721.8 ns   44193.41        128
countParallelN  8               3590629.6 ns   81931.37        128
countParallelNLocal  8          3557523.6 ns   83909.59        128
countParallelN  9               3601288.3 ns  117726.55        128
countParallelNLocal  9          3556687.1 ns   64952.35        128
countParallelN 10               3538581.6 ns  106989.20        128
countParallelNLocal 10          3576636.8 ns   94946.63        128
countParallelN 11               3734171.0 ns   71020.45        128
countParallelNLocal 11          3606540.6 ns   92724.26        128
countParallelN 12               3599008.1 ns  144158.63        128
countParallelNLocal 12          3684831.7 ns   63662.62        128
countParallelN 13               3736199.4 ns  164938.72        128
countParallelNLocal 13          3786405.1 ns   79675.80        128
countParallelN 14               3592937.8 ns  103184.33        128
countParallelNLocal 14          3719360.8 ns  385539.95        128
countParallelN 15               3829819.8 ns   71640.60        128
countParallelNLocal 15          3627291.2 ns  114882.38        128
countParallelN 16               3716660.4 ns   68489.22        128
countParallelNLocal 16          3781135.4 ns  248233.54        128
```

First let's just define what each of the calls does. sequential, countParallelN (**from now on called parallel**) and countParallelNLocal (**from now on called parallelLocal**) all count the amount of primes in a given range, but the parllel does it in parllel

The difference between parallel and parllelLocal is that local uses a local counter per thread, which is summarized once each thread is finished, while the other parllel uses a shared `LongCounter`. What we see is that parllel runs ever so slightly faster than parallelLocal (in most cases), which demonstrates the overhead of synchronizing a shared memory structure

Further, we see that increasing the amount of threads above 4 doesn't yield any performance benefits. While the intervals given to each thread doesn't contain an equal amount of primes, the range of integers tested is rather short (1.000.000). Hence it's more probable that Amdahl's law is limiting the performance, where the overhead of creating threads for smaller and smaller intervals is greater than the performance benefits of paralizing the code that can be paralized

***

#### 6.2.2
***
*Rewrite TestCountPrimesthreads.java using Futures for the tasks of each of the threads in part 1. Run your solutions and report results. How do they compare with the results from the version using threads?*

See [./src/main/java/exercises62/TestCountPrimesThreadsFuture.java](./src/main/java/exercises62/TestCountPrimesThreadsFuture.java)

TODO: Why don't we see any improvements?

```
countSequential                 8037789.5 ns   99647.30         32
countParallelN  1               8483520.3 ns   67305.75         32
countParallelNLocal  1          8813271.2 ns   82389.44         32
countParallelN  2               5693845.3 ns   53497.39         64
countParallelNLocal  2          5869589.9 ns   98983.86         64
countParallelN  3               4580052.1 ns   57198.36         64
countParallelNLocal  3          4595003.7 ns   28505.45         64
countParallelN  4               4048842.7 ns   57907.86         64
countParallelNLocal  4          4442303.7 ns  146690.19         64
countParallelN  5               4326450.2 ns  185615.49         64
countParallelNLocal  5          4333452.6 ns  177725.48         64
countParallelN  6               4132109.1 ns  238939.30         64
countParallelNLocal  6          4099793.1 ns  141465.79         64
countParallelN  7               4121865.7 ns   95425.28         64
countParallelNLocal  7          4142195.2 ns   62259.39         64
countParallelN  8               4408916.9 ns  242124.35         64
countParallelNLocal  8          3754984.8 ns  169130.19         64
countParallelN  9               4235087.6 ns  111732.30         64
countParallelNLocal  9          4393084.1 ns  230168.36         64
countParallelN 10               4111471.3 ns   94478.29         64
countParallelNLocal 10          4114935.1 ns   45719.09         64
countParallelN 11               4177452.7 ns   78302.21         64
countParallelNLocal 11          4169475.8 ns  166472.54         64
countParallelN 12               4181841.6 ns   48029.83         64
countParallelNLocal 12          4199862.9 ns  119780.28         64
countParallelN 13               4198601.5 ns  129954.09         64
countParallelNLocal 13          4106976.7 ns   90888.97         64
countParallelN 14               4146729.5 ns  127273.59         64
countParallelNLocal 14          4136566.6 ns  124550.78         64
countParallelN 15               3976372.3 ns  154845.24         64
countParallelNLocal 15          3971906.9 ns  191875.86         64
countParallelN 16               4035461.3 ns  198823.02         64
countParallelNLocal 16          4026936.0 ns   56653.03        128
```

***

## Exercise 6.3
*A histogram is a collection of bins, each of which is an integer count. The span of the histogram is the number of bins. In the problems below a span of 30 will be sufficient; in that case the bins are numbered 0. . . 29.*

*Consider this Histogram interface for creating histograms:*

```java
interface Histogram {
  public void increment(int bin);
  public float getPercentage(int bin);
  public int getSpan();
}
```
*Method call increment(7) will add one to bin 7; method call getCount(7) will return the current count in bin 7;method call getPercentage(7) will return the current percentage of total in bin 7; method getSpan() will return the number of bins; method call getTotal() will return the current total of all bins.*

*There is a non-thread-safe implementation of Histogram1 in file SimpleHistogram.java. You may assume that the dump method given there is called only when no other thread manipulates the histogram and therefore does not require locking, and that the span is fixed (immutable) for any given Histogram object.*

### Mandatory

#### 6.3.1
***
*Make a thread-safe implementation, class Histogram2 implementing the interface Histogram. Use suitable modifiers (final and synchronized) in a copy of the Histogram1 class. This class must use at most one lock to ensure mutual exclusion.*
 
*Explain what fields and methods need modifiers and why. Does the getSpan method need to be synchronized?*

See [./src/main/java/exercises63/Historgram2.java](./src/main/java/exercises63/Historgram2.java)

We need to make sure that `increment` happens mutually exclusive with any other increment call, hence this has to be synchronized

Whether or not we need to ensure that `getCount` happens mutually exclusive depends on the requirement of the program. If the programs allows stale values, caused by a data race then the synchronized keyword isn't needed

Finally `getSpan` doesn't have to be synchronized due to counts being `final`. Hence the size of the array cannot change and getSpan can never return another value than the size specified in the constructor

***

#### 6.3.2
***
*Now create a new class, Histogram3 (implementing the Histogram interface) that uses lock striping. You can start with a copy of Histogram2. Then, the constructor of Histogram3 must take an additional parameter nrLocks which indicates the number of locks that the histogram uses. You will have to associate a lock to each bin. Note that, if the number of locks is less than the number of bins, you may use the same lock for more than one bin. Try to distribute locks evenly among bins; consider the modulo operation % for this task.*

See [./src/main/java/exercises63/Historgram3.java](./src/main/java/exercises63/Historgram3.java)

***

#### 6.3.3
***
*Now consider again counting the number of prime factors in a number p. Use the Histogram2 class to write a program with multiple threads that counts how many numbers in the range 0. . . 4 999 999 have 0 prime factors, how many have 1 prime factor, how many have 2 prime factors, and so on. You may draw inspiration from the TestCountPrimesThreads.java.*

*The correct result should look like this:*

```
  0: 2
  1: 348513
  2: 979274
  3: 1232881
  4: 1015979
  5: 660254
  6: 374791
  7: 197039
  8: 98949
  9: 48400
... and so on
```

*showing that 348 513 numbers in 0. . . 4 999 999 have 1 prime factor (those are the prime numbers), 979 274 numbers have 2 prime factors, and so on. (The 2 numbers that have 0 prime factors are 0 and 1). And of course the numbers in the second column should add up to 5 000 000.*

*Hint: There is a class HistogramPrimesThread.java which you can use a starting point for this exercise. That class contains a method countFactors(int p) which returns the number of prime factors of p. This might be handy for the exercise*

See [./src/main/java/exercises63/HistorgramPrimesThreads.java](./src/main/java/exercises63/HistorgramPrimesThreads.java)

***

#### 6.3.4
***
*Finally, evaluate the effect of lock striping on the performance of question 6.3. Create a new class where you use Mark7 to measure the performance of Histogram3 with increasing number of locks to compute the number of prime factors in 0. . . 4 999 999. Report your results and comment on them. Is there a increase or not? Why?*

Note: a thread count of 12 was choosen as the PC used for testing had 12 processors

```
Mark7 count primes using Histogram3 with 12 threads and 1 locks      2884522789.9 ns 155340675.86         2
Mark7 count primes using Histogram3 with 12 threads and 2 locks      2165639853.2 ns 60922705.92          2
Mark7 count primes using Histogram3 with 12 threads and 3 locks      1727932604.2 ns 30628190.43          2
Mark7 count primes using Histogram3 with 12 threads and 4 locks      1480927419.1 ns 27914167.45          2
Mark7 count primes using Histogram3 with 12 threads and 5 locks      1405781118.3 ns 35632433.70          2
Mark7 count primes using Histogram3 with 12 threads and 6 locks      1397201715.7 ns 39230403.28          2
Mark7 count primes using Histogram3 with 12 threads and 7 locks      1362618796.6 ns 22425425.17          2
Mark7 count primes using Histogram3 with 12 threads and 8 locks      1357671206.7 ns 31189407.82          2
Mark7 count primes using Histogram3 with 12 threads and 9 locks      1360804582.3 ns 26347640.07          2
Mark7 count primes using Histogram3 with 12 threads and 10 locks     1352582566.1 ns 24835599.91          2
Mark7 count primes using Histogram3 with 12 threads and 11 locks     1355577159.4 ns 24678977.62          2
Mark7 count primes using Histogram3 with 12 threads and 12 locks     1343655950.7 ns 30753391.70          2
Mark7 count primes using Histogram3 with 12 threads and 13 locks     1351389171.1 ns 18240128.14          2
Mark7 count primes using Histogram3 with 12 threads and 14 locks     1355926468.0 ns 36498350.72          2
Mark7 count primes using Histogram3 with 12 threads and 15 locks     1343338442.8 ns 36143562.16          2
Mark7 count primes using Histogram3 with 12 threads and 16 locks     1352412701.9 ns 26899578.17          2
```

We see a clear performance increase from 1 to 4 locks, which makes sense as lock stripping is useful when there's a lot of lock congestion. What we also see is using more than 4 locks doesn't improve performance signifiantly, which indicate that there's not much lock congestion left

***

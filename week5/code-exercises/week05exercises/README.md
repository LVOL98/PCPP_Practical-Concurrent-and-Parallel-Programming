# Solutions Exercise 5

Read the start of [./week5/exercises05.pdf](./week5/exercises05.pdf)

## 5.1
*In this exercise you must perform, on your own hardware, the measurement performed in the lecture using the example code in file TestTimeThreads.java.*

### Mandatory

#### 5.1.1
***
*First compile and run the thread timing code as is, using Mark6, to get a feeling for the variation and robustness of the results. Do not hand in the results but discuss any strangenesses, such as large variation in the time measurements for each case.*

We do see some large variation. First let's discuss the factors that could cause any deviations in program execution times. 

1. Garbage collection: the JVM garbage collector might kick in during the execution slowing down the measurement being performed
2. Interleaving of other programs: though we disabled most other programs, there's still some essential software from the OS running, that might put a thread on pause, that is currently being measured

The code itself doesn't do any complex work, hence there's nothing in the `TestTimeThreads.java` that could affect performance measurements

***

#### 5.1.2
***
*Now change all the measurements to use Mark7, which reports only the final result. Record the results in a text file along with appropriate system identification. Include the results in your hand-in, and reflect and comment on them: Are they plausible? Any surprises? Mention any cases where they deviate significantly from those shown in the lecture.*

```
# OS:   Linux; 5.15.90.1-microsoft-standard-WSL2; amd64
# JVM:  Eclipse Adoptium; 17.0.8.1
# CPU:  null; 16 "cores"
# Date: 2023-09-25T16:52:30+0000
Mark 7 measurements
hashCode()                            6.7 ns       0.06   67108864
Point creation                       46.7 ns       0.88    8388608
Thread's work                      4251.4 ns      60.22      65536
Thread create                      1101.8 ns      65.77     262144
Thread create start              219639.9 ns    4679.32       2048
Thread create start join         250664.3 ns   12258.78       2048
ai value = 1392580000
Uncontended lock                     11.7 ns       0.46   33554432
```

Looking at the above result, we cannot say anything about Thread create start join as it was not mentioned in the slides. Otherwise every performance measure seems to take a bit longer than what was in the slides, which in most cases is a rather small deviation from the slides. Only Thread create start deviates by quite a lot from the slides (by a factor of 10). All these deviations could be due to the use of different operating systems, different version of JVM and so on.

***

## 5.2
*In this exercise you must use the benchmarking infrastructure to measure the performance of the prime counting example given in file TestCountPrimesThreads.java.*

### Mandatory

#### 5.2.1
***
*Measure the performance of the prime counting example on your own hardware, as a function of the number of threads used to determine whether a given number is a prime. Record system information as well as the measurement results for 1. . . 32 threads in a text file. If the measurements take excessively long time on your computer, you may measure just for 1. . . 16 threads instead.*

See [./5.2.1.txt](./5.2.1.txt) or below

```
# OS:   Linux; 5.15.90.1-microsoft-standard-WSL2; amd64
# JVM:  Eclipse Adoptium; 17.0.8.1
# CPU:  null; 16 "cores"
# Date: 2023-09-25T14:51:13+0000
countSequential                 9901191,9 ns  471885,74         32
countParallelN       1         10342673,8 ns  418395,86         32
countParallelN       2          7414577,8 ns  387478,11         64
countParallelN       3          5835413,4 ns  400720,86         64
countParallelN       4          5206997,2 ns  209247,17         64
countParallelN       5          4501809,5 ns  270224,39         64
countParallelN       6          3981303,8 ns   96965,14         64
countParallelN       7          3813011,6 ns   49811,58        128
countParallelN       8          3754979,2 ns  155293,64        128
countParallelN       9          3729585,8 ns   57216,83        128
countParallelN      10          3723846,8 ns  104355,06        128
countParallelN      11          3706627,1 ns   54679,18        128
countParallelN      12          3727183,2 ns   59385,63        128
countParallelN      13          3754326,3 ns   49956,04        128
countParallelN      14          3682043,7 ns   45795,18        128
countParallelN      15          3719533,8 ns   37134,14        128
countParallelN      16          3758541,5 ns   17639,46        128
countParallelN      17          3752656,6 ns   51902,84        128
countParallelN      18          3730390,6 ns   28527,33        128
countParallelN      19          3757758,0 ns   37756,85        128
countParallelN      20          3695495,7 ns   63317,57        128
countParallelN      21          3795647,0 ns   46816,73        128
countParallelN      22          3763716,3 ns   37048,05        128
countParallelN      23          3767617,4 ns   43485,54        128
countParallelN      24          3781845,9 ns  134246,89        128
countParallelN      25          3838061,3 ns   78877,98        128
countParallelN      26          3892537,3 ns  133124,33        128
countParallelN      27          3863154,8 ns   64909,23        128
countParallelN      28          3887980,9 ns   67168,21        128
countParallelN      29          3959189,8 ns  107080,58         64
countParallelN      30          4087476,7 ns  126331,60         64
countParallelN      31          4204668,1 ns  196361,27         64
countParallelN      32          4332637,2 ns  130327,43         64
```

***

#### 5.2.2
***
*Reflect and comment on the results; are they plausible? Is there any reasonable relation between the number of threads that gave best performance, and the number of cores in the computer you ran the benchmarks on? Any surprises?*

The results seems plausible with a few surprises

Two things was observed in the test run

1. As expected with too many threads we see a performance cost. This is as expected that the overhead (coordination and so on) of threads starts to become greater than the performance benefits
2. Surprisingly the performance gain mellowed out before the amount of threads equalled the amount of cores (around 6 threads). This could indicate that the optimum concurrency for this specific program lies around the 6 thread count (more on parallel algorithms and Amdahl's law in exercise 6)

***

## 5.3
*In this exercise you should estimate whether there is a performance gain by declaring a shared variable as volatile. Consider this simple class that has both a volatile int and another int that is not declared volatile:*

```java
public class TestVolatile {
    private volatile int vCtr;
    private int ctr;

    public void vInc () {
        vCtr++;
    }

    public void inc () {
        ctr++;
    }
}
```

### Mandatory

#### 5.3.1
***
*Use Mark7 (from Bendchmark.java) to compare the performance of incrementing a volatile int and a normal int. Include the results in your hand-in and comment on them: Are they plausible? Any surprises?*

Due to the lack of synchronization we run the tests with sequential execution

```text
Test of non-volatile int              2.9 ns       0.03  134217728
Test of non-volatile int              3.0 ns       0.16  134217728
Test of non-volatile int              2.9 ns       0.10  134217728
Test of non-volatile int              2.9 ns       0.07  134217728
Test of non-volatile int              3.0 ns       0.19  134217728
Test of volatile int                  3.0 ns       0.07  134217728
Test of volatile int                  3.0 ns       0.09  134217728
Test of volatile int                  3.1 ns       0.11  134217728
Test of volatile int                  3.0 ns       0.09  134217728
Test of volatile int                  3.1 ns       0.12  134217728
```

Suprisingly we see little to no performance loss when using the `volatile` keyword in this sequential context. We've thuoght that reading the `vCtr`from main memory instead of the CPU cache would incurre a greater performance cost 

***

## 5.4
*In this exercise you must write code searching for a string in a (large) text. Such a search is the core of any web-crawling service such as Google, Bing, Duck-Go-Go etc. Later in the semester, there will be a guest lecture from a Danish company providing a very specialized web-crawling solution that provides search results in real-time.*

*In this exercise you will work with the nonsense text found in:*

*src/main/resources/long-text-file.txt (together with the other exercise code). You may read the file with this code:*

```java
final String filename = "src/main/resources/long-text-file.txt";
...
public static String[] readWords(String filename) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return reader.lines().toArray(String[]::new);//will be explained in Week 7
    } catch (IOException exn) { return null;}
}
```

*readWords will give you an array of lines, each of which is a string of (nonsense) words.*

*The purpose of the code you are asked to write is to find all occurences of a particular word in the text. This skeleton is based on sequentially searching the text (i.e. one thread). You may find it in the code-exercises directory for Week05.*

```java
public class TestTimeSearch {
    public static void main(String[] args) { new TestTimeSearch(); }

    public TestTimeSearch() {
        final String filename = "src/main/resources/long-text-file.txt";
        final String target= "ipsum";

        final LongCounter lc= new LongCounter();
        String[] lineArray= readWords(filename);

        System.out.println("Array Size: "+ lineArray.length);
        System.out.println("# Occurences of "+target+ " :"
            +search(target, lineArray, 0, lineArray.length, lc));
    }

    static long search(String x, String[] lineArray, int from,
                                                int to, LongCounter lc){
        //Search each line of file
        for (int i=from; i<to; i++ ) lc.add(linearSearch(x, lineArray[i]));
        return lc.get();
    }

    static long linearSearch(String x, String line) {
        //Search for occurences of c in line
        String[] arr= line.split(" ");
        long count= 0;
        for (int i=0; i<arr.length; i++ ) if ( (arr[i].equals(x)) ) count++;
        return count;
    }
}
```

### Mandatory

#### 5.4.1
***
*TestTimeSearch uses a slightly extended version of the LongCounter where two methods have been added void add(long c) that increments the counter by c and void reset() that sets the counter to 0.*

*Extend LongCounter with these two methods in such a way that the counter can still be shared safely by several threads.*

See [./weel05exercises/app/src/main/java/exercises05/LongCounter.java](./weel05exercises/app/src/main/java/exercises05/LongCounter.java)

***

#### 5.4.2
***
*How many occurencies of "ipsum" is there in long-text-file.txt. Record the number in your solution.*

```
Array Size: 5697
# Occurences of ipsum :1430
```

***

#### 5.4.3
***
*Use Mark7 to benchmark the search function. Record the result in your solution.*

```
# OS:   Linux; 5.10.102.1-microsoft-standard-WSL2; amd64
# JVM:  Eclipse Adoptium; 17.0.8.1
# CPU:  null; 12 "cores"
# Date: 2023-09-27T06:35:25+0000
TestTimeSearch performance, Mark7      33507231.3 ns 1290057.00          8
```

***

#### 5.4.4
***
*Extend the code in TestTimeSearch with a new method*

```java
private static long countParallelN(String target, String[] lineArray, int N, LongCounter lc) {
    // uses N threads to search lineArray
    ...
}
```

*Fill in the body of countParallelN in such a way that the method uses N threads to search the lineArray. Provide a few test results that make i plausible that your code works correctly.*

The implemented countParallelN

```java
private static long countParallelN(String target, String[] lineArray, int N, LongCounter lc) {
    barrier = new CyclicBarrier(N + 1, null);

    final var splits = evenlysplitInt(lineArray.length, N);

    for (int i = 0; i < N; i++) {
        var fromAndTo = splits.get(i);

        new Thread(() -> {
        try {
            barrier.await();
            search(target, lineArray, fromAndTo.get(0), fromAndTo.get(1), lc);
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

    return lc.get();
}
```

Following is test results for runinng the code with 1 to 10 threads

```
Thread count: 1 target: ipsum count: 1430
Thread count: 2 target: ipsum count: 1430
Thread count: 3 target: ipsum count: 1430
Thread count: 4 target: ipsum count: 1430
Thread count: 5 target: ipsum count: 1430
Thread count: 6 target: ipsum count: 1430
Thread count: 7 target: ipsum count: 1430
Thread count: 8 target: ipsum count: 1430
Thread count: 9 target: ipsum count: 1430
Thread count: 10 target: ipsum count: 1430
```

***

#### 5.4.5
***
*Use Mark7 to benchmark countParallelN. Record the result in your solution and provide a small discussion of the timing results.*

```
Sequential                    134473903.4 ns 39395035.70          2
1 threads                     154088612.8 ns 30855639.63          2
2 threads                     143105149.4 ns 34967380.50          4
3 threads                     131323066.0 ns 40559647.77          4
4 threads                     136780399.8 ns 37841243.68          2
5 threads                     142829034.3 ns 30374315.76          4
6 threads                     151274401.8 ns 32921528.19          4
7 threads                     134285726.5 ns 42397551.74          2
8 threads                     127330035.9 ns 36941695.92          2
9 threads                     144113498.3 ns 58459823.86          2
10 threads                    158339028.1 ns 33150114.50          2
```

It seems that the overhead of using threads does not improve the performance. Maybe this is caused by the long counter being the single bottleneck which needs to be handled synchronous anyway.
***
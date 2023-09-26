# Solutions Exercise 5

Read the start of [./week5/exercises05.pdf](./week5/exercises05.pdf)

## 5.1
*In this exercise you must perform, on your own hardware, the measurement performed in the lecture using the example code in file TestTimeThreads.java.*

### Mandatory

#### 5.1.1
***
*First compile and run the thread timing code as is, using Mark6, to get a feeling for the variation and robustness of the results. Do not hand in the results but discuss any strangenesses, such as large variation in the time measurements for each case.*

First let's discuss the factors that could cause any deviations in program execution times. 

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

Looking at the above result, we cannot say anything about Thread create start join as it was not mentioned in the slides. Otherwise every performance measure seems to take a but longer than what was in the slides, which in most cases is a rather small deviation from the slides. Only Thread create start deviates by quite a lot from the slides (by a factor of 10). This is probably due to either different operating systems, different version of JVM and so on.

***

## 5.2
*In this exercise you must use the benchmarking infrastructure to measure the performance of the prime counting example given in file TestCountPrimesThreads.java.*

### Mandatory

#### 5.2.1
***
*Measure the performance of the prime counting example on your own hardware, as a function of the number of threads used to determine whether a given number is a prime. Record system information as well as the measurement results for 1. . . 32 threads in a text file. If the measurements take excessively long time on your computer, you may measure just for 1. . . 16 threads instead.*

See [./5.2.1.txt](./5.2.1.txt)

***

#### 5.2.2
***
*Reflect and comment on the results; are they plausible? Is there any reasonable relation between the number of threads that gave best performance, and the number of cores in the computer you ran the benchmarks on? Any surprises?*

TODO: run it not in a docker container
TODO: why do our performance increase stop before our core count?

Two things was observed in the test run

1. With a too great amount of threads we see a performance cost. This is as expected that the overhead (coordination and so on) of threads starts to become greater than the performance benefits
2. The performance gain mellowed out before the amount of threads equalled the amount of cores (around 6 threads). This could indicate that this specific scenario

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

TODO: As expected using the keyword `volatile` in a sequential context should have a negleliable effect.

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



***

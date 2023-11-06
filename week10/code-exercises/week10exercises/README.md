# Exercise 10

## 10.1
*Consider this program that computes prime numbers using a while loop:*

```java
class PrimeCountingPerf {
public static void main(String[] args) { new PrimeCountingPerf(); }
static final int range= 100000;
//Test whether n is a prime number
private static boolean isPrime(int n) {
int k= 2;
while (k * k <= n && n % k != 0)
k++;
return n >= 2 && k * k > n;
}
// Sequential solution
private static long countSequential(int range) {
long count= 0;
final int from = 0, to = range;
for (int i=from; i<to; i++)
if (isPrime(i)) count++;
return count;
}
// Stream solution
private static long countStream(int range) {
long count= 0;
//to be filled out
return count;
}
// Parallel stream solution
private static long countParallel(int range) {
long count= 0;
//to be filled out
return count;
}
// --- Benchmarking infrastructure ---
public static double Mark7(String msg, IntToDoubleFunction f) { ... }
public PrimeCountingPerf() {
Mark7("Sequential", i -> countSequential(range));
Mark7("IntStream", i -> countIntStream(range));
Mark7("Parallel", i -> countParallel(range));
List<Integer> list = new ArrayList<Integer>();
for (int i= 2; i< range; i++){ list.add(i); }
Mark7("ParallelStream", i -> countparallelStream(list));
}
}
```
*You may find this in Week10/code-exercises ... /PrimeCountingPerf.java. In addition to counting the number of primes (in the range: 2..range) this program also measures the running time of the loop. Note, in your solution you may change this declaration (and initialization) long count= 0;*

### Mandatory

#### 10.1.1
***
*Compile and run PrimeCountingPerf.java. Record the result in a text file*

```
Sequential                      8437668.8 ns  238861.12         32
IntStream                             1.0 ns       0.01  268435456
Parallel                              1.0 ns       0.02  268435456
ParallelStream                        2.2 ns       0.05  134217728
```

***

#### 10.1.2
***
*Fill in the Java code using a stream for counting the number of primes (in the range: 2..range). Record the result in a text file*

See [app/src/main/java/exercises10/PrimeCountingPerf.java](app/src/main/java/exercises10/PrimeCountingPerf.java)

Sequential                      8181015.0 ns   98301.29         32
IntStream                       8086756.9 ns  150329.67         32
Parallel                              1.0 ns       0.02  268435456
ParallelStream                        1.0 ns       0.01  268435456

***

#### 10.1.3
***
*Add code to the stream expression that prints all the primes in the range 2..range. To test this program reduce range to a small number e.g. 1000*

See [app/src/main/java/exercises10/PrimeCountingPerf.java](app/src/main/java/exercises10/PrimeCountingPerf.java)

***

#### 10.1.4
***
*Fill in the Java code using the intermediate operation parallel for counting the number of primes (in the range: 2..range). Record the result in a text file*

```
Sequential                      8430737.3 ns  198484.10         32
IntStream                       8073198.8 ns  180595.58         32
Parallel                        1630103.3 ns   25389.44        256
ParallelStream                        1.0 ns       0.01  268435456
```

***

#### 10.1.5
***
*Add another prime counting method using a parallelStream for counting the number of primes (in the range: 2..range). Measure its performance using Mark7 in a way similar to how we measured the performance of the other three ways of counting primes*

```
Sequential                      8333896.7 ns  124099.13         32
IntStream                       8243735.1 ns   90697.41         32
Parallel                        1699800.4 ns   83043.16        256
ParallelStream                  1710717.5 ns  105506.14        256
```

***

## 10.2
*This exercise is about processing a large body of English words, using streams of strings. In particular, we use the words in the file app/src/main/resources/english-words.txt, in the exercises project directory.*

*The exercises below should be solved without any explicit loops (or recursion) as far as possible (that is, use streams)*

### Mandatory

#### 10.2.1 - 10.2.8
***

See []()

***

## 10.3
*This exercise is based on the article: (Introduction to Java 8 Parallel Stream) (on the readme for week10). Start by reading this*

### Mandatory

#### 10.3.1
***
*Redo the first example (running the code in Java8ParallelStreamMain) described in the article. Your solution should contain the output from doing this experiment and a short explanation of your output*

```
Sequential - range: 10
0 - main
1 - main
2 - main
3 - main
4 - main
5 - main
6 - main
7 - main
8 - main
9 - main

Parallel - range: 10
6 - main
2 - ForkJoinPool.commonPool-worker-1
3 - ForkJoinPool.commonPool-worker-8
7 - ForkJoinPool.commonPool-worker-6
0 - ForkJoinPool.commonPool-worker-5
9 - ForkJoinPool.commonPool-worker-9
4 - ForkJoinPool.commonPool-worker-7
1 - ForkJoinPool.commonPool-worker-3
8 - ForkJoinPool.commonPool-worker-2
5 - ForkJoinPool.commonPool-worker-4
```

We see as expected in the sequential stream the main thread handles all prints, while in the parallel stream we see that main + 9 other threads executes the print statements

***

#### 10.3.2
***
*Increase the size of the integer array (from the 10 in the article) and see if there is a relation between number of cores on your computer and the number of workes in the ForkJoin*

```
Parallel - range: 20
6 - ForkJoinPool.commonPool-worker-1
7 - ForkJoinPool.commonPool-worker-5
9 - ForkJoinPool.commonPool-worker-6
8 - ForkJoinPool.commonPool-worker-3
15 - ForkJoinPool.commonPool-worker-5
16 - ForkJoinPool.commonPool-worker-7
4 - ForkJoinPool.commonPool-worker-11
5 - ForkJoinPool.commonPool-worker-8
10 - ForkJoinPool.commonPool-worker-11
13 - ForkJoinPool.commonPool-worker-8
14 - ForkJoinPool.commonPool-worker-7
18 - ForkJoinPool.commonPool-worker-3
17 - ForkJoinPool.commonPool-worker-4
11 - ForkJoinPool.commonPool-worker-1
3 - ForkJoinPool.commonPool-worker-5
12 - main
0 - ForkJoinPool.commonPool-worker-6
2 - ForkJoinPool.commonPool-worker-2
1 - ForkJoinPool.commonPool-worker-9
19 - ForkJoinPool.commonPool-worker-10
```

Yes we do see a correlation as no more than 12 threads are run, which correlates with the amount of cores on this computer

***

#### 10.3.3
***
*Change the example by adding a time consuming task (e.g. counting primes in a limited range or the example in the artcle). Report what you see when running the example*

We see no difference

***

## 10.4
*The solution to this exercise is just a short explanation in English - there is no code to develop and run.*

*Despite many superficial syntactical similarities between JavaStream and RxJava, the two concepts are fundamentally different. This exercise focus on some of these differences.*

*Consider the pseudo-code below (that does not compile and run). The source() provides english words and the sink() absorbs them. Note that sink() is a pseudo-operation that just absorbs the data it receives. Your explanations should focus on what happens in between the source() and sink()*

### Mandatory

#### 10.4.1
***
*Describe what happens when this code runs*

```java
source().filter(w -> w.length() > 5).sink()
```

- *as a JavaStream (e.g. the source is a file)*
- *as a RxJava statement where the source could be an input field where a user types strings*

**JavaStream**
This statement will create a pipeline which filters any w (assumed to be work) whose length is less than 5. The pipeline will execute immediately performing the filter on the data input, this is due to the `sink()` call (pull)

**RxJava**
The same as above but, this adds an a filter to an observable (`source()`) that removed any inputs that have a length less than 5. The pipeline will be executed once some data has been added to the `source()` (push)

***

#### 10.4.2
***
*Describe what happens when this code runs*

```java
source().filter(w -> w.length() > 5).sink()
source().filter(w -> w.length() > 10).sink()
```

- *as a JavaStream (e.g. the source is a file)*
- *as a RxJava statement where the source could be an input field where a user types strings*

Same as above but with the predicate have changed in one of the calls

***

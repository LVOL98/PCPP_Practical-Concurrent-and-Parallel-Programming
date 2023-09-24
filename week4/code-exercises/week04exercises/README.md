# Solutions Exercise 4

## 4.1
*We have repeatedly argued that regular (non thread-safe) Java collections are not adequate for use in concurrent programs. We have even seen how to discover interleavings that show that these collections are non thread-safe. However, how likely is it that those interleavings appear? Can they even occur? In this exercise, your task is to develop some tests that will trigger undesired interleavings when using a non thread-safe data structure in a concurrent environment.*

*We look into a set of collections implementing the interface ConcurrentIntegerSet (see the directory code-exercises/week04exercises/app/src/main/java/exercises04/). The interface defines the two most basic operations on sets: add and remove. Additionally, it defines a size method which is useful when writing tests. The file contains three different implementations of the interface:*

1. *ConcurrentIntegerSetBuggy, this class uses an underlying HashSet and binds the interface method calls directly to the equivalent operations in the HashSet.*
2. *ConcurrentIntegerSetSync, this is exactly as ConcurrentIntegerSetBuggy. You will modify so that it to fix the concurrency issues found by some tests you will develop.*
3. *ConcurrentIntegerSetLibrary, this class uses an underlying ConcurrentSkipListSet which is already thread-safe.*

*Note also that there is a skeleton for writing tests in the ConcurrentSetTest.java in the tests directory of the Gradle project for the exercises (app/src/test/java/exercises04/).*

*Before you start with the exercise, here are some (possibly) useful hints regarding testing concurrent programs:*

- *Remember that interleavings appear non-deterministically, so it might be helpful to run the test several times. This can easily be done in JUnit 5 with @RepeatedTest(). Depending on how you design your tests, it is possible that you need to run your tests more than 5000 times. However, no more than 10000 executions should be necessary.*
- *To cover as many interleavings as possible, it is useful to run the tests with increasing number of threads. Typically, slightly more threads than processors should suffice. For instance, in an 8 core machine, running tests with 16 threads should ensure that the execution of threads interleaves.*
- *To minimize the chance of threads executing sequentially, it is helpful to start all threads at the same time. You may CyclicBarrier to make sure that all threads start the execution (almost) at the same time. The same barrier may be used to wait until all threads have terminated their execution.*

***

### Madatory 

#### 4.1.1
***
*Implement a functional correctness test that finds concurrency errors in the add(Integer element) method in ConcurrentIntegerSetBuggy. Describe the interleaving that your test finds.*

*Note I: Remember that, by definition, sets do not have repeated elements. In fact, if, in a sequential test, you try to insert twice the same element, you will notice that the second insertion will no suceed and add will return false.*

*Note II: Remember that the execution of the add() method in HashSet is not atomic.*

*Hint I: Even if many threads try to add the same number, the size of the set must be at most 1. Think of an assertion related to the size of the set that would occur if the same element is added more than once.*

See [./app/arm/main/test/java/exercises04/ConcurrentSetTest.java](./app/arm/main/test/java/exercises04/ConcurrentSetTest.java) test named `exercise_4_1_1`

Let's take the critical section of the `add()` method, and let's write a bit of pseudo code to get an general understanding of what's happening

```java
public boolean add(Integer element) {
    return set.add(element);
}

// More detail (pseudo code)
public boolean add(Integer element) {
    var contains = collection.contains(element);    // 1

    if (!contains) {                                // 2
        collection.add(element);                    // 3
        reutrn true;                                // 4
    }

    return false;                                   // 5
}
```

It can be observed that addition into the set is not a single atomic operation, but a a more complex operation. It should also be noted that the implementation is even more complex that what is show, and the pseudo code above is only for demonstration purposes.

Now let's take an interleaving with two threads, t1 and t2, that leads to an element being inserted at least two times in the `collection`

```
t1(1) -> t1(2) -> t2(1) -> t2(2) -> t2(3) -> t2(4) -> t1(3) -> t1(4)
```

This interleaving of events can lead to an element being inserted two times, which was also demonstrated in the test case mentioned in the start

***

#### 4.1.2
***
*Implement a functional correctness test that finds concurrency errors in the remove(Integer element) method in ConcurrentIntegerSetBuggy. Describe the interleaving that your test finds.*

*Note: Remember that the execution of the remove() method in HashSet is not atomic.*

*Hint: The method size() may return values smaller 0 when executed concurrently. This fact should useful in thinking of an assertion related to the size of the set that would occur if an element is removed more than once.*

See [./app/arm/main/test/java/exercises04/ConcurrentSetTest.java](./app/arm/main/test/java/exercises04/ConcurrentSetTest.java) test named `exercise_4_1_2`

As with questoin 4.1.1 we first a pseudo version of the critical section of `remove()` and describe how an interleaving could lead to an error in the remove

```java
public boolean remove(Integer element) {
    return set.remove(element);
}

// More detail (pseudo code)
public boolean remove(Integer element) {
    var contains = collection.contains(element);    // 1

    if (contains) {                                 // 2
        // assumes no crash when element doesn't exists
        collection.remove(element);                 // 3
        size--;                                     // 4
        return true;                                // 5
    }

    return false;                                   // 6
}
```

Interleaving with two threads, t1 and t2

```
t2(1) -> t2(2) -> t1(1) -> t1(2) -> t1(3) -> t1(4) -> t1(5) -> t2(3) -> t2(4) -> t2(5)
```

This interleaving could lead to the failed test mentioned in the start of this exercise

***

#### 4.1.3
***
*In the class ConcurrentIntegerSetSync, implement fixes to the errors you found in the previous exercises. Run the tests again to increase your confidence that your updates fixed the problems. In addition, explain why your solution fixes the problems discovered by your tests.*

See [./app/arm/main/test/java/exercises04/ConcurrentIntegerSet.java](./app/arm/main/test/java/exercises04/ConcurrentIntegerSet.java). By utilizing Java's concurrent implementation of a set, we ensure that whenever any thread adds or removes any item from the set, mutual exclusion in relation to other set are ensured

***

#### 4.1.4
***
*Run your tests on the ConcurrentIntegerSetLibrary. Discuss the results. Ideally, you should find no errors—otherwise please submit a bug report to the maintainers of Java concurrency package :)*

As it can be observed by running the tests no errors occurred

***

#### 4.1.5
***
*Do a failure on your tests above prove that the tested collection is not thread-safe? Explain your answer.

Yes. If we can in a finite interleaving actions that lead to a concurrency error, we can prove that the collection is not thread safe. This is what we did with our tests, when a failure occurs in our tests, the test executed an finite interleaving of actions that lead to a concurrency failure

***

#### 4.1.6
***
*Does passing your tests above prove that the tested collection is thread-safe (when only using add() and remove())? Explain your answer.*

In most cases no. During our tests we can only perform a finite interleaving of events, but in a program like this there's almost an infinite interleaving of events, hence it is not possible to test every possible interleaving of event, thus we cannot prove that there exists no interleaving of actions that lead to a concurrency failure

Though there exists some cases where all possible interleavings can be executed, hence these programs can be proven as thread safe 

***

## 4.2
*In this exercise, we focus on testing whether a Semaphore implementation works according to its specification. In particular, we will test the Semaphore implementation in the file SemaphoreImp.java in app/src/main/java/exercises04/.*

*As specification for the semaphore, we use Herlihy’s description on how a semaphore class behaves (see Herlihy, Section 8.5, page 194). Concretely, we focus in this property of the specification, “Each semaphore has a capacity that is determined when the semaphore is initialized. Instead of allowing only one thread at a time into the critical section, a semaphore allows at most cthreads,where cis its capacity.”*

### Mandatory

#### 4.2.1
***
*Let capacity denote the final field capacity in SemaphoreImp. Then, the property above does not hold for SemaphoreImp. Your task is to provide an interleaving showing a counterexample of the property, and explain why the interleaving violates the property.*

*Note: If a thread successfully acquires the semaphore—i.e., it executes acquire() and does not block then we consider that it has entered its critical section.*

*Hint I: Consider only interleavings that involve method calls to acquire() and release(). Note that field accesses cannot occur in interleavings because all fields are defined as private.*

*Hint II: The operations executed by the main test thread—i.e., the thread that starts the threads for testing are also part of interleavings. You may use the main thread to execute method calls in the tested semaphore before starting the threads that try to enter the critical section.*

Take the following pseudo code:

```java
public class SemaphoreImp {
    ...

	public void acquire() throws InterruptedException { ... }   // 1
    public void release() { ... }                               // 2
}
```

Let's now look at an instance of `SemaphoreImp` with a `capacity` of 2, and have the following execution of 3 threads (note, the value of state is also included):

```
t1(2), state = -1 --> t2(1), state = 0 --> t1(1), state = 1 --> t3(1), state = 2
```

Though the field `state` never exceeds the `capacity` of 2, three threads are in the critical section of 1, because the state was changed to a negative value

***

#### 4.2.2
***
*Write a functional correctness test that can trigger the interleaving you describe in 1. Explain why your test triggers the interlaving.*

*Note: The note and hints in 1 also apply to this exercise.*

See [./app/arm/main/test/java/exercises04/SemaphoreImpTest.java](./app/arm/main/test/java/exercises04/SemaphoreImpTest.java) test named `exercise_4_2_2`

***

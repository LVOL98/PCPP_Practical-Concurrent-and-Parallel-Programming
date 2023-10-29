# Exercise 8, Solutions
Goal of the exercises
The exercises aim to give you practical experience on:
- Reasoning about correctness of concurrent objects using linearizability.
- Identifying and reasoning about linearization points in implementations of non-blocking data structures.
- Reasoning about non-blocking progress notions.

Syntax for concurrent executions, sequential executions and linearizations
For the answers in the exercises below, you may use the mathematical notation for histories in Herlihy, Chapter
3. However, to avoid getting into the details of the mathematical notation, you may use the following syntax to
provide concurrent executions, sequential executions and linearizations. We describe the syntax by example.
Concurrent executions Consider the concurrent execution at the top of slide 34 in lecture 8. The following
syntax represents the execution:
A: -| q.enq(x) |-------| p.enq(y) |---->
B: -----| q.deq(x) |------| p.enq(y) |->
The text in between | | denotes the method call. The left | denotes the point in time when the method was
invoked. The right | denotes the point int time when the response of the method call is received (in other words,
when the method call finished). The width of | | denotes the duration of the method call. On the left hand side,
A: and B: are thread names. In this case, the upper execution corresponds to thread A and the lower execution to
thread B. The dashed line represents real time.
Sequential execution In slide 34 (lecture 8), we provide a (possible) sequential execution for the concurrent
execution above. The sequential execution can be written using the following syntax:
<q.enq(x),p.enq(y),q.deq(x),p.deq(y)>
The symbol < denotes the beginning of the execution, and the symbol > denotes the end of the execution. In a
sequential execution, the sequence of method calls are listed in the (sequential) order of execution. Recall that
real-time is irrelevant for sequential executions. We are only interested on whether a method call happens before
another.
Linearizations Linearizations have the same syntax as sequential executions. Remember that the process of
finding a linearization involves setting linearization points and map them to a sequential execution. For instance,
the linearization of slide 37 (lecture 8) for the concurrent execution above is written as:
<q.enq(x),q.deq(x),p.enq(y),p.deq(y)>

## Exercise 8.1
*In this exercise, we look into several concurrent executions of a FIFO queue. Your task is to determine whether they are sequentially consistent or linearizable (according to the standard specification of a sequential FIFO queue).*

### Mandatory

#### 8.1.1
***
*Is this execution sequentially consistent? If so, provide a sequential execution that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not sequentially consistent.*

```
A: ---------------|q.enq(x)|--|q.enq(y)|->
B: ---|q.deq(x)|------------------------->
```

It's sequentially consistent as we can provide the following sequential execution

```
q.enq(x) --> q.enq(y) --> q.deq(x)
```

***

#### 8.1.2
***
*Is this execution (same as above) linearizable? If so, provide a linearization that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not linearizable.*

```
A: ---------------|q.enq(x)|--|q.enq(y)|->
B: ---|q.deq(x)|------------------------->
```

It's not linearizable as it has to respect real time ordering, hence the call to `q.deq(x)` happens before x has been enqueued

***

#### 8.1.3
***
*Is this execution linearizable? If so, provide a linearization that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not linearizable.*

```
A: ---|       q.enq(x)           |-->
B: ------| q.deq(x) |--------------->
```

It's linearizable as we can define two linearization points in the two overlapping executions which ensures a correct order, see the following

```
LP = Linearization point
A: ---| LP1      q.enq(x)           |-->
B: -----| q.deq(x) LP2 |--------------->
```

Here `LP1` < `LP2` (happens before)

***

#### 8.1.4
***
*Is this execution linearizable? If so, provide a linearization that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not linearizable.*

```
A: ----|q.enq(x)|-----|q.enq(y)|--->
B: --|  q.deq(y)                 |->
```

It's not linearizable as we cannot create a case where `q.enq(y) --> q.enq(x) --> q.deq(y)`, because no overlapping linearization points allows this ordering

***

## 8.2
*In this exercise, we look into the Treiber Stack. Your task is to reason about its correctness using linearization and testing.*

### Mandatory

#### 8.2.1
***
*Define linearization points for the push and pop methods in the Treiber Stack code provided in app/src/main/java/exercises08/LockFreeStack.java. Explain why those linearization points show that the implementation of the Treiber Stack is correct. For the explanation, follow a similar approach as we did in lecture 8 for the MS Queue (slides 44 and 45).*

A linearization point in a lock free implementation is the point where the method takes effect

In the case of `pop` the linearization point is when the `compareAndSet` succeeds at `PU4`

```java
public void push(T value) {
    Node<T> newHead = new Node<T>(value);           // PU1
    Node<T> oldHead;
    do {
        oldHead      = top.get();                   // PU2
        newHead.next = oldHead;                     // PU3
    } while (!top.compareAndSet(oldHead,newHead));  // PU4
    
}	    
```

In the case of `push` the linearization point is when the `compareAndSet` succeeds at `PO6`

```java
public T pop() {
    Node<T> newHead;                                // PO1
    Node<T> oldHead;                                // PO2
    do {
        oldHead = top.get();                        // PO3
        if(oldHead == null) { return null; }        // PO4
        newHead = oldHead.next;                     // PO5
    } while (!top.compareAndSet(oldHead,newHead));  // PO6
    
    return oldHead.value;
}   
```

***

#### 8.2.2
*Write a JUnit 5 functional correctness test for the push method of the Treiber Stack. Consider a stack of integers. The test must assert that after n threads push integers $x_1, x_2, . . . , x_n$, respectively, the total sum of the elements in the stack equals $\sum_{i = 1}^{n}{x_i}$. Write your test in the test skeleton file app/src/test/java/exercises08/TestLockFreeStack.java* 

See [./src/test/java/exercises08/TestLockFreeStack.java](./src/test/java/exercises08/TestLockFreeStack.java)

#### 8.2.3
***
*Write a JUnit 5 functional correctness test for the pop method of the Treiber Stack. As before, consider a stack of integers. Given a stack with n elements x1, x2, . . . , xn already pushed, the test must assert the following: after n threads pop one element yi each, the sum of popped elements equals the sum of elements originally in the stack $\sum_{i = 1}^{n}{x_i} = \sum_{i = 1}^{n}{y_i}$. Write your test in the test skeleton file app/src/test/java/exercises08/TestLockFreeStack.java*

See [./src/test/java/exercises08/TestLockFreeStack.java](./src/test/java/exercises08/TestLockFreeStack.java)

***

## 8.3
*In this exercise, we revisit the progress notions for non-blocking computation that we discussed in lecture 7.*

### Mandatory

#### 8.3.1
***
*Consider the reader-writer locks exercise from week 7. There are four methods included in this type of locks: writerTryLock, writerUnlock, readerTryLock and readerUnlock. State, for each method, whether they are wait-free, lock-free or obstruction-free and explain your answers.*

See [/workspaces/PCPP_Practical-Concurrent-and-Parallel-Programming/week7/code-exercises/week07exercises/app/src/main/java/exercises07/ReadWriteCASLock.java](/workspaces/PCPP_Practical-Concurrent-and-Parallel-Programming/week7/code-exercises/week07exercises/app/src/main/java/exercises07/ReadWriteCASLock.java)

Let's take it method by method

`writerTryLock` \
**Wait-free**  \
This implementation is wait-free because it finishes in a bounded number of steps, regardless of interference of other threads

`writerUnlock` \
**Wait-free** \
This implementation is wait-free because it finishes in a bounded number of steps, regardless of interference of other threads

`readerTryLock` \
**Lock-free** \
This implementation is lock-free because it ensures that some thread finishes in a bounded number of steps

`readerUnlock` \
**Lock-free** \
This implementation is lock-free because it ensures that some thread finishes in a bounded number of steps

***

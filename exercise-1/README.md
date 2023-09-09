# Solutions for exercises01.pdf

## 1.1

### Madatory

#### 1
    The main method creates a LongCounter object. Then it creates and starts two threads that run concurrently, and each increments the count field 10 million times by calling method increment.
    
    What output values do you get? Do you get the expected output, i.e., 20 million?

From a single test run we got: 19610281 instead of the 20 million (20000000).

TODO (language): This is as expected due to the code in LongCounter not being thread safe. Thereby, a `read-modify-write` `race-condition` can occur where both threads reads the same value x, they then both modify x with x = x + 1 and finally they both save the outcome of this calculation. Because both thread read the same value of x, and saved their modification to memory it looks like only x + 1 was performed

#### 2
    Reduce the counts value from 10 million to 100, recompile, and rerun the code. It is now likely that you get the expected result (200) in every run. 
    
    Explain how this could be. Is it guaranteed that the output is always 200?

It's likely that we will get 200 as the output, but we still can't guarantee it. The same `race condition` as in the previous question still exists, but given only 100 interation the chance if it happening is quite low.

#### 3
    The increment method in LongCounter uses the assignment

    count = count + 1;

    to add one to count. This could be expressed also as count += 1 or as count++.

    Do you think it would make any difference to use one of these forms instead? Why? Change the code and run it. Do you see any difference in the results for any of these alternatives?

No we don't believe that it would change the outcome of the program. This is due to the 3 forms of incrementing `count = count + 1`, `count += 1` and `count++` are all compiled into the same expression in the JVM (see below), and the 3 ways of writing it is just syntactic sugar. When running some small test using the 3 different methods of calculating `count + 1`, we observe the same `race condition`.

An abstract representation of the `count + 1` calculation

```java
var temp = value;
temp = value + 1;
return temp;
```

#### 4
    Set the value of counts back to 10 million. Use Java ReentrantLock to ensure that the output of the program equals 20 million. Explain why your solution is correct, and why no other output is possible.

    Note: In your explanation, please use the concepts and vocabulary introduced during the lecture, e.g., critical sections, interleavings, race conditions, mutual exclusion, etc.

    Note II: The notes above applies to all exercises asking you to explain the correctness of your solution.

By adding the keyword `synchronized` to the `increment` method of `LongCounter`, we can introduced a `ReentrantLock`. See below

```java
...

public synchronized void increment() {
    count = count + 1;
}

...
```

This marks the code inside the `incrment` function as a `critical section` that only 1 thread can execute at a time

Now whenever a thread wants to perform the increment method, a counter is incremented in the JVM and the current thread is set as holding the lock. Then it can perform the increment of count. Once done the JVM decerements the count and no longer associates the thread with the lock. Now the same or a new thread can lock the resource.

While all of this is happening, no other thread can get a lock on the count.

#### 5
    By using the ReetrantLock in the exercise above, you have defined a critical section. Does your critical section contain the least number of lines of code? If so, explain why. If not, fix it and explain why your new critical section contains the least number of lines of code.

    Hint: Recall that the critical section should only include the parts of the program that only one thread can execute at the same time.

TOOD: could it be made simpler with an AtomicInteger in counter?

Yes the `critical section` includes the least amount of lines. This is due to the `race condition` being a read-modify-write which occurs in the increment method. By making this sunchronized we ensure that only one thread can execute the critical section, at a time.

### 1.2

#### 1
    Write a program that creates a Printer object p, and then creates and starts two threads. Each thread must
    call p.print() forever. Note: You can easily run your program using the gradle project for the exercises
    by placing your code in the directory week01exercises/app/src/main/java/exercises01/
    (remember to add package exercises01; as the first line of your Java files).

    You will observe that, most of the time, your program print the dash and bar symbols alternate neatly as in
    -|-|-|-|-|-|-|. But occasionally two bars are printed in a row, or two dashes are printed in a row,
    creating small “weaving faults” like those shown below:

    -|-|-|-||--|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-||--|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-||--|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|

See `TestPrinter.java`

#### 2
    Describe and provide an interleaving where this happens.

Take the following sequence of actions
```bash
--|-|-|-|-|-|-|-|-|-|-|-|-|-|--||
```

The following java code

```java
public void print() {
    System.out.print("-");                                          // (0)
    try { Thread.sleep(50); } catch (InterruptedException exn) { }  // (1)
    System.out.print("|");                                          // (2)
}
```

With the two threads (t1, t2) and following interleavings (syntax taken from the concurrency note)

```
Normal:
..., t1(0), t1(1), t1(2), t2(0), t2(1), t2(2)

Error:
..., t2(0), t1(0), t1(1), t1(2), t2(1), t2(2)
```

Here some action delays thread 2's execution of t2(2) and t2(3) such that thread 1 starts executing out of order, and this would lead to the error shown in the example.

#### 3
    Use Java ReentrantLock to ensure that the program outputs the expected sequence -|-|-|-|....
    
    Compile and run the improved program to see whether it works. Explain why your solution is correct, and
    why it is not possible for incorrect patterns, such as in the output above, to appear.

TODO: is this actually the best solution?

By adding the `synchronized` keyowrd to the `print` method as below, the sequence will never overlap as shown in the previous question

```java
...

public synchronized void print() {
    System.out.print("-");
    try {
        Thread.sleep(50);
    } catch (InterruptedException exn) {
    }
    System.out.print("|");
}

...
```

As the print statements are now defined in a critical section, that only one thread can access, we can ensure that there will be no weaving faults in the output. This is because the weaving fault requires two threads to print the same character out of sync, which the synchronized keyword limits, as both prints statements and the sleep call are in the critical section.

### 1.3
    Imagine that due to the COVID-19 pandemic Tivoli decides to limit the number of visitors to 15000. To this end, you are asked to modify the code for the turnstiles we saw in the lecture. The file CounterThreads2Covid.java includes a new constant MAX_PEOPLE_COVID equal to 15000. However, the two threads simulate 20000 people entering to the park, so unfortunately some people will not get in :’(.

#### 1
    Modify the behaviour of the Turnstile thread class so that that exactly 15000 enter the park; no less no more. To this end, simply add a check before incrementing counter to make sure that there are less than 15000 people in the park. Note that the class does not use any type of locks. You must use ReentrantLock to ensure that the program outputs the correct value, 15000.

The code below shows the changes made, note that any part not changed is excluded

```java
...

public class Turnstile extends Thread {
    public void run() {
        for (int i = 0; i < PEOPLE; i++) {
            increment();
        }	    
    }
    
    synchronized void increment() {
        if (counter < MAX_PEOPLE_COVID) {
            counter++;
        }
    }
}

...
```

#### 2
    Explain why your solution is correct, and why it always output 15000.

TODO: actually don't know

### 1.4
    In Goetz chapter 1.1, three motivations for concurrency is given: resource utilization, fairness and convenience. In the note about concurrency there is an alternative characterization of the different motivations for concurrency (coined by the Norwegian computer scientist Kristen Nygaard):
    
    Inherent User interfaces and other kinds of input/output.
    
    Exploitation Hardware capable of simultaneously executing multiple streams of statements. A special (but important) case is communication and coordination of independent computers on the internet,

    Hidden Enabling several programs to share some resources in a manner where each can act as if they had sole ownership.

    Neither of the definitions is very precise, so for this exercise, there are many possible and acceptable answers

#### 1
    Compare the categories in the concurrency note (https://github.itu.dk/jst/PCPP2023-public/blob/main/week01/concurrencyNotes/concurrencyPCPP.pdf and Goetz, try to find some examples of systems which are included in the categories of Goetz, but not in those in the concurrency note, and vice versa (if possible - if not possible, argue why)

Goetz resource utilization encompasses the concurrency note's interacting with the environment and resource sharing

Goetz fairness also encompasses the concurrency note's interacting with the environment and resource sharing

Goetz convenience kinda has some parts of the concurrency note's hardware

#### 2
    Find examples of 3 systems in each of the categories in the Concurrency note which you have used yourself (as a programmer or user).

Hardware: Bachelor project with several threads on a mobile application, some for the UI, others for complex data processing

Interacting with the environemnt: well just using a computer would kinda be this case, right?

Resource sharing: spinning up windows service in order to make web application more efficient

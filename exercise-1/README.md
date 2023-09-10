# Solutions for exercises01.pdf

## 1.1
*Consider the code in the file TestLongCounterExperiments.java. Note that this file contains a class, LongCounter, used by two threads:*

```java
class LongCounter {
    private long count = 0;

    public void increment() {
        count = count + 1;
    }
    
    public long get() {
        return count;
    }
}
```

### Madatory

#### 1
*The main method creates a LongCounter object. Then it creates and starts two threads that run concurrently, and each increments the count field 10 million times by calling method increment.*
    
*What output values do you get? Do you get the expected output, i.e., 20 million?*

From a single test run we got: 19610281 instead of the expected 20 million (20000000).

<!-- This is as expected due to the code in LongCounter not being thread safe. Its increment is not atomic and can, thereby, lead to a `read-modify-write race-condition`. An interleaving of events can lead to an unexpected outcome, take the following sequence of actions with thread A and B

```
A reads count = 42
B reads count = 42
A modifies count = 42 + 1 = 43
B modifies count = 42 + 1 = 43
B writes count = 42 to memory
A writes count = 42 to memory
```

Here we would have expected that e.g. A executed and B executed hence the final state of count is 43, but due to the `race condition`, we get a unexpected result. -->

#### 2
*Reduce the counts value from 10 million to 100, recompile, and rerun the code. It is now likely that you get the expected result (200) in every run.*
    
*Explain how this could be. Is it guaranteed that the output is always 200?*

It's likely that we will get 200 as the output, but we still can't guarantee it. The same `read-modify-write race condition` as in the previous question still exists, but given only 100 interation the chance if it happening is quite low

#### 3
*The increment method in LongCounter uses the assignment*

`count = count + 1;`

*to add one to count. This could be expressed also as count += 1 or as count++.*

*Do you think it would make any difference to use one of these forms instead? Why? Change the code and run it. Do you see any difference in the results for any of these alternatives?*

No we don't believe that it would change the outcome of the program. This is due to the 3 forms of incrementing `count = count + 1`, `count += 1` and `count++` are all compiled into the same expression in the JVM (see below), and the 3 ways of writing it is just syntactic sugar. When running some small test using the 3 different methods of calculating `count + 1`, we observe the same `race condition`.

An abstract representation of the `count + 1` calculation

```java
var temp = value;
temp = value + 1;
return temp;
```

Since all 3 calculations resulted in the same sequence of expressions and since this sequence is not `atomic`, we don't expected to make any difference

#### 4
*Set the value of counts back to 10 million. Use Java ReentrantLock to ensure that the output of the program equals 20 million. Explain why your solution is correct, and why no other output is possible.*

*Note: In your explanation, please use the concepts and vocabulary introduced during the lecture, e.g., critical sections, interleavings, race conditions, mutual exclusion, etc.*

*Note II: The notes above applies to all exercises asking you to explain the correctness of your solution.*

By adding the following `ReentrantLock` to the `increment()` method of `LongCounter`, we can ensure that no `race condition` occur

```java
...

class LongCounter {
    private long count = 0;    
    private Lock lock = new ReentrantLock();

    public void increment() {
        lock.lock();

        try {
            count = count + 1;
        } finally {
            lock.unlock();
        }
    }

    public long get() {
        return count;
    }
}

...
```

This marks the code inside the `incrment` function as a `critical section` that only 1 thread can execute at a time

Now only one thread at a time can enter the critical section, the `increment()` method, and thereby the only possbiel output of the program is the expected output

#### 5
    By using the ReetrantLock in the exercise above, you have defined a critical section. Does your critical section contain the least number of lines of code? If so, explain why. If not, fix it and explain why your new critical section contains the least number of lines of code.

    Hint: Recall that the critical section should only include the parts of the program that only one thread can execute at the same time.

If the question relates to the lines of code closer to the compiler (see 1.1.3), then yes. This is due to the `race condition` being a `read-modify-write` which occurs in the increment method. By making this sunchronized we ensure that only one thread can execute the critical section, at a time.

Otherwise if it's relating to the lines of code used to create the lcok, then less lines could be used if an `AtomicLong` was used instead. See the example below

```java
public class LongCounter {
    private AtomicLong count = new AtomicLong(0);

    public void increment() {
        count.incrementAndGet();
    }

    public long get() {
        return count.get();
    }
}
```

### 1.2

#### 1
*Write a program that creates a Printer object p, and then creates and starts two threads. Each thread must call p.print() forever. Note: You can easily run your program using the gradle project for the exercises by placing your code in the directory week01exercises/app/src/main/java/exercises01/ (remember to add package exercises01; as the first line of your Java files).*

*You will observe that, most of the time, your program print the dash and bar symbols alternate neatly as in -|-|-|-|-|-|-|. But occasionally two bars are printed in a row, or two dashes are printed in a row, creating small “weaving faults” like those shown below:*

```
    -|-|-|-||--|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-||--|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-||--|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
    -|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|
```

See `TestPrinter.java`

#### 2
*Describe and provide an interleaving where this happens.*

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
*Use Java ReentrantLock to ensure that the program outputs the expected sequence -|-|-|-|....*
    
*Compile and run the improved program to see whether it works. Explain why your solution is correct, and why it is not possible for incorrect patterns, such as in the output above, to appear.*

TODO: is this actually the best solution?

By adding the `ReentrantLock` as follows, we can ensure the program prints the expected output

```java
...

class Printer {
    private Lock lock = new ReentrantLock();

    public void print() {
        lock.lock();

        try {
            System.out.print("-");
            try { Thread.sleep(50); } catch (InterruptedException exn) { }
            System.out.print("|");
        } finally {
            lock.unlock();
        }
    }
}

...
```

As the print statements are now defined in a critical section, that only one thread can access, we can ensure that there will be no weaving faults in the output. This is because the weaving fault requires two threads to print the same character out of sync, which the `ReentrantLock` keyword limits, as both prints statements and the sleep call are in the critical section.

### 1.3
*Imagine that due to the COVID-19 pandemic Tivoli decides to limit the number of visitors to 15000. To this end, you are asked to modify the code for the turnstiles we saw in the lecture. The file CounterThreads2Covid.java includes a new constant MAX_PEOPLE_COVID equal to 15000. However, the two threads simulate 20000 people entering to the park, so unfortunately some people will not get in :’(.*

#### 1
*Modify the behaviour of the Turnstile thread class so that that exactly 15000 enter the park; no less no more. To this end, simply add a check before incrementing counter to make sure that there are less than 15000 people in the park. Note that the class does not use any type of locks. You must use ReentrantLock to ensure that the program outputs the correct value, 15000.*

The code below shows the changes made

```java
...

public class Turnstile extends Thread {
    private Lock lock = new ReentrantLock();

    public void run() {
        for (int i = 0; i < PEOPLE; i++) {
            if (counter < MAX_PEOPLE_COVID) {
                lock.lock();

                try {
                    counter++;
                } finally {
                    lock.unlock();
                }
            }
        }	    
    }
}

...
```

#### 2
*Explain why your solution is correct, and why it always output 15000.*

The `critical section` of this program is the `run()` method, and more specifically the increment of the `count`variable. By wrapping that in a `critical section`, only one thread can modify it at a time, hence no `race condition` can occur that could lead to an increment above the expected amount

### 1.4
*In Goetz chapter 1.1, three motivations for concurrency is given: resource utilization, fairness and convenience. In the note about concurrency there is an alternative characterization of the different motivations for concurrency (coined by the Norwegian computer scientist Kristen Nygaard):*
    
*Inherent User interfaces and other kinds of input/output.*
    
*Exploitation Hardware capable of simultaneously executing multiple streams of statements. A special (but important) case is communication and coordination of independent computers on the internet,* 

*Hidden Enabling several programs to share some resources in a manner where each can act as if they had sole ownership.*

*Neither of the definitions is very precise, so for this exercise, there are many possible and acceptable answers*

#### 1
*Compare the categories in the concurrency note (https://github.itu.dk/jst/PCPP2023-public/blob/main/week01/concurrencyNotes/concurrencyPCPP.pdf and Goetz, try to find some examples of systems which are included in the categories of Goetz, but not in those in the concurrency note, and vice versa (if possible - if not possible, argue why)*

Let's go through Goetz's definitions and see how they relate to the concurrency note

**Resource utilization**: From time to time programs have to wait for external operations, e.g. input output. While waiting, it's more efficient to let another program run. This definition encompasses the `Inherent` category, and they state almost the same. In order to have this category, the system needs to be able to share resources as stated in the `hidden` category

**Fairness**: Multiple programs or users is using the computer. They shuold share the computers resources instead of each program running from start to finish, before the next start. No category from the concurrency note is similar to this category

**Convenience**: Write simple programs that have one task, rather than writing one large program handling all the tasks. While there's no direct category in the concurreny note that relates to this category, the `Exploitation` category is another benefit of concurrent programming. While `Convenience` promotes separation of concern the `Exploitation` category is more realted to optimization

One example of a system that defined by Goetz `Fairness` category and no category in the concurrency note is the reader-writer problem. Say that a program allows for several threads to read a shared value at any time, but while writing to said value only 1 thread can access the value. Without going into too much detail, a case could occur where infinite reader threads are given access, while write threads have to wait indefinetly. The `Fairness` category says that the system must be fair, and the writer threads cannot be starved like this and have to eventually gain access to the variable

One point that's covered by the concurreny note but not Goetz is the question of optimization. Here the concurrency notes `Exploitation` definition states that certain expensive computation can be made significantly faster if run in parallel. Such an argument doesn't exists in Goetz categories

#### 2
*Find examples of 3 systems in each of the categories in the Concurrency note which you have used yourself (as a programmer or user).*

- Inherent - User interfaces and other kinds of input/output.
    - Video games. There's a very elobrate UI running real time while calculations are being done in the background.
- Exploitation - Hardware capable of simultaneously executing multiple streams of statements, a special (but important) case is communication and coordination of independent computers on the internet.
    - Many web applications uses load balancers to achieve this e.g ALB. It allows web requests from users to be dynamically routed to set of target VMs for processing.
- Hidden - Enabling several programs to share some resources in a manner where each can act as if they had sole ownership.
    - Using a computer that are running several programs concurrently (listening to a Twitch stream while running Visual Studio Code)
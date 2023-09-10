# Exercises Week 2

## Exercise 2.1
*Consider the Readers and Writers problem we saw in class. As a reminder, here is the specification of the problem:* \
*• Several reader and writer threads want to access a shared resource.* \
*• Many readers may access the resource at the same time as long as there are no writers.* \
*• At most one writer may access the resource if and only if there are no readers.*

### Mandatory

#### 1
*Use Java Intrinsic Locks (i.e., synchronized) to implement a monitor ensuring that the access to the shared resource by reader and writer threads is according to the specification above. You may use the code in the lectures and Chapter 8 of Herlihy as inspiration, but do not feel obliged copy that structure.*

See [./app/src/main/java/exercises02/ReadWriteMonitor.java](/app/src/main/java/exercise02/ReadWriteMonitor.java)

#### 2
*Is your solution fair towards writer threads? In other words, does your solution ensure that if a writer thread wants to write, then it will eventually do so? If so, explain why. If not, modify part 1. so that your implementation satisfies this fairness requirement, and then explain why your new solution satisfies the requirement.*

The new solultions implements an integer variable called writerQueue. This is used whenever a writer attempts to obtain the lock in the writeLock method. The queue is increased just before the condition and is decreased again after the isWriting boolean is set to true.

In the readLock method the thread awaits the condition whenever the isWriting is true or the writerQueue is above zero.

In theory this does not quarantee that the writer is eventually able to write because it needs to obtain the internal lock in order to register itself in the queue. And we do not control which thread obtains the lock first. However this is as close as we can get.

## Exercise 2.2
*Consider the lecture’s example in file TestMutableInteger.java, which contains this definition of class MutableInteger:*

```java
// WARNING: Not ready for usage by concurrent programs
class MutableInteger {
    private int value = 0;

    public void set(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }
}
```

*For instance, as mentioned in Goetz, this class cannot be used to reliably communicate an integer from one thread to another, as attempted here:*

```java
final MutableInteger mi = new MutableInteger();
Thread t = new Thread(() -> {
    while (mi.get() == 0) // Loop while zero
    {/* Do nothing*/ }
    System.out.println("I completed, mi = " + mi.get());
});
t.start();
try { Thread.sleep(500); } catch (InterruptedException e) {
    e.printStackTrace(); }
mi.set(42);
System.out.println("mi set to 42, waiting for thread ...");
try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
System.out.println("Thread t completed, and so does main");
```

### Mandatory

#### 1
*Execute the example as is. Do you observe the "main" thread’s write to mi.value remains invisible to the t thread, so that it loops forever? Independently of your observation, is it possible that the program loops forever? Explain your answer.*

Yes as stated in the explenation of the tasks, it is possible that thread t loops forever. This is due to the initial value of mi is 0, and the `mi.set(42)` never becomes visible to thread t. Hence the condition in the whlie loop will always be false and the while loop will never terminate

#### 2
*Use Java Intrinsic Locks (synchronized) on the methods of the MutableInteger to ensure that thread t always terminates. Explain why your solution prevents thread t from running forever.*

When modifying the `set()` and `get()` methods with the synchronized keyword (Java Intrinsic Lock). Besides ensuring mutual exclusion, what's important here is, that the visibility of changes between the two threads are ensured. Now the change made with the `mi.set(42)` is made visible to all threads, hence the program will always terminate

#### 3
*Would thread t always terminate if get() is not defined as synchronized? Explain your answer*

If `get()` is not synhcronized then we cannot guarantee that thread t will terminate. This is again due to the visiblity guarantee provided by the synchronized keyword. If `get()` is not synchronized the JMM might cache the value of get, since its spinning asking for `mi.get()` and hence why `get()` needs to be synchronized

#### 4
*Remove all the locks in the program, and define value in MutableInteger as a volatile variable. Does thread t always terminate in this case? Explain your answer*

Yes the program terminates. Again the visibility guarantee of the volatile keyword is enough, as the while loop is spinning asking for `mi.get()`, hence that if the action `mi.set(42)` eventually becomes visible to thread t, it will terminate

## Exercise 2.3

### Mandatory

#### 1
*Execute the program several times. Show the results you get. Are there any race conditions?*

3 excutions is shown below

```bash
Sum is 1576535.000000 and should be 2000000.000000
Sum is 1813496.000000 and should be 2000000.000000
Sum is 1477661.000000 and should be 2000000.000000
```

Given that we got different results in all three runs, we expect some kind of race condition to be present

#### 2
*Explain why race conditions appear when t1 and t2 use the Mystery object. Hint: Consider (a) what it means for an instance method to be synchronized, and (b) what it means for a static method to be synchronized*

The two different methods synchronize on two different things. Take `public static synchronized addStatic(double x)`, due to the use of both `static` and `synchronized`, this method will synchronize on the class object itself, while `public synchronized addInstance(double x)` is missing the `static` keyword and does then synchronize on the class object, but the specific instance of the class 

Hence the two methods actually uses the different locks and are therefore, does not synchronie between one another

#### 3
*Implement a new version of the class Mystery so that the execution of t1 and t2 does not produce race conditions, without changing the modifiers of the field and methods in the Mystery class. That is, you should not make any static field into an instance field (or vice versa), and you should not make any static method into an instance method (or vice versa).*

*Explain why your new implementation does not have race conditions.*

If we cannot change the modifiers, we have to introduce a lock. Since we need to ensure synchronization between both the class object and the class instance the lock has to be static, such that the same lock is available to both the class object and class instance. See the follwoing modifications to the `Mysetery` class below

```java
class Mystery {
    private static double sum = 0;
	private static Lock lock = new ReentrantLock();

    public static synchronized void addStatic(double x) {
		lock.lock();

		try {
			sum += x;
		} finally {
			lock.unlock();
		}
    }

    public synchronized void addInstance(double x) {
		lock.lock();

		try {
			sum += x;
		} finally {
			lock.unlock();
		}
    }

    public static synchronized double sum() {
		lock.lock();

		try {
			return sum;
		} finally {
			lock.unlock();
		}
    }
}
```

Now the same lock is used for both class instance- and class object access, which in turns means that we have achieved synchronization between `addInstance` and `addStatic` thereby eliminating the race condition

#### 4
*Note that the method sum() also uses an intrinsic lock. Is the use of this intrinsic lock on sum() necessary for this program? In other words, would there be race conditions if you remove the modifier synchronized from sum() (assuming that you have fixed the race conditions in 3.)?*

Removing the lock from the sum method could lead to a race condition. While `sum()` is in the process of reading the value of `sum` another call to either `addInstance()` or `addStatic()` could interleave in such a way that `sum()` returns a stale value. Hence `sum()` must also use the lock

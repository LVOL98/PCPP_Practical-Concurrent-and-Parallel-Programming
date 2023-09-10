# Exercises Week 2

## Exercise 2.1
    Consider the Readers and Writers problem we saw in class. As a reminder, here is the specification of the problem:

    • Several reader and writer threads want to access a shared resource.
    • Many readers may access the resource at the same time as long as there are no writers.
    • At most one writer may access the resource if and only if there are no readers.

### Mandatory

#### 1
    Use Java Intrinsic Locks (i.e., synchronized) to implement a monitor ensuring that the access to the shared resource by reader and writer threads is according to the specification above. You may use the code in the lectures and Chapter 8 of Herlihy as inspiration, but do not feel obliged copy that structure.

See [/app/src/main/java/exercises02/FifoReadWriteMonitor.java](/app/src/main/java/exercise02/FifoReadWriteMonitor.java)

#### 2
    Is your solution fair towards writer threads? In other words, does your solution ensure that if a writer thread wants to write, then it will eventually do so? If so, explain why. If not, modify part 1. so that your implementation satisfies this fairness requirement, and then explain why your new solution satisfies the requirement.

Given a single write thread that requests access, then the solution is fair in regards to starvation. As once the write thread requests access, no more read access is given, hence the write thread will eventually obtain access. If there's more than one write thread, and they keep coming indefinetly, we cannot guarantee that one or more write thread suffer from starvation

## Exercise 2.2
    Consider the lecture’s example in file TestMutableInteger.java, which contains this definition of class MutableInteger:

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

For instance, as mentioned in Goetz, this class cannot be used to reliably communicate an integer from one thread
to another, as attempted here:

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
    Execute the example as is. Do you observe the "main" thread’s write to mi.value remains invisible to the t thread, so that it loops forever? Independently of your observation, is it possible that the program loops forever? Explain your answer.

Yes as stated in the explenation of the tasks, it is possible that thread t loops forever. This is due to the initial value of mi is 0, and the `mi.set(42)` never becomes visible to thread t. Hence the while loops will forever be false in this keep, and thread t will never terminate

#### 2
    Use Java Intrinsic Locks (synchronized) on the methods of the MutableInteger to ensure that thread t always terminates. Explain why your solution prevents thread t from running forever.

When modifying the `set()` and `get()` methods with the synchronized keyword (Java Intrinsic Lock), we ensure that both methods ensures mutual exclusion when executing the methods. Besides this, the synchronized keyword also ensure that the result of the execution is visible to all other threads, such that no changes can be hidden. Now the modification to `mi.set(42);` will always be visible to all other threads, hence the program will always terminate.

#### 3
    Would thread t always terminate if get() is not defined as synchronized? Explain your answer

The method `get` needs to use the synchronized keyword, or an infinite while loop can occur. It's again the visiblity guarantee we need, in order fir the call to `mi.get()` to be synchronized with the latest changes to `mi`


#### 4
    Remove all the locks in the program, and define value in MutableInteger as a volatile variable. Does thread t always terminate in this case? Explain your answer

TODO: yes, as the volatile keyword guarantees visibility, but doesn't guarantee mutula exclusion, which isn't needed in this specific case

## Exercise 2.3

### Mandatory

#### 1
    Execute the program several times. Show the results you get. Are there any race conditions?

3 excutions is shown below

```bash
Sum is 1576535.000000 and should be 2000000.000000
Sum is 1813496.000000 and should be 2000000.000000
Sum is 1477661.000000 and should be 2000000.000000
```

Given that we got different results in all three runs, we expect some kind of race condition to be present

#### 2
 Explain why race conditions appear when t1 and t2 use the Mystery object. Hint: Consider (a) what it means for an instance method to be synchronized, and (b) what it means for a static method to be synchronized

The two different methods synchronizes on two different things. Take `public static synchronized addStatic(double x)`, due to the use of both `static` and `synchronized`, this method will synchronize on the class object itself. Now `public synchronized addInstance(double x)` is missing the `static` keyword and does then synchronize on the instance of the class

Hence the two methods actually uses the different locks and are therefore, not synchronie between one another

#### 3
    Implement a new version of the class Mystery so that the execution of t1 and t2 does not produce race conditions, without changing the modifiers of the field and methods in the Mystery class. That is, you should not make any static field into an instance field (or vice versa), and you should not make any static method into an instance method (or vice versa).

    Explain why your new implementation does not have race conditions.

If we cannot change the modifiers, we have to introduce a lock. Now since we have to instances of the `Mystery` class, and they both operates on different parts of the class, the lock has to be static, in order to be accesible by all instances of the class, and on all levels of the class (on the class object itself and all instances). See the follwoing modifications to the `Mysetery` class below

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

Now the same lock os used for both instance- and class object access, which in turns means that we have achieved synchronization of between `addInstance` and `addStatic` thereby eliminating the race condition

#### 4
    Note that the method sum() also uses an intrinsic lock. Is the use of this intrinsic lock on sum() necessary for this program? In other words, would there be race conditions if you remove the modifier synchronized from sum() (assuming that you have fixed the race conditions in 3.)?

Removing the lock from the sum method could lead to a race condition. Here a method, even when synchronized properly, could modify a value while the method `sum` is reading it, thereby leading it to reutnr a stale value.

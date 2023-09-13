# Solutions, Exercise 3

## Exercise 3.1
*A Bounded Buffer is a data structure that can only hold a fixed set of elements. In this exercise, you must implement such a data structure which allow elements to be inserted into the buffer by a number of producer threads, and taken by a number of consumer threads.*

*The specification of the bounded buffer is as follows:*

- *If a producer thread tries to insert an element into and the buffer is full, the thread must be blocked until an element is taken by another thread.*
- *If a consumer thread tries to take an element from and the buffer is empty, the thread must be blocked until an element is inserted.*

*Note that this is an instance of the producer-consumer problem we discussed in the lecture.*

*In this exercise, your task is to build a simple version of a Bounded Buffer. Section 5.3 in Goetz talks about how to use BlockingQueue. Also, java.util.concurrent.BlockingQueue class implements a bounded buffer as described above.*

*Your bounded buffer must implement the interface BoundedBufferInterface.java (see exercises code folder):*

```java
interface BoundedBufferInteface<T> {
    public T take() throws Exception;
    public void insert(T elem) throws Exception;
}
```

*Of course, your class also must include a constructor which takes as parameter the size of the buffer.*

*Note that the methods in the interface allow for throwing Exception. We do this to not impose constraints on the implementation of the methods. In your implementation, please write the concrete exception your code throws, if any.*

*The buffer you design, must follow a FIFO queue policy for inserting and taking elements. Regarding the state of your class, you are only allowed to use non thread-safe collections from the Java library that implement the Queue interface, such as LinkedList<T>. This may save some time in the implementation, but do not feel obliged to use these libraries. It is also allowed to use primitive Java arrays. In summary, any collections from the Java library specifically designed for concurrent access are not allowed in this exercise.*

### Mandatory

#### 1
*Implement a class BoundedBuffer<T> as described above using only Java Semaphore for synchronization— i.e., Java Lock or intrinsic locks (synchronized) cannot be used.*

See [./app/src/main/java/exercises03/BoundedBufferTest.java](./app/src/main/java/exercises03/BoundedBufferTest.java)

#### 2
*Explain why your implementation of BoundedBuffer<T> is thread-safe. Hint: Recall our definition of thread-safe class, and the elements to identify/consider in analyzing thread-safe classes (see slides).*

In order to ensure that the class is thread safe we need a happens-before relationship. Specifically in this program we need to make sure that all modification to the buffer has a happens-before relationship, such that no race condition can occur.

Here the important part is the calls to `push()` and `pop()` because they modify buffer. Though the calls don't show it, there's som logic hidden behind the calls, hence why they have a lock associated with their calls. Hence the critical section is protected by a happens before relationship, and this part of the class is thread safe

Further two Java `Semaphores` is used to control the access to the buffer, ensuring that either a consumer (take) or producer (insert) only modifies a valid queue, e.g. `take()` is not called on an empty queue. While this doesn't necessarily ensuring thread safety, it is still necessary to ensure correct functionality of the program

#### 3
*Is it possible to implement BoundedBuffer<T> using Barriers? Explain your answer.*

yes I believe so

## Exercise 3.2

### Madatory
*Consider a Person class with attributes: id (long), name (String), zip (int) and address (String). The Person class has the following functionality:*

- *It must be possible to change zip and address together.*
- *It is not necessary to be able to change name—but it is not forbidden.*
- *The id cannot be changed.*
- *It must be possible to get the values of all fields.*
- *There must be a constructor for Person that takes no parameters. When calling this constructor, each new instance of Person gets an id one higher than the previously created person. In case the constructor is used to create the first instance of Person, then the id for that object is set to 0.*
- *There must be a constructor for Person that takes as parameter the initial id value from which future ids are generated. In case the constructor is used to create the first instance of Person, the initial parameter must be used. For subsequent instances, the parameter must be ignored and the value of the previously created person must be used (as stated in the previous requirement).*

#### 1
*Implement a thread-safe version of Person using Java intrinsic locks (synchronized). Hint: The Person class may include more attributes than those stated above; including static attributes.*

#### 2
*Explain why your implementation of the Person constructor is thread-safe, and why subsequent accesses to a created object will never refer to partially created objects.*

#### 3
*Implement a main thread that starting several threads the create and use instances of the Person class.*

#### 4
*Assuming that you did not find any errors when running 3. Is your experiment in 3 sufficient to prove that your implementation is thread-safe?*

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package exercises03;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    private static long t1Result = 0;
    private static long t2Result = 0;

    private synchronized void incrementT1Result(long value) {
        t1Result += value;
    }

    private synchronized void incrementT2Result(long value) {
        t2Result += value;
    }

    private synchronized void reset() {
        t1Result = 0;
        t2Result = 0;
    }

    @Test
    public void PersonId_TestValueConstructor() {
        var person1 = new Person(5);
        var person2 = new Person();
        var person3 = new Person();

        assertEquals("", 5, person1.getId());
        assertEquals("", 6, person2.getId());
        assertEquals("", 7, person3.getId());
    }

    @Test
    public void PersonId_NonValueConstructor() {
        var person1 = new Person();
        var person2 = new Person();
        var person3 = new Person();

        assertEquals("", 0, person1.getId());
        assertEquals("", 1, person2.getId());
        assertEquals("", 2, person3.getId());
    }

    @Test
    public void PersonId_Concurrent_NonValueConstructorTest() {
        long testCount = 1000;
        long testCountSplit = testCount / 2;
        long expectedResult = 0;
        for (var i = 0; i < testCount; i++) {
            expectedResult += i;
        }

        Thread t1 = new Thread(() -> {
            for (var i = 0; i < testCountSplit; i++) {
                var person = new Person();

                incrementT1Result(person.getId());
            }
        });
        Thread t2 = new Thread(() -> {
            for (var i = 0; i < testCountSplit; i++) {
                var person = new Person();

                incrementT2Result(person.getId());
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
        }

        assertEquals("Sum of person ids matches", expectedResult, t1Result + t2Result);
    }

    @Test
    public void PersonId_Concurrent_ValueConstructorTest() {
        long startIndex = 10;
        long testCount = 6 + startIndex;
        long testCountSplit = testCount / 2;
        long expectedResult = 0;
        for (var i = 0; i < testCount; i++) {
            expectedResult += i + startIndex;
        }

        Thread t1 = new Thread(() -> {
            for (var i = 0; i < testCountSplit; i++) {
                var person = new Person(startIndex);

                incrementT1Result(person.getId());
            }
        });
        Thread t2 = new Thread(() -> {
            for (var i = 0; i < testCountSplit; i++) {
                var person = new Person(startIndex);

                incrementT2Result(person.getId());
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
        }

        assertEquals("Sum of person ids matches", expectedResult, t1Result + t2Result);
    }
}

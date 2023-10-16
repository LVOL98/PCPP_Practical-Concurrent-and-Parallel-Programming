// For week 10
// sestoft@itu.dk * 2014-11-12, 2015-11-03
// raup@itu.dk * 2021-10-08

// Implementation of CAS using a lock for describing its meaning, and
// implementation of AtomicInteger style operations in terms of CAS.
package lecture07;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntToDoubleFunction;

public class TestCasAtomicInteger {
    public static void main(String[] args) {
		testMyAtomicInteger();
		// timeSequentialGetAndAdd();
		// timeParallelGetAndAdd();
    }

    private static void testMyAtomicInteger() {
		MyAtomicInteger mai = new MyAtomicInteger();
		assert mai.compareAndSwap(72,42) != 72 : "Swapping succeeded, it shouldn't have";
		assert mai.compareAndSwap(0,42)  == 0  : "Swapping failed, it should be 0";
		assert mai.get() == 42                 : "Get failed, it should be 42";
		assert mai.compareAndSet(42,72)        : "Set failed, and it shouldn't have";
		assert !mai.compareAndSet(42,0)        : "Set succeeded, and it shouldn't have";	
		assert mai.get() == 72                 : "Get failed, it should be 42";
		assert mai.addAndGet(1) == 73          : "Addition failed, it should be 73";
		assert mai.getAndAdd(1) == 73          : "Get failed, it should be 79";
    }

    private static void timeSequentialGetAndAdd() {
		SystemInfo();
		final int count = 1_000_000;
		// This is so fast, 0.4 ns per iteration, that some kind of lock
		// coarsening or lock elision must be done in the JVM JIT.
		System.out.println(Mark7("MyAtomicInteger", (int i) -> { 
					final MyAtomicInteger ai = new MyAtomicInteger();
					int res = 0;
					for (int j=0; j<count; j++)
						res += ai.addAndGet(j);
					return res;
				}));
		System.out.println(Mark7("AtomicInteger", (int i) -> { 
					final AtomicInteger ai = new AtomicInteger();
					int res = 0;
					for (int j=0; j<count; j++)
						res += ai.addAndGet(j);
					return res;
				}));
    }

    private static void timeParallelGetAndAdd() {
		SystemInfo();
		final int count = 10_000_000, threadCount = 1;
		final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
			stopBarrier = startBarrier;
		{
			final MyAtomicInteger ai = new MyAtomicInteger();
			for (int t=0; t<threadCount; t++) {
				new Thread(() -> { 
						try { startBarrier.await(); } catch (Exception exn) { }
						for (int p=0; p<count; p++) 
							ai.addAndGet(p);
						try { stopBarrier.await(); } catch (Exception exn) { }
				}).start();
			}
			try { startBarrier.await(); } catch (Exception exn) { }
			Timer t = new Timer();
			try { stopBarrier.await(); } catch (Exception exn) { }
			double time = t.check() * 1e6;
			System.out.printf("MyAtomicInteger sum = %d; time = %10.2f us; per op = %6.1f ns %n", 
							  ai.get(), time, time * 1000.0 / count / threadCount);
		}
		{
			final AtomicInteger ai = new AtomicInteger();
			for (int t=0; t<threadCount; t++) {
				new Thread(() -> { 
						try { startBarrier.await(); } catch (Exception exn) { }
						for (int p=0; p<count; p++) 
							ai.addAndGet(p);
						try { stopBarrier.await(); } catch (Exception exn) { }
				}).start();
			}
			try { startBarrier.await(); } catch (Exception exn) { }
			Timer t = new Timer();
			try { stopBarrier.await(); } catch (Exception exn) { }
			double time = t.check() * 1e6;
			System.out.printf("AtomicInteger   sum = %d; time = %10.2f us; per op = %6.1f ns %n", 
							  ai.get(), time, time * 1000.0 / count / threadCount);
		}
    }

    public static double Mark7(String msg, IntToDoubleFunction f) {
		int n = 10, count = 1, totalCount = 0;
		double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
		do { 
			count *= 2;
			st = sst = 0.0;
			for (int j=0; j<n; j++) {
				Timer t = new Timer();
				for (int i=0; i<count; i++) 
					dummy += f.applyAsDouble(i);
				runningTime = t.check();
				double time = runningTime * 1e9 / count;
				st += time; 
				sst += time * time;
				totalCount += count;
			}
		} while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
		double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
		System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
		return dummy / totalCount;
    }

    public static void SystemInfo() {
		System.out.printf("# OS:   %s; %s; %s%n", 
						  System.getProperty("os.name"), 
						  System.getProperty("os.version"), 
						  System.getProperty("os.arch"));
		System.out.printf("# JVM:  %s; %s%n", 
						  System.getProperty("java.vendor"), 
						  System.getProperty("java.version"));
		// The processor identifier works only on MS Windows:
		System.out.printf("# CPU:  %s; %d \"cores\"%n", 
						  System.getenv("PROCESSOR_IDENTIFIER"),
						  Runtime.getRuntime().availableProcessors());
		java.util.Date now = new java.util.Date();
		System.out.printf("# Date: %s%n", 
						  new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
    }
}

// Model implementation of an AtomicInteger
class MyAtomicInteger {
    private int value;    // Visibility ensured by locking

    // Model implementation of compareAndSwap to illustrate its meaning.
    // In practice, compareAndSwap is not implemented using locks; it is
	// compiled to a machine instruction such as CMPXCHG
    public synchronized int compareAndSwap(int oldValue, int newValue) {
		int valueInRegister = this.value;
		if (this.value == oldValue)
			this.value = newValue;
		return valueInRegister;
    }
    
    public synchronized boolean compareAndSet(int oldValue, int newValue) {
		return oldValue == this.compareAndSwap(oldValue,newValue);
    }

    public synchronized int get() { 
		return this.value;
    }

    public int addAndGet(int delta) {
		int oldValue, newValue;
		do {
			oldValue = get();
			newValue = oldValue + delta;
		} while (!compareAndSet(oldValue, newValue));
		return newValue;
    }

    public int getAndAdd(int delta) {
		int oldValue, newValue;
		do {
			oldValue = get();
			newValue = oldValue + delta;
		} while (!compareAndSet(oldValue, newValue));
		return oldValue;
    }

    public int incrementAndGet() {
		return addAndGet(1);
    }

    public int decrementAndGet() {
		return addAndGet(-1);
    }

    public int getAndSet(int newValue) {
		int oldValue;
		do { 
			oldValue = get();
		} while (!compareAndSet(oldValue, newValue));
		return oldValue;
    }
}


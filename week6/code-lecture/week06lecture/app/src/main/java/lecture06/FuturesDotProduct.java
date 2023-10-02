package lecture06;

// raup@itu.dk 06/10/2022

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class FuturesDotProduct {

    public static void main(String[] args) {
	new FuturesDotProduct();
    }

    public FuturesDotProduct() {

	// Array to store all tasks to execute
	List<DotProductTask> tasks = new ArrayList<DotProductTask>();

	// Randomly initialize arrays
	int N = 3_000_000; // array size
	int[] x  = fillIntArray(N);
	int[] y  = fillIntArray(N);
	shuffle(x);
	shuffle(y);

	// Create the list of tasks (Futures) to execute
	for (int i = 0; i < N; i++)
	    tasks.add(new DotProductTask(x,y,i));

	// Initialize a thread pool to execute the Futures
	ExecutorService pool = Executors.newFixedThreadPool(16);

	// Variable to store final result
	int result = 0;

	try {
	    // Add all futures to the execution pool at once
	    List<Future<Integer>> futures = pool.invokeAll(tasks);
	    for(Future<Integer> f : futures) {
		result += f.get(); // Wait for each future to be executed and add partial result
	    }
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	pool.shutdown(); // We are sure to be done, so we shut down the pool

	// Print result
	System.out.println("Futures results: " + result);	
	// Sanity check
	System.out.println("Correct result? " + (dotProductSequential(x,y) == result));

    }




    // ============= Dot Product Task class =============
    public class DotProductTask implements Callable<Integer> {
	final int pos;
	final int[] x, y;

	public DotProductTask(int[] x, int[] y, int pos) {
	    this.x   = x;
	    this.y   = y;
	    this.pos = pos;
	}

	@Override
	public Integer call() {
	    return x[pos] * y[pos];
	}
    }

    



    // ============= Utils =============
    public static int[] fillIntArray(int n) {
	int [] arr = new int[n];
	for (int i = 0; i < n; i++)
	    arr[i] = i;
	return arr;
    }

    private static final java.util.Random rnd = new java.util.Random();

    public static void shuffle(int[] arr) {
	for (int i = arr.length-1; i > 0; i--)
	    swap(arr, i, rnd.nextInt(i+1));
    }

    public static void swap(int[] arr, int s, int t) {
	int tmp = arr[s];  arr[s] = arr[t];  arr[t] = tmp;
    }

    private static Integer dotProductSequential(int[] x, int[] y) {
	int result = 0;
	for (int i = 0; i < x.length; i++) {
	    result += x[i]*y[i];
	}
	return result;
    }

}

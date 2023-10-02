package exercises05;
// jst@itu.dk * 2023-09-05

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import benchmarking.Benchmark;

// NOTE: cannot run it from vscode!
// use the following command inside the /weeko5exrecises folder
// gradle run -PmainClass=exercises05.TestTimeSearch
public class TestTimeSearch {
  private static CyclicBarrier barrier;

  public static void main(String[] args) {
    // new TestTimeSearch();

    // new TestTimeSearch();
    // Benchmark.Mark7("TestTimeSearch performance, Mark7", i -> {
    //   new TestTimeSearch();
    //   return 2.0;
    // });

    // for (int i = 1; i < 11; i++) {
    //   final String filename = "src/main/resources/long-text-file.txt";
    //   final String target = "ipsum";
  
    //   final LongCounter lc = new LongCounter(); 
    //   String[] lineArray = readWords(filename);
  
    //   System.out.println("Thread count: " + i + " target: " + target + " count: " + countParallelN(target, lineArray, i, lc));
    // }


    // Mark 7 timing
    for (var i = 0; i < 11; i++) {
      if (i == 0) {
        Benchmark.Mark7("Sequential", k -> {
          new TestTimeSearch();
          return 2.0;
        });
      } else {
        final var threadCount = i;
        Benchmark.Mark7(threadCount + " threads", k -> {
          final String filename = "src/main/resources/long-text-file.txt";
          final String target = "ipsum";
      
          final LongCounter lc = new LongCounter(); 
          String[] lineArray = readWords(filename);
      
          return countParallelN(target, lineArray, threadCount, lc);
        });
      }
    }
  }

  private static long countParallelN(String target, String[] lineArray, int N, LongCounter lc) {
    barrier = new CyclicBarrier(N + 1, null);

    final var splits = evenlysplitInt(lineArray.length, N);

    for (int i = 0; i < N; i++) {
      var fromAndTo = splits.get(i);

      new Thread(() -> {
        try {
          barrier.await();
          search(target, lineArray, fromAndTo.get(0), fromAndTo.get(1), lc);
          barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
          e.printStackTrace();
        }
      }).start();
    }
    
    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
    
    return lc.get();
  }

  private static LinkedList<LinkedList<Integer>> evenlysplitInt(int count, int splitCount) {
    var splits = new LinkedList<LinkedList<Integer>>();
    var split = (int) (Math.ceil(count / splitCount));
    var from = 0;
    var to = split;

    for (int i = 0; i < splitCount; i++) {
      final var fromToList = new LinkedList<Integer>();

      fromToList.add(from);
      fromToList.add(i == splitCount - 1 ? count : to);
      splits.add(fromToList);

      from = to;
      to += split;
    }
    
    return splits;
  }

  public TestTimeSearch() {
    final String filename = "src/main/resources/long-text-file.txt";
    final String target = "ipsum";

    final LongCounter lc = new LongCounter(); // name is abit misleading, it is just a counter
    String[] lineArray = readWords(filename);

    // System.out.println("Array Size: " + lineArray.length);
    // System.out.println("# Occurences of " + target + " :" + search(target, lineArray, 0, lineArray.length, lc));
        // System.out.println("Thread count: " + "sequential" + " target: " + target + " count: " + search(target, lineArray, 0, lineArray.length, lc));
  }

  static long search(String x, String[] lineArray, int from, int to, LongCounter lc) {
    // Search each line of file
    for (int i = from; i < to; i++)
      lc.add(linearSearch(x, lineArray[i]));
    // System.out.println("Found: "+lc.get());
    return lc.get();
  }

  static int linearSearch(String x, String line) {
    // Search for occurences of c in line
    String[] arr = line.split(" ");
    int count = 0;
    for (int i = 0; i < arr.length; i++)
      if ((arr[i].equals(x)))
        count++;
    return count;
  }

  public static String[] readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines().toArray(String[]::new); // will be explained in Week07;
    } catch (IOException exn) {
      exn.printStackTrace();
      return null;
    }
  }
}

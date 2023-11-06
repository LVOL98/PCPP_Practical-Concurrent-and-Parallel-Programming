package exercises10;

import java.util.Arrays;
import java.util.stream.IntStream;

public class E10_3 {
    public static void main(String[] args) { // TODO: it seems to run all of them in parallel? try running it
        final var range = 20;

        // System.out.println("Sequential - range: " + range);
        // SequentialStreamPrintThreadId(range);
        // System.out.println("Parallel - range: " + range);
        // ParallelStreamPrintThreadId(range);

        System.out.println("Sequential - range: " + range);
        SequentialStreamPrintThreadIdSlow(range);
        System.out.println("Parallel - range: " + range);
        ParallelStreamPrintThreadIdSlow(range);
    }

    private static void SequentialStreamPrintThreadId(int range) {
        IntStream.range(0, range)
                .forEach(i -> System.out.println(i + " - " + Thread.currentThread().getName()));
    }

    private static void ParallelStreamPrintThreadId(int range) {
        IntStream.range(0, range)
                .parallel()
                .forEach(i -> System.out.println(i + " - " + Thread.currentThread().getName()));
    }

    private static void SequentialStreamPrintThreadIdSlow(int range) {
        IntStream.range(0, range)
                .filter(i -> {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return true;
                })
                .forEach(i -> System.out.println(i + " - " + Thread.currentThread().getName()));
    }

    private static void ParallelStreamPrintThreadIdSlow(int range) {
        IntStream.range(0, range)
                .parallel()
                .filter(i -> {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return true;
                })
                .forEach(i -> System.out.println(i + " - " + Thread.currentThread().getName()));
    }
}
